package ch.cern.atlas.apvs.server;

import java.io.IOException;

import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.PingAction;
import org.asteriskjava.manager.response.ManagerResponse;
import org.asteriskjava.manager.response.PingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsteriskPing {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private PingAction ping;
	private ManagerConnection serverConnection;
	private final static int TIMEOUT = 3000;

	public AsteriskPing(ManagerConnection managerConnection) {
		this.serverConnection = managerConnection;
		ping = new PingAction();
	}

	public boolean isAlive() {
		ManagerResponse response = new ManagerResponse();
		try {
			response = this.serverConnection.sendAction(ping, TIMEOUT);

		} catch (IllegalArgumentException e1) {
			log.error("Exception thrown in polling server: ",
					e1);
		} catch (IllegalStateException e1) {
			log.error("Exception thrown in polling server: ",
					e1);
		} catch (IOException e1) {
			log.error("Exception thrown in polling server: ",
					e1);
		} catch (TimeoutException e1) {
			log.error("Exception thrown in polling server: ",
					e1);
		}
		if (response instanceof PingResponse) {
			return true;
		} else {
			return false;
		}
	}

}
