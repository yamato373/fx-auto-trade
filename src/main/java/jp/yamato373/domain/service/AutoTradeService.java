package jp.yamato373.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.cache.PositionCache;
import jp.yamato373.domain.model.entry.OrderResult;
import jp.yamato373.domain.model.entry.Position;
import jp.yamato373.domain.model.entry.PositionHistory;
import jp.yamato373.domain.repository.OrderResultRepository;
import jp.yamato373.domain.repository.PositionHistoryRepository;
import jp.yamato373.domain.repository.PositionRepository;
import jp.yamato373.domain.service.shared.OrderService;
import jp.yamato373.uitl.AppSettings;
import jp.yamato373.uitl.FixSettings;
import jp.yamato373.uitl.FxEnums.Side;
import jp.yamato373.uitl.FxEnums.Status;
import jp.yamato373.uitl.TradeSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
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
	OrderResultRepository orderResultRepository;

	@Autowired
	PositionCache positionCache;

	@Autowired
	OrderService orderService;

	int id = 0; // TODO 暫定

	@PostConstruct
	public void start(){
		positionCache.refresh();
	}

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

			boolean changePositionFlg = false;
			for (Position position : positionCache.getPositionSet()) {
				// ポジション+トラップ値幅がレートの売り価格以下だった場合、かつ注文中じゃない場合
				if (!position.isBuyingFlg() && Objects.isNull(position.getBidClOrdId())
						&& position.getTrapPx().add(tradeSettings.getTrapRange()).compareTo(rate.getBidEntry().getPx()) <= 0) {

					Position p = positionRepository.findByAskClOrdId(position.getAskClOrdId());
					OrderResult or = orderService.order(
							Side.ASK,
							orderResultRepository.findOne(p.getAskClOrdId()).getLastQty(),
							p.getTrapPx().add(tradeSettings.getTrapRange()));

					p.setBidClOrdId(or.getClOrdId());
					positionRepository.save(p);

					changePositionFlg = true;
					log.info("AutoTradeでポジションのASK注文送信 OrderResult:" + or + "、Position:" + p);
				}
			}
			if (changePositionFlg){
				positionCache.refresh();
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
			if (positionCache.notContains(trapPx)) {
				OrderResult or = orderService.order(Side.BID, tradeSettings.getOrderAmount(), trapPx);
				Position p = positionRepository.save(new Position(trapPx, or.getClOrdId(), true));

				log.info("AutoTradeでBID注文送信。OrderResult:" + or + "、Position:" + p);

				// 遡って注文できるポジションがあれば注文する
				BigDecimal firstPx = positionCache.getFirst();
				log.debug("保持しているポジションで一番小さい値:" + firstPx);

				if (firstPx != null) {
					for (BigDecimal tp = trapPx.add(tradeSettings.getTrapRange()); tp.compareTo(firstPx) < 0
							; tp = tp.add(tradeSettings.getTrapRange())) {

						or = orderService.order(Side.BID, tradeSettings.getOrderAmount(), trapPx);
						p = positionRepository.save(new Position(tp, or.getClOrdId(), true));

						log.info("AutoTradeでポジションを遡ってASK注文。OrderResult:" + or + "、Position:" + p);
					}
				}
				positionCache.refresh();
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
				Position p = positionRepository.findByBidClOrdId(orderResult.getClOrdId());
				p.setBidClOrdId(null);
				positionRepository.save(p);

			} else if (Side.BID.equals(orderResult.getSide())) {
				positionRepository.deleteByAskClOrdId(orderResult.getClOrdId());

			}
		} else if (Side.ASK.equals(orderResult.getSide())) {
			Position p = positionRepository.findByBidClOrdId(orderResult.getClOrdId());
			PositionHistory ph = new PositionHistory(id++, p.getTrapPx(), p.getAskClOrdId(), p.getBidClOrdId()); // TODO 暫定
			positionHistoryRepository.save(ph);
			positionRepository.deleteByBidClOrdId(orderResult.getClOrdId());

		} else if (Side.BID.equals(orderResult.getSide())){
			Position p = positionRepository.findByAskClOrdId(orderResult.getClOrdId());
			p.setBuyingFlg(false);
			positionRepository.save(p);
		}
		positionCache.refresh();
	}

	/**
	 * 保持しているポジションを全件取得
	 *
	 * @return Set<Position>
	 */
	public List<Position> getAllPosition() {
		List<Position> list = positionRepository.findAll();
		list.sort((a, b) -> a.getTrapPx().compareTo(b.getTrapPx()));
		return list;
	}

	/**
	 * 決済したポジションを全件取得*
	 * @return List<PositionHistory>
	 */
	public List<PositionHistory> getAllPositionHistory() {
		List<PositionHistory> list = positionHistoryRepository.findAll();
		list.sort((a, b) -> a.getId().compareTo(b.getId()));
		return list;
	}

	/**
	 * 利益結果取得
	 *
	 * @return Collection<ProfitReport>
	 */
	public Collection<ProfitReport> profitReport() {
		List<ProfitReport> list = new ArrayList<>();
		for (PositionHistory ph : getAllPositionHistory()){
			OrderResult askOr = orderResultRepository.findOne(ph.getAskClOrdId());
			OrderResult bidOr = orderResultRepository.findOne(ph.getBidClOrdId());
			BigDecimal profit = bidOr.getLastPx().multiply(bidOr.getLastQty()).subtract(askOr.getLastPx().multiply(askOr.getLastQty()));
			list.add(new ProfitReport(
							ph.getId(),
							profit,
							ph.getTrapPx(),
							askOr.getExecTime(),
							askOr.getLastPx(),
							askOr.getLastQty(),
							bidOr.getExecTime(),
							bidOr.getLastPx(),
							bidOr.getLastQty()));
		}
		return list;
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

	@Data
	@AllArgsConstructor
	public static class ProfitReport {
		final private Integer ポジション履歴ID;
		final private BigDecimal 利益;
		final private BigDecimal トラップ価格;
		final private Date 購入日時;
		final private BigDecimal 購入価格;
		final private BigDecimal 購入数量;
		final private Date 売却日時;
		final private BigDecimal 売却価格;
		final private BigDecimal 売却数量;
	}
}
