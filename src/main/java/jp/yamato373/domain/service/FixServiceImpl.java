package jp.yamato373.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.fix.OrderSender;
import jp.yamato373.domain.model.fix.Subscription;
import quickfix.SessionID;

@Service
public class FixServiceImpl implements FixService {

	@Autowired
	OrderSender orderSender;

	@Autowired
	Subscription subscription;

	@Override
	public void orderSenderStart(SessionID sessionID) {
		orderSender.start(sessionID);
	}

	@Override
	public void subscriptionStart(SessionID sessionID) {
		subscription.start(sessionID);
	}

	@Override
	public String getMDReqID() {
		return subscription.getMDReqID();
	}

	@Override
	public void updataLastReceivetime() {
		subscription.updataLastReceivetime();
	}

	@Override
	public void unsubscribe() {
		subscription.unsubscribe();
	}
}
