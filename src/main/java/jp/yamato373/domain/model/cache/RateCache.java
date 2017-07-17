package jp.yamato373.domain.model.cache;

import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.Rate.Entry;
import jp.yamato373.uitl.FxEnums.Side;

@Component
public class RateCache {

	private Rate rate;

	public void set(Rate rate) {
		this.rate = rate;
	}

	public Rate get() {
		return rate;
	}

	public void clear() {
		this.rate = null;
	}

	public Entry getEntry(Side side) {
		if (Side.ASK.equals(side)) {
			return rate.getAskEntry();
		}
		return rate.getBidEntry();
	}
}
