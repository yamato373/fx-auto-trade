package jp.yamato373.domain.repository;

import java.util.Map;

import jp.yamato373.domain.model.OrderResult;

public interface OrderResultRepository {

	Map<String, OrderResult> getAll();

	void put(String clOrdId, OrderResult orderResult);

	OrderResult get(String clOrdId);

}
