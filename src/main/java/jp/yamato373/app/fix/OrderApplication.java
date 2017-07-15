package jp.yamato373.app.fix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.service.AutoTradeServiceImpl;
import jp.yamato373.domain.service.FixService;
import jp.yamato373.domain.service.shared.OrderServiceImpl;
import jp.yamato373.uitl.FixSettings;
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

	@Autowired
	FixService fixService;

	@Autowired
	OrderServiceImpl orderService;

	@Autowired
	AutoTradeServiceImpl autoTradeService;

	@Autowired
	FixSettings fixSettings;

	@Override
	public void onCreate(SessionID sessionID) {
	}

	@Override
	public void onLogon(SessionID sessionID) {
		log.info("オーダーセッションのログイン完了");
		fixService.orderSenderStart(sessionID);
	}

	@Override
	public void onLogout(SessionID sessionID) {
		log.info("オーダーセッションのログアウト完了");
	}

	@Override
	public void toAdmin(quickfix.Message message, SessionID sessionID) {
		try {
			if (MsgType.LOGON.equals(message.getHeader().getString(MsgType.FIELD))) {
				message.getHeader().setString(Password.FIELD, fixSettings.getPassword());
			}
		} catch (FieldNotFound e) {
			log.error("message:" + message, e);
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
		log.info("オーダー結果受信 ExecutionReport:"+ executionReport);

		OrderResult or = orderService.report(executionReport);
		autoTradeService.addPosition(or);
	}
}
