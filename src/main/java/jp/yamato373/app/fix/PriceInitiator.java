package jp.yamato373.app.fix;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jp.yamato373.domain.service.shared.FixService;
import jp.yamato373.domain.service.shared.RateService;
import lombok.extern.slf4j.Slf4j;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SLF4JLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

@Component
@Slf4j
public class PriceInitiator {

	@Autowired
	RateService rateService;

	@Autowired
	FixService fixService;

	PriceApplication priceApplication;

	SessionSettings sessionSettings;

	private boolean initiatorStarted = false;
	private Initiator initiator = null;

	@Autowired
	public PriceInitiator(PriceApplication priceApplication) throws ConfigError {
		sessionSettings = new SessionSettings("price.cfg");
		this.priceApplication = priceApplication;
	}

	@PostConstruct
	public void init() {
		try {
			MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
			LogFactory logFactory = new SLF4JLogFactory(sessionSettings);
			MessageFactory messageFactory = new DefaultMessageFactory();

			initiator = new SocketInitiator(priceApplication, messageStoreFactory, sessionSettings, logFactory,
					messageFactory);

			logon();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@PreDestroy
	public void stop() {
		if (!StringUtils.isEmpty(fixService.getMDReqID())) {
			fixService.unsubscribe();
		}
		rateService.clearRate();
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
