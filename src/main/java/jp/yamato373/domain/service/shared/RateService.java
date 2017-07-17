package jp.yamato373.domain.service.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.cache.RateCache;

@Service
public class RateService {

	@Autowired
	RateCache rateCache;

	public void setRate(Rate rate) {
		rateCache.set(rate);
	}

	public Rate getRate() {
		return rateCache.get();
	}

	public void clearRate() {
		rateCache.clear();
	}
}
