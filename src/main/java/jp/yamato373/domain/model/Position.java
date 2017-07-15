package jp.yamato373.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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

	/**
	 * 買い注文番号
	 */
	String askClOrderId;

	/**
	 * 売り番号
	 */
	String bidClOrderId;
}
