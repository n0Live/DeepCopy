package com.kry.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.kry.copyutils.CopyUtils;

public class DeepCopyTest {
	static ComplexObject original;
	static ComplexObject referenceClone;
	
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
		return ((original != clone) && original.equals(clone));
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
	public void testDeepCopyByCommonWayT() {
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
	public void testDeepCopyT() {
		ComplexObject testClone = CopyUtils.deepCopy(original);
		assertTrue("it's not equals to the original", compareComplexObjects(original, testClone));
		assertTrue("it's not equals to the referenceClone",
				compareComplexObjects(referenceClone, testClone));
		
		original.toComplicate();
		assertFalse("it's just a shallow copy", compareComplexObjects(original, testClone));
		
		testClone = CopyUtils.deepCopy(original);
		assertTrue("Something going wrong", compareComplexObjects(original, testClone));
		
	}
	
}
