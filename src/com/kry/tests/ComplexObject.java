package com.kry.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Very complex object
 * <p>
 * <i>Warning: for the test using only!</i>
 */
public class ComplexObject extends ComplexObjectParent implements Cloneable {
	
	public final ComplexObject self;
	
	Object[] objectArray;
	
	public List<Object[]> listOfObjectArrays;
	
	private final List<String> list;
	
	int somePrimitive;
	
	protected Integer someWrapped = 42;
	
	public List<int[]> listOfIntArrays;
	
	public ComplexObject() {
		self = this;
		
		objectArray = new ComplexObject[2];
		Arrays.fill(objectArray, self);
		
		listOfObjectArrays = new ArrayList<>(1);
		listOfObjectArrays.add(objectArray);
		
		list = new LinkedList<>();
		list.add("42");
		list.add(new String("forty two"));
		
		somePrimitive = 42 >> 1;
		
		int[] intArray = { somePrimitive, someWrapped };
		listOfIntArrays = new ArrayList<>(Arrays.asList(intArray));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ComplexObject(ComplexObject aObj) {
		super(aObj);
		self = this;
		
		objectArray = aObj.objectArray.clone();
		for (int i = 0; i < objectArray.length; i++) {
			if (objectArray[i] == aObj.self) {
				objectArray[i] = self;
			}
		}
		
		listOfObjectArrays = (List) ((ArrayList) aObj.listOfObjectArrays).clone();
		for (int i = 0; i < listOfObjectArrays.size(); i++) {
			if (listOfObjectArrays.get(i) == aObj.objectArray) {
				listOfObjectArrays.set(i, objectArray);
			} else {
				Object[] item = Arrays.copyOf(listOfObjectArrays.get(i),
						listOfObjectArrays.get(i).length);
				for (int j = 0; j < item.length; j++) {
					if (item[j] == aObj.self) {
						item[j] = self;
					}
				}
				listOfObjectArrays.set(i, item);
			}
		}
		
		list = (List) ((LinkedList) aObj.list).clone();
		
		somePrimitive = aObj.somePrimitive;
		someWrapped = aObj.someWrapped;
		
		listOfIntArrays = (List) ((ArrayList) aObj.listOfIntArrays).clone();
		for (int i = 0; i < listOfIntArrays.size(); i++) {
			listOfIntArrays.set(i, listOfIntArrays.get(i).clone());
		}
	}
	
	@Override
	public ComplexObject clone() {
		return new ComplexObject(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		ComplexObject other = (ComplexObject) obj;
		if (list == null) {
			if (other.list != null) return false;
		} else if (!list.equals(other.list)) return false;
		if (listOfIntArrays == null) {
			if (other.listOfIntArrays != null) return false;
		} else {
			if (listOfIntArrays.size() != other.listOfIntArrays.size()) return false;
			for (int i = 0; i < listOfIntArrays.size(); i++) {
				if (!Arrays.equals(listOfIntArrays.get(i), other.listOfIntArrays.get(i)))
					return false;
			}
		}
		if (!equalsOrReferenceToObject(self, other.self, this, other)) return false;
		if (objectArray == null) {
			if (other.objectArray != null) return false;
		} else {
			if (objectArray.length != other.objectArray.length) return false;
			for (int j = 0; j < objectArray.length; j++) {
				Object check = objectArray[j];
				Object otherCheck = other.objectArray[j];
				if (!equalsOrReferenceToObject(check, otherCheck, this, other)) return false;
			}
		}
		if (listOfObjectArrays == null) {
			if (other.listOfObjectArrays != null) return false;
		} else {
			if (listOfObjectArrays.size() != other.listOfObjectArrays.size()) return false;
			for (int i = 0; i < listOfObjectArrays.size(); i++) {
				Object[] checkArray = listOfObjectArrays.get(i);
				Object[] otherCheckArray = other.listOfObjectArrays.get(i);
				if (checkArray == null) {
					if (otherCheckArray != null) return false;
				} else {
					if (checkArray.length != otherCheckArray.length) return false;
					if (checkArray == objectArray) {
						if (otherCheckArray != other.objectArray) return false;
					}
					for (int j = 0; j < checkArray.length; j++) {
						Object check = checkArray[j];
						Object otherCheck = otherCheckArray[j];
						if (!equalsOrReferenceToObject(check, otherCheck, this, other))
							return false;
					}
					
				}
			}
		}
		if (somePrimitive != other.somePrimitive) return false;
		if (someWrapped == null) {
			if (other.someWrapped != null) return false;
		} else if (!someWrapped.equals(other.someWrapped)) return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (list == null ? 0 : list.hashCode());
		result = prime * result + (listOfIntArrays == null ? 0 : listOfIntArrays.hashCode());
		result = prime * result + somePrimitive;
		result = prime * result + someWrapped;
		return result;
	}
	
	public void toComplicate() {
		objectArray = new String[3];
		objectArray[0] = "Be";
		objectArray[1] = new String(" more ");
		objectArray[2] = "complex";
		
		someWrapped = new Integer(24);
		map.put(objectArray[2].toString(), self);
	}
	
}
