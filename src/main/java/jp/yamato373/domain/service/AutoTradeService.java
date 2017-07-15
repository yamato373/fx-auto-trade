package jp.yamato373.domain.service;

import java.math.BigDecimal;
import java.util.Map;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.model.Position;
import jp.yamato373.domain.model.Rate;

public interface AutoTradeService {

	Map<BigDecimal, Position> getAllPosition();

	/**
	 * オーダー結果をPositionに反映させる
	 *
	 * @param orderResult
	 */
	void addPosition(OrderResult orderResult);

	/**
	 * オーダーが必要ならオーダーする
	 *
	 * @param rate
	 * @param symbol
	 */
	void checkAndOrder(String symbol, Rate rate);

}
