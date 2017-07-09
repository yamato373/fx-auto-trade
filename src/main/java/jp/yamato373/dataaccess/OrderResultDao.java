package jp.yamato373.dataaccess;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.cache.OrderResultCache;
import jp.yamato373.order.model.OrderResult;

@Repository
public class OrderResultDao {

	@Autowired
	OrderResultCache orderResultCache;

	public OrderResult get(String clOrdId) {
		return orderResultCache.get(clOrdId);
	}

	public void put(String clOrdId, OrderResult orderResult) {
		orderResultCache.put(clOrdId, orderResult);
	}

	public Map<String, OrderResult> getAll() {
		return orderResultCache.getAll();
	}

}
