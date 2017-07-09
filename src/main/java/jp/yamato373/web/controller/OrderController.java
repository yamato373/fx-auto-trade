package jp.yamato373.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.order.model.OrderResult;
import jp.yamato373.order.service.OrderService;
import jp.yamato373.uitll.FxEnums.Side;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderController {

	@Autowired
	OrderService orderService;

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public OrderResult order(
			@RequestParam("cp") String cp,
			@RequestParam("symbol") String symbol,
			@RequestParam("side") String side,
			@RequestParam("amt") Double amt) {

		log.info("注文APIが叩かれたよ！cp=" + cp + " symbol=" + symbol + " side=" + Side.valueOf(side) + " amt=" + amt);

		return orderService.order(cp, symbol, Side.valueOf(side), amt);
	}

	@RequestMapping(value = "/order/history", method = RequestMethod.GET)
	public Map<String, OrderResult> getOrderHistory() {

		log.info("注文履歴APIが叩かれたよ！");

		return orderService.getOrderResultAll();
	}
}
