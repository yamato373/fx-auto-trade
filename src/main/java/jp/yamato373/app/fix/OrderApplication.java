package jp.yamato373.app.fix;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.entry.OrderResult;
import jp.yamato373.domain.service.AutoTradeService;
import jp.yamato373.domain.service.shared.FixService;
import jp.yamato373.domain.service.shared.OrderService;
import jp.yamato373.uitl.FixSettings;
import jp.yamato373.uitl.FxEnums;
import jp.yamato373.uitl.FxEnums.Status;
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
import quickfix.field.OrdRejReason;
import quickfix.field.Password;
import quickfix.fix44.MessageCracker;

@Component
@Slf4j
public class OrderApplication extends MessageCracker implements Application {

	@Autowired
	FixService fixService;

	@Autowired
	OrderService orderService;

	@Autowired
	AutoTradeService autoTradeService;

	@Autowired
	FixSettings fixSettings;

	@Override
	public void onCreate(SessionID sessionID) {
	}

	@Override
	public void onLogon(SessionID sessionID) {
		log.info("オーダーセッションのログイン完了。");
		fixService.orderSenderStart(sessionID);
	}

	@Override
	public void onLogout(SessionID sessionID) {
		log.info("オーダーセッションのログアウト完了。");
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
		log.info("オーダー結果受信。ExecutionReport:"+ executionReport);

		OrderResult or = orderService.getOrderResult(executionReport.getClOrdID().getValue());

		Status status = FxEnums.getStatus(executionReport.getOrdStatus().getValue());
		or.setStatus(status);
		or.setOrderId(executionReport.getOrderID().getValue());
		or.setExecId(executionReport.getExecID().getValue());
		if (Status.FILL.equals(status)){
			or.setLastQty(BigDecimal.valueOf(executionReport.getLastQty().getValue()));
			or.setLastPx(BigDecimal.valueOf(executionReport.getLastPx().getValue()));
			or.setExecTime(executionReport.getTransactTime().getValue());
		}
		if (executionReport.isSetField(OrdRejReason.FIELD)){
			or.setRejReason(executionReport.getOrdRejReason().getValue());
		}
		orderService.setOrderResult(or);
		autoTradeService.updatePosition(or);
	}
}
