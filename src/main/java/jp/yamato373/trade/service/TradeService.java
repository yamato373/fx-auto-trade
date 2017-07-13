package jp.yamato373.trade.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.dataaccess.PositionDao;
import jp.yamato373.fix.util.FixSettings;
import jp.yamato373.order.model.OrderResult;
import jp.yamato373.order.service.OrderService;
import jp.yamato373.price.model.Rate;
import jp.yamato373.trade.util.TradeSettings;
import jp.yamato373.uitl.AppSettings;
import jp.yamato373.uitl.FxEnums.Side;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TradeService {

	@Autowired
	AppSettings appSettings;

	@Autowired
	FixSettings fixSettings;

	@Autowired
	TradeSettings tradeSettings;

	@Autowired
	PositionDao positionDao;

	@Autowired
	OrderService orderService;

	/**
	 * オーダーが必要ならオーダーするサービス
	 *
	 * @param rate
	 * @param symbol
	 */
	public void checkAndOrder(String symbol, Rate rate) {

		// 売り
		positionDao.getAll().values().forEach(position -> {
			// ポジション+トラップ値幅がレートの売り価格以下だった場合売却する
			if (position.getTrapPx().add(tradeSettings.getTrapRange()).compareTo(rate.getBidEntry().getPx()) <= 0) {

				// 持ってる数量を売れるだけ売る
				BigDecimal amt;
				if (position.getAgreedAmt().compareTo(rate.getBidEntry().getAmt()) <= 0) {
					amt = position.getAgreedAmt();
				} else {
					amt = rate.getBidEntry().getAmt();
				}
				OrderResult orderResult = orderService.order(appSettings.getCp(), appSettings.getSymbol(), Side.BID, amt); // TODO 結果処理考える
				log.info("オートトレードでポジションの売り注文したよ！OrderResult:" + orderResult.toString());
			}
		});

		// 買い
		if (rate.getAskEntry().isIndicative() || rangeLimitCheck(rate)) {
			log.info("買いのレートでしたが買いませんでした！indicative:" + rate.getAskEntry().isIndicative()
					+ " RangeCheck:"+ rangeLimitCheck(rate));
			return;
		}
		// トラップ価格計算
		BigDecimal trapPrice = rate.getAskEntry().getPx()
				.setScale(1, BigDecimal.ROUND_DOWN).add(tradeSettings.getTrapTiming());
		if (rate.getAskEntry().getPx().compareTo(trapPrice) < 0) {
			trapPrice = trapPrice.subtract(tradeSettings.getTrapRange());
		}
		if (!positionDao.getAll().containsKey(trapPrice)) {
			BigDecimal amt;
			if (tradeSettings.getOrderAmount().compareTo(rate.getAskEntry().getAmt()) <= 0) {
				amt = tradeSettings.getOrderAmount();
			} else {
				amt = rate.getAskEntry().getAmt();
			}
			OrderResult or = orderService.order(appSettings.getCp(), appSettings.getSymbol(), Side.ASK, amt, trapPrice); // TODO // 結果処理考える
			log.info("オートトレードでポジションの買い注文したよ！OrderResult:" + or.toString());

			// 取りこぼしたポジションがあれば注文する
			Optional<BigDecimal> bestHighTrapPx = positionDao.getAll().keySet().stream().min(Comparator.naturalOrder());
			if (bestHighTrapPx.isPresent()) {
				for (BigDecimal b = trapPrice.add(tradeSettings.getTrapRange())
						; b.compareTo(bestHighTrapPx.get()) < 0 ; b.add(tradeSettings.getTrapRange())) {
					or = orderService.order(appSettings.getCp(), appSettings.getSymbol(), Side.ASK, amt, b); // TODO // 結果処理考える
					log.info("オートトレードでさかのぼってポジションの買い注文したよ！OrderResult:" + or.toString());
				}
			}
		}
	}

	private boolean rangeLimitCheck(Rate rate) {
		return tradeSettings.getUppoerLimit().compareTo(rate.getAskEntry().getPx()) < 0
				|| tradeSettings.getLowerLimit().compareTo(rate.getAskEntry().getPx()) > 0;
	}

	public static void main(String[] args) {
		BigDecimal askpx = new BigDecimal("99");
		for (int i = 0; i <= 10000; i++) {
			log.info("元の値は " + askpx.toString());
			log.info("切り捨て " + askpx.setScale(1, BigDecimal.ROUND_DOWN).toString());
			log.info("０．０７ " + askpx.setScale(1, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.07")).toString());
			BigDecimal b = askpx.setScale(1, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.07"));
			if (askpx.compareTo(b) < 0) {
				log.info("標準ーー " + b.subtract(BigDecimal.valueOf(0.1)).toString());
			} else {
				log.info("標準＋＋ " + b.toString());
			}
			log.info("―――――――――――――――――――――――――――――――");
			askpx = askpx.add(new BigDecimal(0.001)).setScale(3, BigDecimal.ROUND_HALF_UP);
		}
		Map<BigDecimal, String> testMap = new HashMap<>();
		testMap.put(BigDecimal.valueOf(111.111), "aaaa");
		testMap.put(BigDecimal.valueOf(111.112), "aaaa");
		testMap.put(BigDecimal.valueOf(111.113), "aaaa");
		testMap.keySet().stream().min(Comparator.naturalOrder()).ifPresent(k -> log.info(k.toString()));
	}
}
