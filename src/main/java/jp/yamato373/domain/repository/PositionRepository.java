package jp.yamato373.domain.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.db.PositionTable;
import jp.yamato373.domain.model.entry.Position;

@Repository
public class PositionRepository {

	@Autowired
	PositionTable positionTable;

	public Position save(Position p) {
		return positionTable.save(p);
	}

	public List<Position> findAll(){
		return positionTable.findAll();
	}

	public Position findByBidClOrdId(String clOrdId) {
		return positionTable.findByBidClOrdId(clOrdId);
	}

	public Position findByAskClOrdId(String clOrdId) {
		return positionTable.findByAskClOrdId(clOrdId);
	}

	public void deleteByAskClOrdId(String clOrdId){
		positionTable.deleteByAskClOrdId(clOrdId);
	}

	public void deleteByBidClOrdId(String clOrdId){
		 positionTable.deleteByBidClOrdId(clOrdId);
	}
}
