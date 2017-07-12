package jp.yamato373.trade.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Position {

	/**
	 * トラップ価格
	 */
	BigDecimal trapPx;

	/**
	 * 約定価格（買い）
	 */
	BigDecimal boughtPx;

	/**
	 * 約定数量
	 */
	BigDecimal agreedAmt;
}
