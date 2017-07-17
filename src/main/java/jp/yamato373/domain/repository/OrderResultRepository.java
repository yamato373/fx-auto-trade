package jp.yamato373.domain.repository;

import java.util.Set;

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
		OrderResult or = orderResultTable.findOne(orderResult.getClOrdId());
		if (or == null){
			orderResultTable.insert(orderResult);
		}else{
			or.setExecId(orderResult.getExecId());
			or.setExecTime(orderResult.getExecTime());
			or.setLastPx(orderResult.getLastPx());
			or.setLastQty(orderResult.getLastQty());
			or.setOrderId(orderResult.getOrderId());
			or.setRejReason(orderResult.getRejReason());
		}
	}

	public Set<OrderResult> findAll() {
		return orderResultTable.findAll();
	}

}
