package org.eclipse.epsilon.emc.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.json.simple.JSONObject;

/**
 * Thin wrapper over a {@link JSONObject} which adds the concept of a container to its
 * values.
 */
public class JsonModelObject implements Contained, Map<String, Object> {
	// Underlying map is the same, but we can intercept all calls
	private JSONObject object = new JSONObject();
	private Object container;

	@Override
	public Object getContainer() {
		return container;
	}

	@Override
	public void setContainer(Object container) {
		this.container = container;
	}

	@Override
	public int size() {
		return object.size();
	}

	@Override
	public boolean isEmpty() {
		return object.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return object.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return object.containsKey(value);
	}

	@Override
	public Object get(Object key) {
		return object.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object put(String key, Object value) {
		Object ret = object.put(key, value);

		if (value instanceof Contained) {
			((Contained) value).setContainer(this);
		}
		if (ret instanceof Contained) {
			((Contained) ret).setContainer(null);
		}

		return ret;
	}

	@Override
	public Object remove(Object key) {
		Object ret = object.remove(key);
		if (ret instanceof Contained) {
			((Contained) ret).setContainer(null);
		}
		return ret;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (Entry<? extends String, ? extends Object> e : m.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	@Override
	public void clear() {
		for (Object e : object.values()) {
			if (e instanceof Contained) {
				((Contained) e).setContainer(null);
			}
		}
		object.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(object.keySet());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Object> values() {
		return Collections.unmodifiableCollection(object.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return Collections.unmodifiableSet(object.entrySet());
	}

	@Override
	public int hashCode() {
		return Objects.hash(object);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		return object.equals(obj);
	}

	@Override
	public String toString() {
		return "JsonModelObject [object=" + object + ", container=" + container + "]";
	}

}
