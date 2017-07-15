package jp.yamato373.domain.service.shared;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.model.Position;
import jp.yamato373.domain.model.fix.OrderSender;
import jp.yamato373.domain.repository.OrderResultRepository;
import jp.yamato373.domain.repository.PositionRepository;
import jp.yamato373.domain.repository.RateRepository;
import jp.yamato373.uitl.FxEnums;
import jp.yamato373.uitl.FxEnums.Side;
import jp.yamato373.uitl.FxEnums.Status;
import lombok.extern.slf4j.Slf4j;
import quickfix.FieldNotFound;
import quickfix.fix44.ExecutionReport;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{

	@Autowired
	RateRepository rateRepository;

	@Autowired
	OrderResultRepository orderResultRepository;

	@Autowired
	PositionRepository positionRepository;

	@Autowired
	OrderSender orderSender;

	private int clOrdIdCounterr = 0;

	@Override
	public OrderResult order(String cp, String symbol, Side side, BigDecimal amt) {
		BigDecimal price = rateRepository.getEntry(symbol, side).getPx();
		OrderResult orderResult = new OrderResult(Status.BEFORE, generatClOrdId(), symbol, side, new Date(), amt, price);
		return order(orderResult);
	}

	@Override
	public OrderResult order(String cp, String symbol, Side side, BigDecimal amt, BigDecimal price) {
		OrderResult orderResult = new OrderResult(Status.BEFORE, generatClOrdId(), symbol, side, new Date(), amt, price);
		return order(orderResult);
	}

	@Override
	public OrderResult bidOrder(String cp, String symbol, Position position) {
		BigDecimal price = rateRepository.getEntry(symbol, Side.BID).getPx();
		OrderResult orderResult = new OrderResult(Status.BEFORE, generatClOrdId(), symbol, Side.BID, new Date(), position.getAgreedAmt(), price);
		positionRepository.setBidOrder(position.getTrapPx(), orderResult.getClOrdId());
		return order(orderResult);
	}

	@Override
	public OrderResult report(ExecutionReport executionReport) throws FieldNotFound {

		String clOrdId = executionReport.getClOrdID().getValue();
		OrderResult orderResult = orderResultRepository.get(clOrdId);

		Status status = FxEnums.getStatus(executionReport.getOrdStatus().getValue());

		orderResult.setStatus(status);
		orderResult.setOrderId(executionReport.getOrderID().getValue());
		orderResult.setExecId(executionReport.getExecID().getValue());
		orderResult.setLastQty(BigDecimal.valueOf(executionReport.getLastQty().getValue()));
		orderResult.setLastPx(BigDecimal.valueOf(executionReport.getLastPx().getValue()));
		orderResult.setExecTime(executionReport.getTransactTime().getValue());
		if (Status.REJECT.equals(status)){
			orderResult.setRejReason(executionReport.getOrdRejReason().getValue());
		}

		orderResultRepository.put(clOrdId, orderResult);
		log.info("注文結果格納"+ orderResult);

		return orderResult;
	}

	@Override
	public Map<String, OrderResult> getOrderResultAll() {
		return orderResultRepository.getAll();
	}

	private OrderResult order(OrderResult orderResult) {
		orderResultRepository.put(orderResult.getClOrdId(), orderResult);

		orderSender.sendNewOrderSingle(orderResult);

		orderResult.setStatus(Status.ORDER);
		orderResultRepository.put(orderResult.getClOrdId(), orderResult);

		return orderResult;
	}

	private String generatClOrdId() {
		return Integer.toString(clOrdIdCounterr++); // TODO 永続的なシーケンスにする
	}
}
