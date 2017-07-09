package jp.yamato373.order.model;

import java.util.Date;

import jp.yamato373.uitll.FxEnums.Side;
import jp.yamato373.uitll.FxEnums.Status;
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
	Double orderQty;

	/**
	 * 注文価格
	 */
	Double price;

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
	Double lastQty;

	/**
	 * 約定価格
	 */
	Double lastPx;

	/**
	 * リジェクト理由コード
	 */
	Integer rejReason;

	public OrderResult(Status status, String clOrdId, String symbol, Side side, Date orderTime, Double orderQty,
			Double price) {
		this.status = status;
		this.clOrdId = clOrdId;
		this.symbol = symbol;
		this.side = side;
		this.orderTime = orderTime;
		this.orderQty = orderQty;
		this.price = price;
	}
}