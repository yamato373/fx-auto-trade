package jp.yamato373.domain.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.db.OrderResultTable;
import jp.yamato373.domain.model.entry.OrderResult;

@Repository
public class OrderResultRepository {

	@Autowired
	OrderResultTable orderResultTable;

	public OrderResult findOne(String clOrdId) {
		return orderResultTable.findOne(clOrdId);
	}

	public void save(OrderResult orderResult) {
		orderResultTable.save(orderResult);
	}

	public List<OrderResult> findAll() {
		return orderResultTable.findAll();
	}
}
