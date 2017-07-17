package jp.yamato373.domain.model.cache;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

@Component
public class PositionPxCache {

	TreeSet<BigDecimal> positionPxSet = new TreeSet<>();

	public Set<BigDecimal> getPositionPxSet(){
		return positionPxSet;
	}

	public boolean addPositionPx(BigDecimal trapPx){
		return positionPxSet.add(trapPx);
	}

	public boolean removePositionPx(BigDecimal trapPx){
		return positionPxSet.remove(trapPx);
	}

	public boolean notContainsPositionPx(BigDecimal trapPx){
		return !positionPxSet.contains(trapPx);
	}

	public BigDecimal getFirst(){
		if (positionPxSet.isEmpty()){
			return null;
		}
		return positionPxSet.first();
	}
}
