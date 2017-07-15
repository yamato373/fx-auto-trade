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

	public Position get(BigDecimal trapPx){
		return positionMap.get(trapPx);
	}

	public void add(Position position){
		positionMap.put(position.getTrapPx(), position);
	}

	public Position remove(BigDecimal trapPx){
		return positionMap.remove(trapPx);
	}

	public Position removeByClOrderId(String clOrdId) {
		for (Position p : positionMap.values()){
			if(clOrdId.equals(p.getBidClOrderId())){
				return positionMap.remove(p.getTrapPx());
			}
		}
		return null;
	}
}
