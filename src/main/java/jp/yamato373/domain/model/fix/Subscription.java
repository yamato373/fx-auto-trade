package jp.yamato373.domain.model.fix;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.yamato373.domain.service.shared.RateService;
import jp.yamato373.uitl.AppSettings;
import jp.yamato373.uitl.FixSettings;
import lombok.extern.slf4j.Slf4j;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataRequest.NoMDEntryTypes;
import quickfix.fix44.MarketDataRequest.NoRelatedSym;

@Component
@Slf4j
public class Subscription {

	@Autowired
	RateService rateService;

	@Autowired
	FixSettings fixSettings;

	@Autowired
	AppSettings appSettings;

	private final int MARKET_DEPTH = 1;

	/**
	 * 初回subscribeフラグ
	 */
	private boolean firstFlg = true;

	/**
	 * 最後にsubscribeしたときのリクエストID
	 */
	private String mDReqID;

	/**
	 * 最後にプライスが来た時間を保持
	 */
	private Date lastReceivetime = new Date();

	SessionID sessionId;

	private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	public void start(SessionID sessionId) {
		this.sessionId = sessionId;

		service.scheduleWithFixedDelay(() -> {

			log.info("Subscriptionチェックタスクを実行。");

			// 初回subscribe
			if (firstFlg) {
				subscribe();
				firstFlg = false;
			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(lastReceivetime);
				cal.add(Calendar.SECOND, fixSettings.getDelayThreshold());

				// 無配信検知
				if (0 >= cal.getTime().compareTo(new Date())) {
					log.info("無配信検知。");
					unsubscribe();
					subscribe();
				}
			}
		}, 0, fixSettings.getSubscribeCheckInterval(), TimeUnit.SECONDS);
	}

	/**
	 * リクエストIDを初期化
	 */
	public void clearMDReqID() {
		log.info("リクエストID初期化。");
		mDReqID = null;
	}

	/**
	 * リクエストIDのgetter
	 */
	public String getMDReqID() {
		return mDReqID;
	}

	/**
	 * 最後にプライスが来た時間を更新
	 */
	public void updataLastReceivetime() {
		lastReceivetime = new Date();
	}

	/**
	 * subscribe
	 *
	 * @param sessionID
	 */
	private void subscribe() {
		mDReqID = appSettings.getSymbol() + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		MarketDataRequest marketDataRequest = new MarketDataRequest();

		marketDataRequest.set(new MDReqID(mDReqID));
		marketDataRequest.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		marketDataRequest.set(new MarketDepth(MARKET_DEPTH));
		marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));

		NoMDEntryTypes noMDEntryTypes = new NoMDEntryTypes();
		noMDEntryTypes.set(new MDEntryType(MDEntryType.BID));
		marketDataRequest.addGroup(noMDEntryTypes);
		noMDEntryTypes.set(new MDEntryType(MDEntryType.OFFER));
		marketDataRequest.addGroup(noMDEntryTypes);

		NoRelatedSym noRelatedSym = new NoRelatedSym();
		noRelatedSym.set(new Symbol(appSettings.getSymbol()));
		marketDataRequest.addGroup(noRelatedSym);

		send(marketDataRequest);

		log.info("subscribe送信。");
	}

	/**
	 * unsubscribe
	 *
	 * <p>
	 * リクエストIDがない状態でコール禁止
	 *
	 * @param sessionID
	 */
	public void unsubscribe() {
		MarketDataRequest marketDataRequest = new MarketDataRequest();

		marketDataRequest.set(new MDReqID(mDReqID));
		marketDataRequest.set(
				new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
		marketDataRequest.set(new MarketDepth(MARKET_DEPTH));

		NoMDEntryTypes noMDEntryTypes = new NoMDEntryTypes();
		noMDEntryTypes.set(new MDEntryType(MDEntryType.BID));
		marketDataRequest.addGroup(noMDEntryTypes);
		noMDEntryTypes.set(new MDEntryType(MDEntryType.OFFER));
		marketDataRequest.addGroup(noMDEntryTypes);

		NoRelatedSym noRelatedSym = new NoRelatedSym();
		noRelatedSym.set(new Symbol(appSettings.getSymbol()));
		marketDataRequest.addGroup(noRelatedSym);

		send(marketDataRequest);

		log.info("Unsubscribe送信");

		rateService.clearRate();
	}

	/**
	 * FIXメッセージ送信
	 *
	 * @param message
	 * @param sessionID
	 */
	private void send(Message message) {
		try {
			Session.sendToTarget(message, sessionId);
		} catch (SessionNotFound e) {
			log.error("プライスメッセージ送信に失敗。", e);
		}
	}

}
