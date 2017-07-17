package jp.yamato373.db;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.entry.OrderResult;

/**
 * TODO 暫定対応。DBに移植する。
 *
 */
@Component
public class OrderResultTable {

	Set<OrderResult> orderResultTable = new HashSet<>();

	public void insert(OrderResult orderResult) {
		orderResultTable.add(orderResult);
	}

	public OrderResult findOne(String clOrdId) {
		OrderResult orderResult;
		try{
			orderResult = orderResultTable.stream().filter(or -> or.getClOrdId().equals(clOrdId)).findFirst().get();
		}catch (NoSuchElementException e) {
			return null;
		}
		return orderResult;
	}

	public Set<OrderResult> findAll() {
		return orderResultTable;
	}
}
