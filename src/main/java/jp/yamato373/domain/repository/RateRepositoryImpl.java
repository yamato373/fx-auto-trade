package jp.yamato373.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.Rate.Entry;
import jp.yamato373.domain.model.cache.RateCache;
import jp.yamato373.uitl.FxEnums.Side;

@Repository
public class RateRepositoryImpl implements RateRepository{

	@Autowired
	RateCache rateCache;

	@Override
	public void set(String symbol, Rate rate) {
		rateCache.set(symbol, rate);
	}

	@Override
	public Rate get(String symbol) {
		return rateCache.get(symbol);
	}

	@Override
	public void clear(String symbol) {
		rateCache.clear(symbol);
	}

	@Override
	public void clearAll() {
		rateCache.clearAll();
	}

	@Override
	public Entry getEntry(String symbol, Side side) {
		return rateCache.getEntry(symbol, side);
	}
}
