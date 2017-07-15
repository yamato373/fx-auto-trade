package jp.yamato373.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import jp.yamato373.uitl.FxEnums.Side;
import jp.yamato373.uitl.FxEnums.Status;
import lombok.Data;

@Data
public class OrderResult {

	/**
	 * ステータス
	 */
	Status status;

	/**
	 * クライアント注文ID
	 */
	String clOrdId;

	/**
	 * 通貨ペア
	 */
	String symbol;

	/**
	 * サイド
	 */
	Side side;

	/**
	 * 注文時刻
	 */
	Date orderTime;

	/**
	 * 注文数量
	 */
	BigDecimal orderQty;

	/**
	 * 注文価格
	 */
	BigDecimal price;

	/**
	 * CP側注文ID
	 */
	String orderId;

	/**
	 * CP側約定ID
	 */
	String execId;

	/**
	 * 約定時刻
	 */
	Date execTime;

	/**
	 * 約定数量
	 */
	BigDecimal lastQty;

	/**
	 * 約定価格
	 */
	BigDecimal lastPx;

	/**
	 * リジェクト理由コード
	 */
	Integer rejReason;

	public OrderResult(Status status, String clOrdId, String symbol, Side side, Date orderTime, BigDecimal orderQty,
			BigDecimal price) {
		this.status = status;
		this.clOrdId = clOrdId;
		this.symbol = symbol;
		this.side = side;
		this.orderTime = orderTime;
		this.orderQty = orderQty;
		this.price = price;
	}
}