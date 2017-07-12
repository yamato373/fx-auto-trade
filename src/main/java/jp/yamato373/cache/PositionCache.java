package jp.yamato373.cache;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import jp.yamato373.trade.model.Position;

@Component
public class PositionCache {

	Set<Position> positionSet = new HashSet<>();

	public Set<Position> getAll(){
		return positionSet;
	}

	public void add(Position position){
		positionSet.add(position);
	}

	public void remove(Position position){
		positionSet.remove(position);
	}

}
