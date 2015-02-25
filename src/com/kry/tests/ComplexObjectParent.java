package com.kry.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ComplexObjectParent {
	private final Object someObject = new String("42");
	public Map<String, Object> map;
	float someFloat = 1.337f;
	
	protected static boolean equalsOrReferenceToObject(Object field1, Object field2,
			Object object1, Object object2) {
		if (field1 == null) {
			if (field2 != null) return false;
		} else {
			if (!(field1 == object1 ? field2 == object2 : field1.equals(field2))) return false;
		}
		return true;
	}
	
	protected ComplexObjectParent() {
		map = new HashMap<>();
		map.put("42", someObject);
	}
	
	@SuppressWarnings("unchecked")
	protected ComplexObjectParent(ComplexObjectParent aObj) {
		map = (Map<String, Object>) ((HashMap<String, Object>) aObj.map).clone();
		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			if (entry.getValue() == aObj) {
				entry.setValue(this);
			} else if (entry.getValue() == aObj.someObject) {
				entry.setValue(someObject);
			}
		}
		someFloat = aObj.someFloat;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ComplexObjectParent other = (ComplexObjectParent) obj;
		if (someObject == null) {
			if (other.someObject != null) return false;
		} else if (!someObject.equals(other.someObject)) return false;
		if (map == null) {
			if (other.map != null) return false;
		} else {
			if (map.size() != other.map.size()) return false;
			for (String key : map.keySet()) {
				Object check = map.get(key);
				Object otherCheck = other.map.get(key);
				if (!equalsOrReferenceToObject(check, otherCheck, this, other)) return false;
			}
		}
		if (someFloat != other.someFloat) return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(someFloat);
		result = prime * result + ((someObject == null) ? 0 : someObject.hashCode());
		return result;
	}
}
