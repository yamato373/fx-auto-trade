package jp.yamato373.price.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.dataaccess.RateDao;
import jp.yamato373.price.model.Rate;

@Service
public class PriceService {

	@Autowired
	RateDao rateDao;

	public Rate getRate(String symbol) {
		return rateDao.get(symbol);
	}

	public void clearRate(String symbol) {
		rateDao.clear(symbol);
	}

	public void clearAllRate() {
		rateDao.clearAll();
	}

	public void setRate(String symbol, Rate rate) {
		rateDao.set(symbol, rate);
	}
}
