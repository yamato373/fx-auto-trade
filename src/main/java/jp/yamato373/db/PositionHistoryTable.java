package jp.yamato373.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.entry.PositionHistory;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO 暫定対応。DBに移植する。
 *
 */
@Component
@Slf4j
public class PositionHistoryTable {

	List<PositionHistory> positionHistoryTable = new ArrayList<>();

	public PositionHistory save(PositionHistory ph) {
		positionHistoryTable.add(ph);
		log.info("ポジション履歴を追加をしました。PositionHistory:" + ph);
		return ph;
	}

	public List<PositionHistory> findAll() {
		return positionHistoryTable;
	}

	public PositionHistory findByTrapPx(BigDecimal trapPx) {
		return positionHistoryTable.stream()
				.filter(ph -> trapPx.compareTo(ph.getTrapPx()) == 0)
				.findFirst()
				.get();
	}

	public PositionHistory findByBidClOrdId(String clOrdId) {
		return findAll().stream().filter(ph -> clOrdId.equals(ph.getBidClOrdId())).findFirst().get();
	}

	public PositionHistory findByAskClOrdId(String clOrdId) {
		return findAll().stream().filter(ph -> clOrdId.equals(ph.getAskClOrdId())).findFirst().get();
	}
}
