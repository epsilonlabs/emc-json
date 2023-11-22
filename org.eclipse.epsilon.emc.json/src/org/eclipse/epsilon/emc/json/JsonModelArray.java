package org.eclipse.epsilon.emc.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.json.simple.JSONArray;

/**
 * Thin wrapper over a {@link JSONArray} which adds the concept of a container.
 */
public class JsonModelArray implements List<Object>, Contained {

	private JSONArray array = new JSONArray();
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
		return array.size();
	}

	@Override
	public boolean isEmpty() {
		return array.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return array.contains(o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Object> iterator() {
		return Collections.unmodifiableList(array).iterator();
	}

	@Override
	public Object[] toArray() {
		return array.toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) array.toArray(a);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(Object e) {
		boolean ret = array.add(e);
		if (ret && e instanceof Contained) {
			((Contained) e).setContainer(this);
		}
		return ret;
	}

	@Override
	public boolean remove(Object o) {
		boolean ret = array.remove(o);
		if (ret && o instanceof Contained) {
			((Contained) o).setContainer(null);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsAll(Collection<?> c) {
		return array.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		boolean ret = false;
		for (Object o : c) {
			ret = add(o) || ret;
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		for (Object o : c) {
			if (o instanceof Contained) {
				((Contained) o).setContainer(this);
			}
		}
		return array.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean anyRemoved = false;
		for (Object o : c) {
			if (array.remove(o)) {
				anyRemoved = true;
				if (o instanceof Contained) {
					((Contained) o).setContainer(null);
				}
			}
		}
		return anyRemoved;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean anyRemoved = false;
		for (Iterator<Object> it = array.iterator(); it.hasNext();) {
			Object o = it.next();
			if (!c.contains(o)) {
				it.remove();
				anyRemoved = true;

				if (o instanceof Contained) {
					((Contained) o).setContainer(null);
				}
			}
		}

		return anyRemoved;
	}

	@Override
	public void clear() {
		for (Object o : array) {
			if (o instanceof Contained) {
				((Contained) o).setContainer(null);
			}
		}
		array.clear();
	}

	@Override
	public Object get(int index) {
		return array.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object set(int index, Object newValue) {
		final Object oldValue = array.set(index, newValue);
		if (oldValue instanceof Contained) {
			((Contained) oldValue).setContainer(null);
		}
		if (newValue instanceof Contained) {
			((Contained) newValue).setContainer(this);
		}
		return oldValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(int index, Object element) {
		if (element instanceof Contained) {
			((Contained) element).setContainer(this);
		}
		array.add(index, element);
	}

	@Override
	public Object remove(int index) {
		final Object removed = array.remove(index);
		if (removed instanceof Contained) {
			((Contained) removed).setContainer(null);
		}
		return removed;
	}

	@Override
	public int indexOf(Object o) {
		return array.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return array.lastIndexOf(o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListIterator<Object> listIterator() {
		return Collections.unmodifiableList(array).listIterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListIterator<Object> listIterator(int index) {
		return Collections.unmodifiableList(array).listIterator(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		return Collections.unmodifiableList(array).subList(fromIndex, toIndex);
	}

	@Override
	public int hashCode() {
		return Objects.hash(array);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		return array.equals(obj);
	}

	@Override
	public String toString() {
		return "JsonModelArray [array=" + array + ", container=" + container + "]";
	}
	
}
