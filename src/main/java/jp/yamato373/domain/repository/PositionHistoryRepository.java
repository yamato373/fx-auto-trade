package jp.yamato373.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.db.PositionHistoryTable;
import jp.yamato373.domain.model.entry.PositionHistory;

@Repository
public class PositionHistoryRepository {

	@Autowired
	PositionHistoryTable positionHistoryTable;

	public PositionHistory save(PositionHistory ph) {
		return positionHistoryTable.save(ph);
	}

	public List<PositionHistory> findAll(){
		return positionHistoryTable.findAll();
	}

	public PositionHistory findByTrapPx(BigDecimal trapPx) {
		return positionHistoryTable.findByTrapPx(trapPx);
	}

	public PositionHistory findByBidClOrdId(String clOrdId) {
		return positionHistoryTable.findByBidClOrdId(clOrdId);
	}

	public PositionHistory findByAskClOrdId(String clOrdId) {
		return positionHistoryTable.findByAskClOrdId(clOrdId);
	}
}
