package jp.yamato373.fix.model;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

@Component
@Slf4j
public class OrderInitiator {

	private boolean initiatorStarted = false;
	private Initiator initiator = null;

	OrderApplication orderApplication;

	SessionSettings sessionSettings;

	@Autowired
	public OrderInitiator(OrderApplication orderApplication) throws ConfigError {
		sessionSettings = new SessionSettings("config/order.cfg");
		this.orderApplication = orderApplication;
	}

	@PostConstruct
	public void init() {
		try {
			MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
			LogFactory logFactory = new ScreenLogFactory(true, true, true);
			MessageFactory messageFactory = new DefaultMessageFactory();

			initiator = new SocketInitiator(orderApplication, messageStoreFactory, sessionSettings, logFactory,
					messageFactory);

			logon();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@PreDestroy
	public void stop() {
		logout();
	}

	public synchronized void logon() {
		if (!initiatorStarted) {
			try {
				initiator.start();
				initiatorStarted = true;
			} catch (Exception e) {
				log.error("Logon failed", e);
			}
		} else {
			for (SessionID sessionId : initiator.getSessions()) {
				Session.lookupSession(sessionId).logon();
			}
		}
	}

	public void logout() {
		for (SessionID sessionId : initiator.getSessions()) {
			Session.lookupSession(sessionId).logout("user requested");
		}
	}
}
