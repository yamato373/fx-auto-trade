package jp.yamato373.dataaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.cache.RateCache;
import jp.yamato373.price.model.Rate;
import jp.yamato373.price.model.Rate.Entry;
import jp.yamato373.uitl.FxEnums.Side;

@Repository
public class RateDao {

	@Autowired
	RateCache rateCache;

	public void set(String symbol, Rate rate) {
		rateCache.set(symbol, rate);
	}

	public Rate get(String symbol) {
		return rateCache.get(symbol);
	}

	public void clear(String symbol) {
		rateCache.clear(symbol);
	}

	public void clearAll() {
		rateCache.clearAll();
	}

	public Entry getEntry(String symbol, Side side) {
		return rateCache.getEntry(symbol, side);
	}
}
