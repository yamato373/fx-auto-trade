package jp.yamato373.domain.service.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.fix.OrderSender;
import jp.yamato373.domain.model.fix.Subscription;
import quickfix.SessionID;

@Service
public class FixService {

	@Autowired
	OrderSender orderSender;

	@Autowired
	Subscription subscription;

	public void orderSenderStart(SessionID sessionID) {
		orderSender.start(sessionID);
	}

	public void subscriptionStart(SessionID sessionID) {
		subscription.start(sessionID);
	}

	public String getMDReqID() {
		return subscription.getMDReqID();
	}

	public void updataLastReceivetime() {
		subscription.updataLastReceivetime();
	}

	public void unsubscribe() {
		subscription.unsubscribe();
	}
}
