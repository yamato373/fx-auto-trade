package jp.yamato373.database;

import java.util.HashSet;
import java.util.Set;

import jp.yamato373.trade.model.Position;

// TODO DBを用意するまでの代わり
public class PositionTable {

	Set<Position> positionTable = new HashSet<>();

	public void insert(Position position){
		positionTable.add(position);
	}

	public void delete(Position position){
		positionTable.remove(position);
	}

}
