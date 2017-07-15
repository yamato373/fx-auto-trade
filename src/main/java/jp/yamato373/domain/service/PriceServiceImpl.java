package jp.yamato373.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.repository.RateRepository;

@Service
public class PriceServiceImpl implements PriceService{

	@Autowired
	RateRepository rateRepository;

	@Override
	public Rate getRate(String symbol) {
		return rateRepository.get(symbol);
	}

	@Override
	public void clearRate(String symbol) {
		rateRepository.clear(symbol);
	}

	@Override
	public void clearAllRate() {
		rateRepository.clearAll();
	}

	@Override
	public void setRate(String symbol, Rate rate) {
		rateRepository.set(symbol, rate);
	}
}
