/*
 * 
 */
package context;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving context events.
 * The class that is interested in processing a context
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addContextListener<code> method. When
 * the context event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ContextEvent
 */
public interface ContextListener {
	
	/**
	 * Context changed.
	 *
	 * @param contextElement the context element
	 */
	public void contextChanged(ContextElement contextElement);
}
