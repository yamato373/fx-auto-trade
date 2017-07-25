package jp.yamato373.domain.model.cache;

import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.Rate.Entry;
import jp.yamato373.uitl.FxEnums.Side;

@Component
public class RateCache {

	private Rate rate;

	public void setRate(Rate rate) {
		this.rate = rate;
	}

	public Rate getRate() {
		return rate;
	}

	public void clearRate() {
		this.rate = null;
	}

	public Entry getEntry(Side side) {
		if (Side.ASK.equals(side)) {
			return rate.getAskEntry();
		}
		return rate.getBidEntry();
	}
}
