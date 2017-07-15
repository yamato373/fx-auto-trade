package jp.yamato373.domain.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.model.cache.OrderResultCache;

@Repository
public class OrderResultRepositoryImpl implements OrderResultRepository{

	@Autowired
	OrderResultCache orderResultCache;

	@Override
	public OrderResult get(String clOrdId) {
		return orderResultCache.get(clOrdId);
	}

	@Override
	public void put(String clOrdId, OrderResult orderResult) {
		orderResultCache.put(clOrdId, orderResult);
	}

	@Override
	public Map<String, OrderResult> getAll() {
		return orderResultCache.getAll();
	}

}
