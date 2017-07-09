package jp.yamato373.fix.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.price.service.PriceService;
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

@Service
@Slf4j
public class Subscription {

	@Autowired
	PriceService priceService;

	private final String SYMBOL = "USD/JPY";
	private final int MARKET_DEPTH = 1;

	/**
	 * サブスクライブチェック間隔
	 */
	private final int INTERVAL = 60;

	/**
	 * 遅延閾値
	 */
	private final int delayThreshold = 60;

	/**
	 * 初回サブスクライブフラグ
	 */
	private boolean firstFlg = true;

	/**
	 * 最後にサブスクライブしたときのリクエストID
	 */
	private String mDReqID;

	/**
	 * 最後にプライスが来た時間を保持
	 */
	private Date lastReceivetime = new Date();

	SessionID sessionId;

	private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	@PostConstruct
	public void init() {
		log.info("サブスクライブクラスを初期化するよ！");
	}

	public void start(SessionID sessionId) {

		this.sessionId = sessionId;

		service.scheduleWithFixedDelay(() -> {

			log.info("サブスクライブタスクが実行されたよ！");

			// 初回サブスクライブ
			if (firstFlg) {
				subscribe();
				firstFlg = false;
			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(lastReceivetime);
				cal.add(Calendar.SECOND, delayThreshold);

				// 無配信検知
				if (0 >= cal.getTime().compareTo(new Date())) {
					log.info("無配信検知したよ！");
					unsubscribe();
					subscribe();
				}
			}
		}, 0, INTERVAL, TimeUnit.SECONDS);
	}

	/**
	 * リクエストIDを初期化
	 */
	public void clearMDReqID() {
		log.info("リクエストIDを初期化するよ！");
		mDReqID = null;
	}

	/**
	 * リクエストIDのゲッター
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
	 * サブスクライブ
	 *
	 * @param sessionID
	 */
	private void subscribe() {

		log.info("サブスクライブするよ！");

		mDReqID = SYMBOL + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

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
		noRelatedSym.set(new Symbol(SYMBOL));
		marketDataRequest.addGroup(noRelatedSym);

		send(marketDataRequest);
	}

	/**
	 * アンサブスクライブ
	 *
	 * <p>
	 * リクエストIDがない状態でコール禁止
	 *
	 * @param sessionID
	 */
	public void unsubscribe() {

		log.info("アンサブスクライブするよ！");

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
		noRelatedSym.set(new Symbol(SYMBOL));
		marketDataRequest.addGroup(noRelatedSym);

		send(marketDataRequest);

		priceService.clearRate(SYMBOL);
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
			log.error("サブスクライブ送信に失敗しました", e);
		}
	}

}
