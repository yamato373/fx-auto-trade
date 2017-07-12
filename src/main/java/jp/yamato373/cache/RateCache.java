package jp.yamato373.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jp.yamato373.price.model.Rate;
import jp.yamato373.price.model.Rate.Entry;
import jp.yamato373.uitl.FxEnums.Side;

@Component
public class RateCache {

	Map<String, Rate> rateMap = new ConcurrentHashMap<>();

	public void set(String symbol, Rate rate) {
		rateMap.put(symbol, rate);
	}

	public Rate get(String symbol) {
		return rateMap.get(symbol);
	}

	public void clear(String symbol) {
		rateMap.remove(symbol);
	}

	public void clearAll() {
		rateMap.clear();
	}

	public Entry getEntry(String symbol, Side side) {
		if (Side.ASK.equals(side)) {
			return rateMap.get(symbol).getAskEntry();
		}
		return rateMap.get(symbol).getBidEntry();

	}
}
