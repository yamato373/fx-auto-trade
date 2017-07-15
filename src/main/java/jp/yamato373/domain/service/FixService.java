package jp.yamato373.domain.service;

import quickfix.SessionID;

public interface FixService {

	void orderSenderStart(SessionID sessionID);

	void subscriptionStart(SessionID sessionID);

	String getMDReqID();

	void updataLastReceivetime();

	void unsubscribe();

}
