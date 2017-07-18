package jp.yamato373.db;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import jp.yamato373.domain.model.entry.OrderResult;

/**
 * TODO 暫定対応。DBに移植する。
 *
 */
@Component
public class OrderResultTable {

	List<OrderResult> orderResultTable = new ArrayList<>();

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

	public List<OrderResult> findAll() {
		return orderResultTable;
	}

	public void save(OrderResult orderResult) {
		OrderResult or = findOne(orderResult.getClOrdId());
		if (or == null){
			insert(orderResult);
		}else{
			or.setExecId(orderResult.getExecId());
			or.setExecTime(orderResult.getExecTime());
			or.setLastPx(orderResult.getLastPx());
			or.setLastQty(orderResult.getLastQty());
			or.setOrderId(orderResult.getOrderId());
			or.setRejReason(orderResult.getRejReason());
		}
	}

	public OrderResult findByAskOrdId(String askClOrdId) {
		return findAll().stream().filter(or -> or.getClOrdId().equals(askClOrdId)).findFirst().get();
	}
}
