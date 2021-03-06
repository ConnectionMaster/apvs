package ch.cern.atlas.apvs.dosimeter.shared;

import java.util.List;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

// FIXME no longer needed
public class DosimeterSerialNumbersChangedEvent extends RemoteEvent<DosimeterSerialNumbersChangedEvent.Handler> {

	private static final long serialVersionUID = -3506909579723676949L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onDosimeterSerialNumbersChanged(DosimeterSerialNumbersChangedEvent event);
	}

	public static final Type<DosimeterSerialNumbersChangedEvent.Handler> TYPE = new Type<DosimeterSerialNumbersChangedEvent.Handler>();

	/**
	 * Register a handler for events on the eventbus.
	 * 
	 * @param eventBus
	 *            the {@link EventBus}
	 * @param handler
	 *            an Handler instance
	 * @return an {@link HandlerRegistration} instance
	 */
	public static HandlerRegistration register(EventBus eventBus,
			DosimeterSerialNumbersChangedEvent.Handler handler) {
		return ((RemoteEventBus)eventBus).addHandler(TYPE, handler);
	}
	
	public static HandlerRegistration subscribe(EventBus eventBus, Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		((RemoteEventBus)eventBus).fireEvent(new RequestRemoteEvent(DosimeterSerialNumbersChangedEvent.class));
		
		return registration;
	}
	
	private List<String> dosimeterSerialNumbers;
	
	public DosimeterSerialNumbersChangedEvent() {
	}

	public DosimeterSerialNumbersChangedEvent(List<String> dosimeterSerialNumbers) {
		this.dosimeterSerialNumbers = dosimeterSerialNumbers;
	}

	@Override
	public Type<DosimeterSerialNumbersChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public List<String> getDosimeterSerialNumbers() {
		return dosimeterSerialNumbers;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onDosimeterSerialNumbersChanged(this);
	}
	
	@Override
	public String toString() {
		return "DosimeterSerialNumbersChangedEvent "+dosimeterSerialNumbers.size();
	}
}
