package jp.yamato373.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 1バンド分のレート情報
 *
 */
@Data
public class Rate {

	/**
	 * 通貨ペア
	 */
	String symbol;

	/**
	 * 送信時間
	 *
	 * <p>
	 * 受信ごとにアップデートする
	 */
	Date SendingTime;

	/**
	 * ASKのプライス
	 */
	Entry askEntry;

	/**
	 * BIDのプライス
	 */
	Entry bidEntry;

	public Rate() {
	}

	public Rate(String symbol, Date sendingTime) {
		this.symbol = symbol;
		this.SendingTime = sendingTime;
	}

	@Data
	public static class Entry {

		/**
		 * サイド
		 *
		 * 0 = Bid（売り) 1 = Offer（買い）
		 */
//		int side; TODO いる？

		/**
		 * 値段
		 */
		BigDecimal px;

		/**
		 * 数量
		 */
		BigDecimal amt;

		/**
		 * 気配値
		 */
		boolean indicative;
	}
}