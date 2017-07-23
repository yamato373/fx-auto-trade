package jp.yamato373.domain.service.shared;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.cache.RateCache;
import jp.yamato373.domain.model.entry.OrderResult;
import jp.yamato373.domain.model.entry.Position;
import jp.yamato373.domain.model.fix.OrderSender;
import jp.yamato373.domain.repository.OrderResultRepository;
import jp.yamato373.uitl.AppSettings;
import jp.yamato373.uitl.FxEnums.Side;
import jp.yamato373.uitl.FxEnums.Status;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

	@Autowired
	RateCache rateCache;

	@Autowired
	OrderResultRepository orderResultRepository;

	@Autowired
	OrderSender orderSender;

	@Autowired
	AppSettings appSettings;

	/**
	 * 最新のプライスでオーダーする
	 *
	 * @param side
	 * @param amt
	 * @return
	 */
	public OrderResult order(Side side, BigDecimal amt) {
		BigDecimal px = rateCache.getEntry(Side.ASK.equals(side) ? Side.BID : Side.ASK).getPx();
		OrderResult or = new OrderResult(Status.ORDER, appSettings.getSymbol(), side, new Date(), amt, px);
		return order(or);
	}

	/**
	 * プライスを指定してオーダーする
	 *
	 * @param side
	 * @param amt
	 * @param price
	 * @return
	 */
	public OrderResult order(Side side, BigDecimal amt, BigDecimal price) {
		OrderResult or = new OrderResult(Status.ORDER, appSettings.getSymbol(), side, new Date(), amt, price);
		return order(or);
	}

	/**
	 * ClOrdIDを元に送信時のOrderResultを取得
	 *
	 * @param ClOrdId
	 * @return
	 */
	public OrderResult getOrderResult(Integer clOrdId) {
		return orderResultRepository.findOne(clOrdId);
	}

	/**
	 * 注文結果反映済みのOrderResultを格納
	 *
	 * @param OrderResult
	 */
	public void setOrderResult(OrderResult or) {
		orderResultRepository.save(or);
		log.info("注文結果格納" + or);
	}

	/**
	 *
	 * ポジションを取得したときのアマウントを取得
	 * @param position
	 * @return amt
	 */
	public BigDecimal getBidAmt(Position position) {
		return orderResultRepository.findOne(position.getAskClOrdId()).getLastQty();
	}

	/**
	 * 全注文履歴
	 *
	 * @return
	 */
	public List<OrderResult> getOrderResultAll() {
		return orderResultRepository.findAll();
	}

	private OrderResult order(OrderResult or) {
		orderResultRepository.save(or);
		orderSender.sendNewOrderSingle(or);
		return or;
	}
}
