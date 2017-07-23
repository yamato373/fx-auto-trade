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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
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
	Integer askClOrdId;

	/**
	 * 売り注文番号
	 */
	@Column
	@NotNull
	Integer bidClOrdId;

	/**
	 * 決済日時
	 */
	@Column
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	Date settlTime;


}