package jp.yamato373.order.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.dataaccess.OrderResultDao;
import jp.yamato373.dataaccess.RateDao;
import jp.yamato373.fix.service.FixOrderService;
import jp.yamato373.order.model.OrderResult;
import jp.yamato373.uitll.FxEnums;
import jp.yamato373.uitll.FxEnums.Side;
import jp.yamato373.uitll.FxEnums.Status;
import lombok.extern.slf4j.Slf4j;
import quickfix.FieldNotFound;
import quickfix.fix44.ExecutionReport;

@Service
@Slf4j
public class OrderService {

	@Autowired
	RateDao rateDao;

	@Autowired
	OrderResultDao orderResultDao;

	@Autowired
	FixOrderService fixOrderService;

	private int clOrdIdCounterr = 0;

	public OrderResult order(String cp, String symbol, Side side, Double amt) {

		String clOrdID = generatClOrdId();
		Double price = rateDao.getEntry(symbol, side).getPx();
		OrderResult orderResult = new OrderResult(Status.BEFORE, clOrdID, symbol, side, new Date(), amt, price);
		orderResultDao.put(clOrdID, orderResult);

		fixOrderService.sendOrder(orderResult);

		orderResult.setStatus(Status.ORDER);
		orderResultDao.put(clOrdID, orderResult);

		return orderResult;
	}

	public void report(ExecutionReport executionReport) throws FieldNotFound {

		String clOrdId = executionReport.getClOrdID().getValue();
		OrderResult orderResult = orderResultDao.get(clOrdId);

		Status status = FxEnums.getStatus(executionReport.getOrdStatus().getValue());

		orderResult.setStatus(status);
		orderResult.setOrderId(executionReport.getOrderID().getValue());
		orderResult.setExecId(executionReport.getExecID().getValue());
		orderResult.setLastQty(executionReport.getLastQty().getValue());
		orderResult.setLastPx(executionReport.getLastPx().getValue());
		orderResult.setExecTime(executionReport.getTransactTime().getValue());
		if (Status.REJECT.equals(status)){
			orderResult.setRejReason(executionReport.getOrdRejReason().getValue());
		}

		orderResultDao.put(clOrdId, orderResult);

		log.info("注文結果を格納したよ！"+ orderResult);
	}

	public Map<String, OrderResult> getOrderResultAll() {
		return orderResultDao.getAll();
	}

	private String generatClOrdId() {
		return Integer.toString(clOrdIdCounterr++); // TODO 永続的なシーケンスにする
	}
}
