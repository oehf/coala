package exceptions;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class JSFExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler wrapped;

	public JSFExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {

		Iterator<ExceptionQueuedEvent> itr = getUnhandledExceptionQueuedEvents()
				.iterator();
		while (itr.hasNext()) {

			ExceptionQueuedEvent event = itr.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event
					.getSource();
			Throwable thr = context.getException();

			if (thr instanceof FacesException) {
				FacesContext fc = FacesContext.getCurrentInstance();
				NavigationHandler nav = fc.getApplication()
						.getNavigationHandler();
				try {
					fc.addMessage(
							null,
							new FacesMessage(
									"Server not available"));
					nav.handleNavigation(fc, null, "/login.xhtml");
					fc.renderResponse();
				} finally {
					itr.remove();
				}
			}
		}
		getWrapped().handle();
	}
}
