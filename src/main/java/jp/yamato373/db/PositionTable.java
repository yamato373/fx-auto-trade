package jp.yamato373.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.entry.Position;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO 暫定対応。DBに移植する。
 *
 */
@Component
@Slf4j
public class PositionTable {

	List<Position> positionTable = new ArrayList<>();

	public List<Position> findAll(){
		return positionTable;
	}

	public Position findOne(BigDecimal trapPx){
		Position position;
		try{
			position =  positionTable.stream().filter(p -> p.getTrapPx().compareTo(trapPx) == 0).findFirst().get();
		}catch (NoSuchElementException e) {
			return null;
		}
		return position;
	}

	public void insert(Position position){
		positionTable.add(position);
	}

	public void update(Position position) {
		Iterator<Position> iterator = positionTable.iterator();
		while (iterator.hasNext()) {
		    Position p = iterator.next();
		    if (p.getTrapPx().equals(position.getTrapPx())) {
		        iterator.remove();
		    }
		}
		positionTable.add(position);
	}

	public boolean deleteByAskClOrdId(String askClOrdId){
		Position position;
		try{
			position =  positionTable.stream().filter(p -> askClOrdId.equals(p.getAskClOrdId())).findFirst().get();
		}catch (NoSuchElementException e) {
			return false;
		}
		return positionTable.remove(position);
	}

	public boolean deleteByBidClOrdId(String bidClOrdId) {
		Position position;
		try{
			position =  positionTable.stream().filter(p -> bidClOrdId.equals(p.getBidClOrdId())).findFirst().get();
		}catch (NoSuchElementException e) {
			return false;
		}
		return positionTable.remove(position);
	}

	public Position save(Position p) {
		Position position = findOne(p.getTrapPx());
		if (position == null){
			insert(p);
			log.info("ポジションを追加をしました。position:" + findOne(p.getTrapPx()));
		}else{
			update(p);
			log.info("ポジションを更新をしました。position:" + findOne(p.getTrapPx()));
		}
		return p;
	}

	public Position findByBidClOrdId(String clOrdId) {
		return findAll().stream().filter(p -> clOrdId.equals(p.getBidClOrdId())).findFirst().get();
	}

	public Position findByAskClOrdId(String clOrdId) {
		return findAll().stream().filter(p -> clOrdId.equals(p.getAskClOrdId())).findFirst().get();
	}
}
