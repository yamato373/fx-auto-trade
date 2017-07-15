package jp.yamato373.fix.model;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jp.yamato373.fix.util.FixSettings;
import jp.yamato373.price.model.Rate;
import jp.yamato373.price.model.Rate.Entry;
import jp.yamato373.price.service.PriceService;
import jp.yamato373.trade.service.TradeService;
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
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.fix44.MessageCracker;

@Component
@Slf4j
public class PriceApplication extends MessageCracker implements Application {

	@Autowired
	FixSettings fixSettings;

	@Autowired
	Subscription subscription;

	@Autowired
	PriceService priceService;

	@Autowired
	TradeService tradeService;

	@Override
	public void onCreate(SessionID sessionID) {
	}

	@Override
	public void onLogon(SessionID sessionID) {
		log.info("プライスセッションのログインしたよ！");
		subscription.start(sessionID);
	}

	@Override
	public void onLogout(SessionID sessionID) {
		log.info("プライスセッションのログアウトしたよ！");
	}

	@Override
	public void toAdmin(quickfix.Message message, SessionID sessionID) {

		try {
			if (MsgType.LOGON.equals(message.getHeader().getString(MsgType.FIELD))) {
				message.getHeader().setString(Password.FIELD, fixSettings.getPassword());
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

		if (MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH.equals(message.getHeader().getString(MsgType.FIELD))
				|| MsgType.MARKET_DATA_INCREMENTAL_REFRESH.equals(message.getHeader().getString(MsgType.FIELD))) {

			// リクエストIDがないのにプライスが来た場合
			if (StringUtils.isEmpty(subscription.getMDReqID()))
				return;

			crack(message, sessionID);
		}
	}

	public void onMessage(quickfix.fix44.MarketDataSnapshotFullRefresh snapshot, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		String symbol = snapshot.getSymbol().getValue();

		Rate rate = new Rate(symbol, snapshot.getHeader().getUtcTimeStamp(SendingTime.FIELD));

		for (int i = 1; i <= snapshot.getNoMDEntries().getValue(); ++i) {
			Group mdEntry = snapshot.getGroup(i, NoMDEntries.FIELD);

			Entry entry = new Entry();
//			entry.setSide(mdEntry.getInt(MDEntryType.FIELD)); // TODO いる？
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
		priceService.setRate(symbol, rate);
		subscription.updataLastReceivetime();
	}

	public void onMessage(quickfix.fix44.MarketDataIncrementalRefresh incremental, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		Rate rate = new Rate();
		String symbol = incremental.getGroup(1, NoMDEntries.FIELD).getString(Symbol.FIELD);
		BeanUtils.copyProperties(priceService.getRate(symbol), rate);

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
		priceService.setRate(symbol, rate);
		subscription.updataLastReceivetime();
		tradeService.checkAndOrder(symbol, rate);
	}
}
