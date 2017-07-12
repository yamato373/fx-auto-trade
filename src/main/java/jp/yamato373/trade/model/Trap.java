package jp.yamato373.trade.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor // finalのフィールドだけのコンストラクタ
public class Trap {

	/**
	 * トラップが動作するプライス
	 */
	final double trapPrice;

	/**
	 * ポジションの保持状態
	 */
	boolean positionFlg;

}
