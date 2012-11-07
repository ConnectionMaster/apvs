package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.HandlerRegistration;

public class AudioSettingsChangedEvent extends
		RemoteEvent<AudioSettingsChangedEvent.Handler> {

	private static final long serialVersionUID = 1L;

	public interface Handler {

		void onAudioSettingsChanged(AudioSettingsChangedEvent event);
	}

	private static final Type<AudioSettingsChangedEvent.Handler> TYPE = new Type<AudioSettingsChangedEvent.Handler>();

	public static HandlerRegistration register(RemoteEventBus eventBus,
			AudioSettingsChangedEvent.Handler handler) {

		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			AudioSettingsChangedEvent.Handler handler) {

		HandlerRegistration registration = register(eventBus, handler);
		eventBus.fireEvent(new RequestRemoteEvent(AudioSettingsChangedEvent.class));

		return registration;
	}

	private AudioSettings voipAccounts;

	public AudioSettingsChangedEvent() {
	}

	public AudioSettingsChangedEvent(AudioSettings voipAccount) {
		this.voipAccounts = voipAccount;
	}

	public AudioSettings getAudioSettings() {
		return voipAccounts;
	}

	@Override
	public Type<AudioSettingsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onAudioSettingsChanged(this);
	}

}
