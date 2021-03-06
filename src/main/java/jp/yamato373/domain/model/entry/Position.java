package jp.yamato373.domain.model.entry;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
	Integer askClOrdId;

	/**
	 * 売り注文番号
	 */
	@Column
	Integer bidClOrdId;

	/**
	 * 買い注文中フラグ
	 */
	@Column
	boolean buyingFlg;
}