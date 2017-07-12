package jp.yamato373.dataaccess;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.cache.PositionCache;
import jp.yamato373.trade.model.Position;

@Repository
public class PositionDao {

	@Autowired
	PositionCache positionCache;

	public Set<Position> getAll(){
		return positionCache.getAll();
	}

	

}
