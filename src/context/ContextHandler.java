package context;

import java.util.ArrayList;

public class ContextHandler {
	private ArrayList<ContextListener> listeners = new ArrayList<ContextListener>();
	
	private ContextElement contextElement;
	public ArrayList<ContextListener> getListeners() {
		return listeners;
	}
	public ContextElement getContextElement() {
		return contextElement;
	}

	public void setContext(ContextElement contextElement) {
		
		if (contextElement != this.contextElement) {
			this.contextElement = contextElement;
			
			for (ContextListener listener: listeners)
				listener.contextChanged(contextElement);
		}
		
	}

}
