package jp.yamato373.domain.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.model.Position;
import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.repository.PositionRepository;
import jp.yamato373.domain.service.shared.OrderService;
import jp.yamato373.uitl.AppSettings;
import jp.yamato373.uitl.FixSettings;
import jp.yamato373.uitl.FxEnums.Side;
import jp.yamato373.uitl.FxEnums.Status;
import jp.yamato373.uitl.TradeSettings;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AutoTradeServiceImpl implements AutoTradeService{

	@Autowired
	AppSettings appSettings;

	@Autowired
	FixSettings fixSettings;

	@Autowired
	TradeSettings tradeSettings;

	@Autowired
	PositionRepository positionRepository;

	@Autowired
	OrderService orderService;

	@Override
	public void checkAndOrder(String symbol, Rate rate) {

		// 売り
		positionRepository.getAll().values().forEach(position -> {
			// ポジション+トラップ値幅がレートの売り価格以下だった場合売却する
			if (position.getTrapPx().add(tradeSettings.getTrapRange()).compareTo(rate.getBidEntry().getPx()) <= 0) {
				OrderResult orderResult = orderService.bidOrder(appSettings.getCp(), appSettings.getSymbol(), position);
				log.info("AutoTradeでポジションのBID注文送信 OrderResult:" + orderResult.toString());
			}
		});

		// そもそも買えるかチェック
		if (rate.getAskEntry().isIndicative() || rate.getAskEntry().getAmt().compareTo(BigDecimal.ZERO) <= 0 || rangeLimitCheck(rate)) {
			log.debug("買いませんでした indicative:" + rate.getAskEntry().isIndicative() + " amt: " + rate.getAskEntry().getAmt() + " RangeCheck:" + rangeLimitCheck(rate));
			return;
		}
		// トラップ価格計算
		BigDecimal trapPrice = rate.getAskEntry().getPx().setScale(1, BigDecimal.ROUND_DOWN).add(tradeSettings.getTrapTiming());
		if (rate.getAskEntry().getPx().compareTo(trapPrice) < 0) {
			trapPrice = trapPrice.subtract(tradeSettings.getTrapRange());
		}
		log.debug("レートのASK:" + rate.getAskEntry().getPx().toString() + " トラップ価格:" + trapPrice.toString());
		// 買い処理
		if (!positionRepository.getAll().containsKey(trapPrice)) {
			OrderResult or = orderService.order(appSettings.getCp(), appSettings.getSymbol(), Side.ASK, tradeSettings.getOrderAmount(), trapPrice);
			log.info("AutoTradeでポジションのASK注文送信 OrderResult:" + or.toString());

			// さかのぼって注文できるポジションがあれば注文する
			Optional<BigDecimal> bestHighTrapPxOpt = positionRepository.getAll().keySet().stream().min(Comparator.naturalOrder());
			if (bestHighTrapPxOpt.isPresent()){
				for (BigDecimal b = trapPrice.add(tradeSettings.getTrapRange())
						; b.compareTo(bestHighTrapPxOpt.get()) < 0; b = b.add(tradeSettings.getTrapRange())) {
					or = orderService.order(appSettings.getCp(), appSettings.getSymbol(), Side.ASK, tradeSettings.getOrderAmount(), b);
					log.info("AutoTradeで遡ってポジションのASK注文 OrderResult:" + or.toString());
				}
			}
		}
	}

	@Override
	public void addPosition(OrderResult orderResult) {
		// リジェクトの場合は無視する
		if (!Status.FILL.equals(orderResult.getStatus())){
			return;
		}
		if (Side.BID.equals(orderResult.getSide())) {
			positionRepository.remove(orderResult);
		} else if (Side.ASK.equals(orderResult.getSide())) {
			positionRepository.add(orderResult);
		}
	}

	@Override
	public Map<BigDecimal,Position> getAllPosition() {
		return positionRepository.getAll();
	}

	private boolean rangeLimitCheck(Rate rate) {
		return tradeSettings.getUppoerLimit().compareTo(rate.getAskEntry().getPx()) < 0
				|| tradeSettings.getLowerLimit().compareTo(rate.getAskEntry().getPx()) > 0;
	}
}
