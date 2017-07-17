package jp.yamato373.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.db.PositionTable;
import jp.yamato373.domain.model.entry.Position;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PositionRepository {

	@Autowired
	PositionTable positionTable;

	public Position save(Position p) {

		Position position = positionTable.findOne(p.getTrapPx());
		if (position == null){
			positionTable.insert(p);
			log.info("ポジションを追加をしました。position" + p);
		}else{
			position.setAskClOrdId(p.getAskClOrdId());
			position.setBidClOrdId(p.getBidClOrdId());
			position.setAskOrderResult(p.getAskOrderResult());
			position.setBidOrderResult(p.getBidOrderResult());
			log.info("ポジションを更新(bidClOrderId,BidOrderResult)をしました。position" + p);
		}
		return position;
	}

	public List<Position> findAll(){
		return positionTable.findAll();
	}

	public Position findOne(BigDecimal trapPx) {
		return positionTable.findOne(trapPx);
	}

	public Position findByBidClOrdId(String clOrdId) {
		return positionTable.findAll().stream().filter(p -> p.getBidClOrdId().equals(clOrdId)).findFirst().get();
	}

	public Position findByAskClOrdId(String clOrdId) {
		return positionTable.findAll().stream().filter(p -> p.getAskClOrdId().equals(clOrdId)).findFirst().get();
	}

	public void deleteByAskClOrdId(String clOrdId){
		boolean result = positionTable.removeByAskClOrdId(clOrdId);
		if (result){
			log.info("ポジションを削除をしました。clOrdId:" + clOrdId);
		}else{
			log.error("ポジションの削除を試みましたが既にありませんでした。clOrdId:" + clOrdId);
		}
	}

	public void deleteByBidClOrdId(String clOrdId){
		boolean result = positionTable.removeByBidClOrdId(clOrdId);
		if (result){
			log.info("ポジションを削除をしました。clOrdId:" + clOrdId);
		}else{
			log.error("ポジションの削除を試みましたが既にありませんでした。clOrdId:" + clOrdId);
		}
	}
}
