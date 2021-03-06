package jp.yamato373.app.fix;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.Rate.Entry;
import jp.yamato373.domain.service.AutoTradeService;
import jp.yamato373.domain.service.shared.FixService;
import jp.yamato373.domain.service.shared.RateService;
import jp.yamato373.uitl.FixSettings;
import lombok.extern.slf4j.Slf4j;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.Password;
import quickfix.field.SendingTime;
import quickfix.field.Text;
import quickfix.fix44.MessageCracker;

@Component
@Slf4j
public class PriceApplication extends MessageCracker implements Application {

	@Autowired
	FixService fixService;

	@Autowired
	RateService rateService;

	@Autowired
	AutoTradeService autoTradeService;

	@Autowired
	FixSettings fixSettings;

	@Override
	public void onCreate(SessionID sessionID) {
	}

	@Override
	public void onLogon(SessionID sessionID) {
		log.info("プライスセッションのログイン完了。");
		fixService.subscriptionStart(sessionID);
	}

	@Override
	public void onLogout(SessionID sessionID) {
		fixService.stop();
		log.info("プライスセッションのログアウト完了。");
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
		if (MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH.equals(message.getHeader().getString(MsgType.FIELD))
				|| MsgType.MARKET_DATA_INCREMENTAL_REFRESH.equals(message.getHeader().getString(MsgType.FIELD))) {

			// リクエストIDがないのにプライスが来た場合は無視
			if (StringUtils.isEmpty(fixService.getMDReqID())){
				log.warn("リクエストIDを保持していないのにプライスを受信しました。message:" + message);
				return;
			}
			crack(message, sessionID);
		}
		if (MsgType.NEWS.equals(message.getHeader().getString(MsgType.FIELD))){
			log.info("Newsは無視します。message:"+ message);
			return;
		}
	}

	public void onMessage(quickfix.fix44.MarketDataSnapshotFullRefresh snapshot, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		if (!fixService.getMDReqID().equals(snapshot.getMDReqID().getValue())){
			log.warn("管理していないリクエストIDのプライスを受信しました。保持reqID:" + fixService.getMDReqID()
				+ "、受信reqID" + snapshot.getMDReqID());
			return;
		}

		String symbol = snapshot.getSymbol().getValue();

		Rate rate = new Rate(symbol, snapshot.getHeader().getUtcTimeStamp(SendingTime.FIELD));

		for (int i = 1; i <= snapshot.getNoMDEntries().getValue(); ++i) {
			Group mdEntry = snapshot.getGroup(i, NoMDEntries.FIELD);

			Entry entry = new Entry();
			entry.setPx(mdEntry.getDecimal(MDEntryPx.FIELD));
			entry.setAmt(mdEntry.getDecimal(MDEntrySize.FIELD));
			if (mdEntry.isSetField(Text.FIELD) && fixSettings.getIndicativeText().equals(mdEntry.getString(Text.FIELD))) {
				entry.setIndicative(true);
			}
			if (MDEntryType.OFFER == mdEntry.getChar(MDEntryType.FIELD)) {
				rate.setAskEntry(entry);
			} else {
				rate.setBidEntry(entry);
			}
		}
		rateService.setRate(rate);
		fixService.updataLastReceivetime();
	}

	public void onMessage(quickfix.fix44.MarketDataIncrementalRefresh incremental, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		if (!fixService.getMDReqID().equals(incremental.getMDReqID().getValue())){
			log.warn("管理していないリクエストIDのプライスを受信しました。保持reqID:" + fixService.getMDReqID()
				+ "、受信reqID" + incremental.getMDReqID());
			return;
		}

		Rate rate = new Rate();
		BeanUtils.copyProperties(rateService.getRate(), rate);
		rate.setSendingTime(incremental.getHeader().getUtcTimeStamp(SendingTime.FIELD));

		for (int i = 1; i <= incremental.getNoMDEntries().getValue(); ++i) {
			Group mdEntry = incremental.getGroup(i, NoMDEntries.FIELD);

			if (MDEntryType.OFFER == mdEntry.getChar(MDEntryType.FIELD)) {
				if (mdEntry.isSetField(MDEntryPx.FIELD)) {
					rate.getAskEntry().setPx(mdEntry.getDecimal(MDEntryPx.FIELD));
				}
				if (mdEntry.isSetField(MDEntrySize.FIELD)) {
					rate.getAskEntry().setAmt(mdEntry.getDecimal(MDEntrySize.FIELD));
				}
				if (mdEntry.isSetField(Text.FIELD) && fixSettings.getIndicativeText().equals(mdEntry.getString(Text.FIELD))) {
					rate.getAskEntry().setIndicative(true);
				} else {
					rate.getAskEntry().setIndicative(false);
				}
			} else {
				if (mdEntry.isSetField(MDEntryPx.FIELD)) {
					rate.getBidEntry().setPx(mdEntry.getDecimal(MDEntryPx.FIELD));
				}
				if (mdEntry.isSetField(MDEntrySize.FIELD)) {
					rate.getBidEntry().setAmt(mdEntry.getDecimal(MDEntrySize.FIELD));
				}
				if (mdEntry.isSetField(Text.FIELD) && fixSettings.getIndicativeText().equals(mdEntry.getString(Text.FIELD))) {
					rate.getBidEntry().setIndicative(true);
				} else {
					rate.getBidEntry().setIndicative(false);
				}
			}
		}
		rateService.setRate(rate);
		fixService.updataLastReceivetime();
		autoTradeService.checkAndOrder(rate);
	}
}
