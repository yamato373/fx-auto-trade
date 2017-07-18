package jp.yamato373.domain.model.cache;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.entry.Position;
import jp.yamato373.domain.repository.PositionRepository;

@Component
public class PositionCache {

	@Autowired
	PositionRepository positionRepository;

	private Map<BigDecimal, Position> positionMap = new ConcurrentHashMap<>();

	public Set<BigDecimal> getTrapPxSet() {
		return positionMap.keySet();
	}

	public Collection<Position> getPositionSet() {
		return positionMap.values();
	}

	public boolean notContains(BigDecimal trapPx) {
		return !positionMap.containsKey(trapPx);
	}

	public BigDecimal getFirst(){
		if (positionMap.isEmpty()){
			return null;
		}
		BigDecimal firstKey = null;
		for (BigDecimal b : positionMap.keySet()) {
			if (firstKey == null){
				firstKey = b;
			}
			if (b.compareTo(firstKey) < 0){
				firstKey = b;
			}
		}
		return firstKey;
	}

	public void refresh(){
		positionMap.clear();
		positionRepository.findAll().forEach(p -> {
			positionMap.put(p.getTrapPx(), p);
		});
	}
}
