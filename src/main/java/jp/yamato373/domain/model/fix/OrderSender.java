package jp.yamato373.domain.model.fix;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.uitl.FixSettings;
import lombok.extern.slf4j.Slf4j;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix44.NewOrderSingle;

@Component
@Slf4j
public class OrderSender {

	@Autowired
	FixSettings fixSettings;

	SessionID sessionId;

	BlockingQueue<Message> orderMessagedQueue = new LinkedBlockingQueue<>();

	private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	@PostConstruct
	public void init() {
		log.info("OrderSenderクラスの初期化開始");
	}

	public void start(SessionID sessionId) {

		this.sessionId = sessionId;

		service.execute(() -> {

			while (true) {
				try {
					Message message = orderMessagedQueue.take();
					Session.sendToTarget(message, sessionId);

				} catch (InterruptedException | SessionNotFound e) {
					log.error("メッセージ送信に失敗", e);
				}
			}
		});
	}

	/**
	 * 注文
	 *
	 * @param orderResult
	 * @throws InterruptedException
	 */
	public void sendNewOrderSingle(OrderResult orderResult) {
		NewOrderSingle newOrderSingle = new NewOrderSingle(
				new ClOrdID(orderResult.getClOrdId()),
				new Side(orderResult.getSide().getFieldCode()),
				new TransactTime(orderResult.getOrderTime()),
				new OrdType(OrdType.LIMIT));
		newOrderSingle.set(new Account(fixSettings.getAccount()));
		newOrderSingle.set(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
		newOrderSingle.set(new Symbol(orderResult.getSymbol()));
		newOrderSingle.set(new OrderQty(orderResult.getOrderQty().doubleValue()));
		newOrderSingle.set(new Price(orderResult.getPrice().doubleValue()));
		newOrderSingle.set(new TimeInForce(TimeInForce.FILL_OR_KILL));

		sendMessage(newOrderSingle);
	}

	/**
	 * FIXメッセージ送信
	 *
	 * @param message
	 * @param sessionID
	 */
	private void sendMessage(Message message) {
		try {
			orderMessagedQueue.put(message);
		} catch (InterruptedException e) {
			log.error("送信メッセージの追加に失敗 message:" + message, e);
		}
	}
}
