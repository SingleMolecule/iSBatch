/*
 * 
 */
package context;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ContextHandler.
 */
public class ContextHandler {
	
	/** The listeners. */
	private ArrayList<ContextListener> listeners = new ArrayList<ContextListener>();
	
	/** The context element. */
	private ContextElement contextElement;

	/**
	 * Gets the listeners.
	 *
	 * @return the listeners
	 */
	public ArrayList<ContextListener> getListeners() {
		return listeners;
	}

	/**
	 * Gets the context element.
	 *
	 * @return the context element
	 */
	public ContextElement getContextElement() {
		return contextElement;
	}

	/**
	 * Sets the context.
	 *
	 * @param contextElement the new context
	 */
	public void setContext(ContextElement contextElement) {
		
		if (contextElement != this.contextElement) {
			this.contextElement = contextElement;
			
			for (ContextListener listener: listeners)
				listener.contextChanged(contextElement);
		}
		
	}

}
