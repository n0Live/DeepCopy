package com.kry.copyutils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class CopyUtils {
	/**
	 * Map for mapping object references between <i>original</i> and
	 * <i>clone</i>
	 */
	private static Map<Object, Object> references;
	
	/**
	 * Map which contains a default wrapped values for a primitive types
	 */
	private final static Map<Class<?>, Object> primitiveWrappersMap = new HashMap<Class<?>, Object>();
	static {
		primitiveWrappersMap.put(boolean.class, new Boolean(false));
		primitiveWrappersMap.put(byte.class, new Byte((byte) 0));
		primitiveWrappersMap.put(short.class, new Short((short) 0));
		primitiveWrappersMap.put(char.class, new Character((char) 0));
		primitiveWrappersMap.put(int.class, new Integer(0));
		primitiveWrappersMap.put(long.class, new Long(0l));
		primitiveWrappersMap.put(float.class, new Float(0f));
		primitiveWrappersMap.put(double.class, new Double(0));
	}
	
	/**
	 * Add object references between <i>original</i> and <i>clone</i> to the
	 * {@link #references} map.
	 * 
	 * @param original
	 *            original object
	 * @param copy
	 *            clone object
	 */
	private static void addToReferencesMap(Object original, Object copy) {
		references.put(original, copy);
	}
	
	/**
	 * Constructs a new object instance using the constructor of a given class
	 * 
	 * @param clazz
	 *            class of a new object
	 * @return a new object instance of a given class
	 * @throws ReflectiveOperationException
	 */
	private static Object constractNewObject(Class<?> clazz) throws ReflectiveOperationException {
		Object newObject = null;
		
		// try to use default constructor
		try {
			newObject = clazz.newInstance();
			return newObject;
		} catch (ReflectiveOperationException e) {
			// do nothing
		}
		
		// gets all the declared constructors and try to use them in turn
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		if (constructors.length > 0) {
			for (Constructor<?> constructor : constructors) {
				Class<?>[] parameters = constructor.getParameterTypes();
				Object[] args = new Object[parameters.length];
				
				for (int i = 0; i < parameters.length; i++) {
					// null or a primitive defaults
					args[i] = (parameters[i].isPrimitive()) ? getPrimitiveDefault(parameters[i])
							: null;
				}
				
				try {
					// for using private constructors
					constructor.setAccessible(true);
					newObject = constructor.newInstance(args);
					if (newObject != null) return newObject;
				} catch (ReflectiveOperationException e) {
					// go to next constructor
					continue;
				}
			}
		}
		return newObject;
	}
	
	/**
	 * Create a copy of the given array
	 * 
	 * @param array
	 *            array for copying
	 * @return copy of the given array
	 */
	private static Object copyArray(Object array) {
		Class<?> arrayType = array.getClass().getComponentType();
		int length = Array.getLength(array);
		Object arrayCopy = Array.newInstance(arrayType, length);
		
		// arrays is the objects too
		addToReferencesMap(array, arrayCopy);
		
		for (int i = 0; i < length; i++) {
			Object value = Array.get(array, i);
			Object cloneValue = getClone(value, arrayType);
			Array.set(arrayCopy, i, cloneValue);
		}
		return arrayCopy;
	}
	
	/**
	 * Deep copy the values from the given {@code fields} of the {@code fromObj}
	 * to the {@code toObj}.
	 * 
	 * @param fields
	 *            array of the fields declared by the class of the
	 *            {@code fromObj}
	 * @param fromObj
	 *            source for reading values
	 * @param toObj
	 *            destination for writing values
	 * @throws ReflectiveOperationException
	 */
	private static <T> void copyFieldValues(Field[] fields, T fromObj, T toObj)
			throws ReflectiveOperationException {
		Object value;
		Object cloneValue;
		for (Field field : fields) {
			// for settings private fields
			field.setAccessible(true);
			Class<?> fieldClazz = field.getType();
			
			value = field.get(fromObj);
			cloneValue = getClone(value, fieldClazz);
			field.set(toObj, cloneValue);
		}
	}
	
	/**
	 * Create a deep copy of the given {@code obj} by using reflections
	 * 
	 * @param obj
	 *            object for copying
	 * @return a deep copy of the given object
	 */
	public static <T> T deepCopy(T obj) {
		try {
			// for an external call create a new references map
			references = new HashMap<>();
			return deepCopy(obj, null);
		} finally {
			references.clear();
		}
	}
	
	/**
	 * The entry point to the real {@code deepCopy} method
	 * <p>
	 * For the internal use only.
	 * 
	 * @param obj
	 *            object for copying
	 * @param recursiveCall
	 *            must by {@code null} - use for overloading
	 * @return a deep copy of the given object
	 */
	@SuppressWarnings("unchecked")
	private static <T> T deepCopy(T obj, Object recursiveCall) {
		if (obj == null) return null;
		Class<T> clazz = (Class<T>) obj.getClass();
		try {
			T copy = (T) constractNewObject(clazz);
			
			addToReferencesMap(obj, copy);
			
			Field[] fields = getFields(clazz);
			copyFieldValues(fields, obj, copy);
			return copy;
		} catch (ReflectiveOperationException e) {
			Logger.getAnonymousLogger().info("deepCopy() was failed");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Try to create a deep copy of the given {@code obj} by the following ways:<br>
	 * - in first, by calling clone() method,<br>
	 * - in second, try to use a copy constructor<br>
	 * - and, lastly, by calling deepCopy(obj)
	 * 
	 * @param obj
	 *            object for copying
	 * @return a deep copy of the given object
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T> T deepCopyByCommonWay(T obj) {
		Class<?> clazz = obj.getClass();
		// Try to use a clone() method
		if (obj instanceof Cloneable) {
			for (Method m : clazz.getMethods())
				// find appropriate clone(), takes no parameters, and
				// returns type instance of the obj
				if (m.getName().equals("clone") && m.getParameterTypes().length == 0
						&& m.getReturnType().isInstance(obj)) {
					try {
						T copy = (T) m.invoke(obj);
						return copy;
					} catch (ReflectiveOperationException e) {
						Logger.getAnonymousLogger().info("Try to use a clone() is fail");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
		}
		// Try to use a copy constructor
		Class<?>[] paramTypes = new Class[] { clazz };
		Constructor<T> constructor;
		try {
			constructor = (Constructor<T>) clazz.getDeclaredConstructor(paramTypes);
			T copy = constructor.newInstance(obj);
			return copy;
		} catch (ReflectiveOperationException e) {
			Logger.getAnonymousLogger().info("Try to use a copy constructor is fail");
		}
		return deepCopy(obj);
	}
	
	/**
	 * Returns a clone of the given object
	 * 
	 * @param original
	 *            object for copying
	 * @param clazz
	 *            class of the {@code original} object
	 * @return a deep copy of the given object
	 */
	public static Object getClone(Object original, Class<?> clazz) {
		if (original == null) return null;
		
		Object cloneValue = null;
		
		if (!clazz.isPrimitive()) {
			// for objects - trying to give value from the references map
			cloneValue = getFromReferencesMap(original);
		}
		
		if (cloneValue == null) {
			// workaround for suppressing calling of a Wrappers
			Class<?> valueType = clazz.isPrimitive() ? clazz : original.getClass();
			if (valueType.isArray()) {
				cloneValue = copyArray(original);
			} else if (valueType.isPrimitive()) {
				cloneValue = original;
			} else {
				cloneValue = deepCopy(original, null);
			}
		}
		return cloneValue;
	}
	
	/**
	 * Returns an array of {@code Field} objects reflecting all the fields
	 * declared by the given {@code class}
	 * 
	 * @param clazz
	 *            {@code Class} object
	 * @return array of {@code Field} objects
	 */
	private static Field[] getFields(Class<?> clazz) {
		List<Field> result = new LinkedList<>();
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// static fields are not needed
				if (!Modifier.isStatic(field.getModifiers())) {
					result.add(field);
				}
			}
			// get up to the Superclass
			clazz = clazz.getSuperclass();
		}
		return result.toArray(new Field[result.size()]);
	}
	
	/**
	 * Returns the object referenced by the given {@code obj}, or null if the
	 * references map contains no mapping for the {@code obj}.
	 * 
	 * @param obj
	 *            the key object
	 * @return referenced object
	 */
	private static Object getFromReferencesMap(Object obj) {
		return references.get(obj);
	}
	
	/**
	 * Returns the wrapper object of a given primitive {@code Class} with the
	 * default value
	 * 
	 * @param primitiveClazz
	 *            primitive {@code Class}
	 * @return the wrapper object of a primitive with the default value
	 */
	private static Object getPrimitiveDefault(Class<?> primitiveClazz) {
		return primitiveWrappersMap.get(primitiveClazz);
		
	}
	
	private CopyUtils() {
	}
	
}
