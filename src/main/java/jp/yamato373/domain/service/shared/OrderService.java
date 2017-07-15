package jp.yamato373.domain.service.shared;

import java.math.BigDecimal;
import java.util.Map;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.model.Position;
import jp.yamato373.uitl.FxEnums.Side;
import quickfix.FieldNotFound;
import quickfix.fix44.ExecutionReport;

public interface OrderService {

	/**
	 * 最新のプライスでオーダーする
	 *
	 * @param cp
	 * @param symbol
	 * @param side
	 * @param amt
	 * @return
	 */
	OrderResult order(String cp, String symbol, Side side, BigDecimal amt);

	/**
	 * プライスを指定してオーダーする
	 *
	 * @param cp
	 * @param symbol
	 * @param side
	 * @param amt
	 * @param price
	 * @return
	 */
	OrderResult order(String cp, String symbol, Side side, BigDecimal amt, BigDecimal price);


	/**
	 * ポジションを指定して売りオーダーする
	 *
	 * @param cp
	 * @param symbol
	 * @param position
	 * @return
	 */
	OrderResult bidOrder(String cp, String symbol, Position position);

	/**
	 * オーダー結果受信処理
	 *
	 * @param executionReport
	 * @return orderResult
	 * @throws FieldNotFound
	 */
	OrderResult report(ExecutionReport executionReport) throws FieldNotFound;

	/**
	 * 全注文履歴
	 *
	 * @return
	 */
	Map<String, OrderResult> getOrderResultAll();

}
