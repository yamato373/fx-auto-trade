package jp.yamato373.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jp.yamato373.order.model.OrderResult;

@Component
public class OrderResultCache {

	Map<String, OrderResult> orderResultMap = new ConcurrentHashMap<>();

	public void put(String clOrdId, OrderResult orderResult) {
		orderResultMap.put(clOrdId, orderResult);
	}

	public OrderResult get(String clOrdId) {
		return orderResultMap.get(clOrdId);
	}

	public Map<String, OrderResult> getAll() {
		return orderResultMap;
	}
}
