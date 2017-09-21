package jp.yamato373.domain.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.entry.OrderResult;
import jp.yamato373.domain.model.entry.Position;
import jp.yamato373.domain.model.entry.PositionHistory;
import jp.yamato373.domain.repository.PositionHistoryRepository;
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
public class AutoTradeService {

	@Autowired
	AppSettings appSettings;

	@Autowired
	FixSettings fixSettings;

	@Autowired
	TradeSettings tradeSettings;

	@Autowired
	PositionRepository positionRepository;

	@Autowired
	PositionHistoryRepository positionHistoryRepository;

	@Autowired
	OrderService orderService;

	/**
	 * オーダー要かチェックしオーダーする
	 *
	 * @param rate
	 */
	public void checkAndOrder(Rate rate) {
		// 売り処理
		if (bidEntryIndicativeCheck(rate)) {
			log.info("Indicativeの為、売り対象ではありません。indicative:" + rate.getBidEntry().isIndicative()
					+ "、amt:" + rate.getBidEntry().getAmt());
		} else {
			for (Position position : positionRepository.findAll()) {
				// ポジション+トラップ値幅がレートの売り価格以下だった場合、かつ注文中じゃない場合
				if (!position.isBuyingFlg() && Objects.isNull(position.getBidClOrdId())
						&& position.getTrapPx().add(tradeSettings.getTrapRange()).compareTo(rate.getBidEntry().getPx()) <= 0) {

					OrderResult or = orderService.order(
							Side.ASK,
							orderService.getBidAmt(position),
							position.getTrapPx().add(tradeSettings.getTrapRange()));

					position.setBidClOrdId(or.getClOrdId());
					positionRepository.save(position);

					log.info("AutoTradeでポジションのASK注文送信 OrderResult:" + or + "、Position:" + position);
				}
			}
		}

		// 買い処理
		if (askEntryIndicativeCheck(rate) || rangeLimitCheck(rate)) {
			log.info("Indicativeまたはレンジ外の為、買い対象ではありません。indicative:" + rate.getAskEntry().isIndicative()
					+ "、amt:" + rate.getAskEntry().getAmt() + "、RangeCheck:" + rangeLimitCheck(rate));
		} else {
			// トラップ価格計算
			// 例1) ASK価格:99.001→トラップ価格:99.07
			// 例2) ASK価格:100.17→トラップ価格:100.17
			// 例3) ASK価格:100.171→トラップ価格:100.27
			BigDecimal trapPx = rate.getAskEntry().getPx().setScale(1, BigDecimal.ROUND_DOWN).add(tradeSettings.getTrapTiming());
			if (rate.getAskEntry().getPx().compareTo(trapPx) > 0) {
				trapPx = trapPx.add(tradeSettings.getTrapRange());
			}
			log.debug("トラップ価格計算。" + rate.getAskEntry().getPx().toString() + "→" + trapPx.toString());

			// ポジションを持っていなかったら買う
			if (!positionRepository.exists(trapPx)) {

				// 遡って注文できるポジションがあれば注文する
				BigDecimal firstPx = positionRepository.findMinFirstPx();
				log.debug("保持しているポジションで一番小さい値:" + firstPx);
				if (firstPx != null) {
					for (BigDecimal tp = trapPx.add(tradeSettings.getTrapRange()); tp.compareTo(firstPx) < 0
							; tp = tp.add(tradeSettings.getTrapRange())) {

						OrderResult or = orderService.order(Side.BID, tradeSettings.getOrderAmount(), trapPx);
						Position p = positionRepository.save(new Position(tp, or.getClOrdId(), null, true));

						log.info("AutoTradeでポジションを遡ってBID注文。OrderResult:" + or + "、Position:" + p);
					}
				}
				// 現在の価格のポジションを注文する
				OrderResult or = orderService.order(Side.BID, tradeSettings.getOrderAmount(), trapPx);
				Position p = positionRepository.save(new Position(trapPx, or.getClOrdId(), null, true));

				log.info("AutoTradeでBID注文送信。OrderResult:" + or + "、Position:" + p);
			}
		}
	}

	/**
	 * オーダー結果をPositionに反映させる
	 *
	 * @param orderResult
	 */
	public void updatePosition(OrderResult orderResult) {

		if (!Status.FILL.equals(orderResult.getStatus())) {
			if (Side.ASK.equals(orderResult.getSide())) {
				log.info("AutoTradeでASK注文失敗。OrderResult:" + orderResult);
				Position p = positionRepository.findByBidClOrdId(orderResult.getClOrdId());
				p.setBidClOrdId(null);
				positionRepository.save(p);
				log.info("ポジションを更新しました。p:" + p);

			} else if (Side.BID.equals(orderResult.getSide())) {
				log.info("AutoTradeでBID注文失敗。OrderResult:" + orderResult);
				positionRepository.delete(positionRepository.findByAskClOrdId(orderResult.getClOrdId()));
				log.info("ポジションを削除しました。");
			}
		} else if (Side.ASK.equals(orderResult.getSide())) {
			log.info("AutoTradeでASK注文成功。OrderResult:" + orderResult);
			Position p = positionRepository.findByBidClOrdId(orderResult.getClOrdId());
			PositionHistory ph = new PositionHistory(null, p.getTrapPx(), p.getAskClOrdId(), p.getBidClOrdId(), new Date());
			positionHistoryRepository.save(ph);
			log.info("ポジション履歴に追加しました。p:" + ph);
			positionRepository.delete(p);
			log.info("ポジションを削除しました。p:" + p);

		} else if (Side.BID.equals(orderResult.getSide())){
			log.info("AutoTradeでBID注文成功。OrderResult:" + orderResult);
			Position p = positionRepository.findByAskClOrdId(orderResult.getClOrdId());
			p.setBuyingFlg(false);
			positionRepository.save(p);
			log.info("ポジションを更新しました。p:" + p);
		}
	}

	private boolean bidEntryIndicativeCheck(Rate rate) {
		return rate.getBidEntry().isIndicative() || rate.getBidEntry().getAmt().compareTo(BigDecimal.ZERO) <= 0;
	}

	private boolean askEntryIndicativeCheck(Rate rate) {
		return rate.getAskEntry().isIndicative() || rate.getAskEntry().getAmt().compareTo(BigDecimal.ZERO) <= 0;
	}

	private boolean rangeLimitCheck(Rate rate) {
		return tradeSettings.getUppoerLimit().compareTo(rate.getAskEntry().getPx()) < 0
				|| tradeSettings.getLowerLimit().compareTo(rate.getAskEntry().getPx()) > 0;
	}
}
