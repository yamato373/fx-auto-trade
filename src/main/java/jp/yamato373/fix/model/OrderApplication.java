package jp.yamato373.fix.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.yamato373.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;
import quickfix.field.Password;
import quickfix.fix44.MessageCracker;

@Component
@Slf4j
public class OrderApplication extends MessageCracker implements Application {

	private final String PASSWARD = "XXXXX";

	@Autowired
	OrderSender orderSender;

	@Autowired
	OrderService orderService;

	@Override
	public void onCreate(SessionID sessionID) {
	}

	@Override
	public void onLogon(SessionID sessionID) {
		log.info("オーダーセッションのログインしたよ！");
		orderSender.start(sessionID);
	}

	@Override
	public void onLogout(SessionID sessionID) {
		log.info("オーダーセッションのログアウトしたよ！");
	}

	@Override
	public void toAdmin(quickfix.Message message, SessionID sessionID) {

		try {
			if (MsgType.LOGON.equals(message.getHeader().getString(MsgType.FIELD))) {
				message.getHeader().setString(Password.FIELD, PASSWARD);
			}
		} catch (FieldNotFound e) {
			log.error("toAdminの処理で失敗！", e);
		}
	}

	@Override
	public void toApp(quickfix.Message message, SessionID sessionID) throws DoNotSend {
	}

	@Override
	public void fromAdmin(quickfix.Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
	}

	@Override
	public void fromApp(quickfix.Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		crack(message, sessionID);
	}

	public void onMessage(quickfix.fix44.ExecutionReport executionReport, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
		log.info("オーダー結果が返ってきたよ！"+ executionReport);

		orderService.report(executionReport);
	}
}
