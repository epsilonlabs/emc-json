package org.eclipse.epsilon.emc.json;

/**
 * Interface for something that has a container.
 */
public interface Contained {

	Object getContainer();
	void setContainer(Object container);

	default boolean isContainedBy(Object element) {
		for (Object ancestor = this;
			ancestor != null;
			ancestor = ancestor instanceof Contained ? ((Contained) ancestor).getContainer() : null
		) {
			if (ancestor == element) {
				return true;
			}
		}

		return false;
	}

}
