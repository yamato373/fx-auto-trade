package jp.yamato373.domain.service;

import jp.yamato373.domain.model.Rate;

public interface PriceService {

	Rate getRate(String symbol);

	void clearRate(String symbol);

	void clearAllRate();

	void setRate(String symbol, Rate rate);

}
