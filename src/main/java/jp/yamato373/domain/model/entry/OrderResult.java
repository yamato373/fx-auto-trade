package jp.yamato373.domain.model.entry;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import jp.yamato373.uitl.FxEnums.Side;
import jp.yamato373.uitl.FxEnums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderResult {

	/**
	 * クライアント注文ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer clOrdId;

	/**
	 * ステータス
	 */
	@Column
	@NotNull
	Status status;

	/**
	 * 通貨ペア
	 */
	@Column
	@NotNull
	String symbol;

	/**
	 * サイド
	 */
	@Column
	@NotNull
	Side side;

	/**
	 * 注文時刻
	 */
	@Column
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	Date orderTime;

	/**
	 * 注文数量
	 */
	@Column
	@NotNull
	BigDecimal orderQty;

	/**
	 * 注文価格
	 */
	@Column
	@NotNull
	BigDecimal price;

	/**
	 * CP側注文ID
	 */
	@Column
	String orderId;

	/**
	 * CP側約定ID
	 */
	@Column
	String execId;

	/**
	 * 約定時刻
	 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	Date execTime;

	/**
	 * 約定数量
	 */
	@Column
	BigDecimal lastQty;

	/**
	 * 約定価格
	 */
	@Column
	BigDecimal lastPx;

	/**
	 * リジェクト理由コード
	 */
	@Column
	Integer rejReason;

	public OrderResult(Status status, String symbol, Side side, Date orderTime, BigDecimal orderQty, BigDecimal price) {
		this.status = status;
		this.symbol = symbol;
		this.side = side;
		this.orderTime = orderTime;
		this.orderQty = orderQty;
		this.price = price;
	}
}