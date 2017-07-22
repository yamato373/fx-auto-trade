package jp.yamato373.domain.model.entry;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionHistory {

	/**
	 * ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	/**
	 * トラップ価格
	 */
	@Column
	@NotNull
	BigDecimal trapPx;

	/**
	 * 買い注文番号
	 */
	@Column
	@NotNull
	String askClOrdId;

	/**
	 * 売り注文番号
	 */
	@Column
	@NotNull
	String bidClOrdId;
}