package jp.yamato373.domain.model.entry;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Position {

	/**
	 * トラップ価格
	 */
	@Id
	BigDecimal trapPx;

	/**
	 * 買い注文番号
	 */
	@Column
	@NotNull
	// TODO 適当。調べる。@JoinColumn(name = "order_result_cl_ord_id")
	String askClOrdId;

	/**
	 * 売り注文番号
	 */
	@Column
	// TODO 適当。調べる。@JoinColumn(name = "order_result_cl_ord_id")
	String bidClOrdId;

	@OneToOne
	// TODO 適当。調べる。@JoinColumn(name="order_result_cl_ord_id", referencedColumnName="cl_ord_id", insertable=false, updatable=false)
	OrderResult askOrderResult;

	@OneToOne
	// TODO 適当。調べる。@JoinColumn(name="order_result_cl_ord_id", referencedColumnName="cl_ord_id", insertable=false, updatable=false)
	OrderResult bidOrderResult;


	public Position(BigDecimal trapPx, String askClOrdId, OrderResult orderResult) {
		this.trapPx = trapPx;
		this.askClOrdId = askClOrdId;
		this.askOrderResult = orderResult;
	}
}