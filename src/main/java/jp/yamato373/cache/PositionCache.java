package jp.yamato373.cache;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import jp.yamato373.trade.model.Position;

@Component
public class PositionCache {

	Map<BigDecimal, Position> positionMap = new HashMap<>();

	public Map<BigDecimal, Position> getAll(){
		return positionMap;
	}

	public void add(Position position){
		positionMap.put(position.getTrapPx(), position);
	}

	public void remove(BigDecimal trapPx){
		positionMap.remove(trapPx);
	}
}
