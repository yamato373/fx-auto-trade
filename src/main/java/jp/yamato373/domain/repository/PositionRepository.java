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
			log.info("ポジションを追加をしました。position:" + positionTable.findOne(p.getTrapPx()));
			return p;
		}else{
			positionTable.update(p);
			log.info("ポジションを更新をしました。position:" + positionTable.findOne(p.getTrapPx()));
			return p;
		}
	}

	public List<Position> findAll(){
		return positionTable.findAll();
	}

	public Position findOne(BigDecimal trapPx) {
		Position p = positionTable.findOne(trapPx);

		if(p == null)log.error("BIF注文処理でポジションが無い。"); // TODO デバッグ用。消す。

		return p;
	}

	public Position findByBidClOrdId(String clOrdId) {
		return positionTable.findAll().stream().filter(p -> clOrdId.equals(p.getBidClOrdId())).findFirst().get();
	}

	public Position findByAskClOrdId(String clOrdId) {
		return positionTable.findAll().stream().filter(p -> clOrdId.equals(p.getAskClOrdId())).findFirst().get();
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
