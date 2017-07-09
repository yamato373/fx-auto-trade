package jp.yamato373.fix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.fix.model.OrderSender;
import jp.yamato373.order.model.OrderResult;

@Service
public class FixOrderService {

	@Autowired
	OrderSender orderSender;

	public void sendOrder(OrderResult orderResult) {
		orderSender.sendNewOrderSingle(orderResult);
	}

}
