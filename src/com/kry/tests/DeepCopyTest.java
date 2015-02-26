package com.kry.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import com.kry.copyutils.CopyUtils;

public class DeepCopyTest {
	ComplexObject original;
	ComplexObject referenceClone;
	
	/**
	 * Compare two {@code ComplexObject} objects
	 * 
	 * @param original
	 *            first object
	 * @param clone
	 *            second object
	 * @return {@code true} if the first isn't same the second one, but equals
	 *         to it; otherwise - {@code false}
	 */
	private static boolean compareComplexObjects(ComplexObject original, ComplexObject clone) {
		return original != clone && original.equals(clone);
	}
	
	@Before
	public void setUp() throws Exception {
		original = new ComplexObject();
		referenceClone = original.clone();
	}
	
	@Test
	public void testComplexObjectInnerCopyT() {
		assertTrue("it's not equals to the original",
		        compareComplexObjects(original, referenceClone));
		
		original.toComplicate();
		assertFalse("it's just a shallow copy", compareComplexObjects(original, referenceClone));
		
		referenceClone = original.clone();
		assertTrue("Something going wrong", compareComplexObjects(original, referenceClone));
	}
	
	@Test
	public void testDeepCopyByCommonWayT() throws ReflectiveOperationException {
		ComplexObject testClone = CopyUtils.deepCopyByCommonWay(original);
		assertTrue("it's not equals to the original", compareComplexObjects(original, testClone));
		assertTrue("it's not equals to the referenceClone",
		        compareComplexObjects(referenceClone, testClone));
		
		original.toComplicate();
		assertFalse("it's just a shallow copy", compareComplexObjects(original, testClone));
		
		testClone = CopyUtils.deepCopyByCommonWay(original);
		assertTrue("Something going wrong", compareComplexObjects(original, testClone));
	}
	
	@Test
	public void testDeepCopyT() throws ReflectiveOperationException {
		ComplexObject testClone = CopyUtils.deepCopy(original);
		assertTrue("it's not equals to the original", compareComplexObjects(original, testClone));
		assertTrue("it's not equals to the referenceClone",
		        compareComplexObjects(referenceClone, testClone));
		
		original.toComplicate();
		assertFalse("it's just a shallow copy", compareComplexObjects(original, testClone));
		
		testClone = CopyUtils.deepCopy(original);
		assertTrue("Something going wrong", compareComplexObjects(original, testClone));
	}
	
	@Test
	public void testDeepCopyWithStandartClassesT() throws ReflectiveOperationException {
		Object testClone;
		HashMap<String, Object> map = new HashMap<>(100);
		map.put("first", original);
		map.put("second", referenceClone);
		
		testClone = CopyUtils.deepCopy(map);
		assertTrue("HashMap not equals to the original", map.equals(testClone));
		assertTrue("HashMap not equals to the referenceClone", map.clone().equals(testClone));
		
		TreeSet<Object> set = new TreeSet<>();
		set.add("second");
		set.add("first");
		set.add("1st");
		
		testClone = CopyUtils.deepCopy(set);
		assertTrue("TreeSet not equals to the original", set.equals(testClone));
		assertTrue("TreeSet not equals to the referenceClone", set.clone().equals(testClone));
		
		Vector<Object> vector = new Vector<>();
		vector.addAll(map.values());
		vector.add(set);
		vector.add(new LinkedList<Object>(set));
		
		testClone = CopyUtils.deepCopy(vector);
		assertTrue("ArrayDeque not equals to the original", vector.equals(testClone));
		assertTrue("ArrayDeque not equals to the referenceClone", vector.clone().equals(testClone));
	}
	
}
