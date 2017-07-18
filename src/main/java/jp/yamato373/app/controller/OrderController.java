package jp.yamato373.app.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.domain.model.entry.OrderResult;
import jp.yamato373.domain.service.AutoTradeService;
import jp.yamato373.domain.service.shared.OrderService;
import jp.yamato373.uitl.FxEnums.Side;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderController {

	@Autowired
	OrderService orderService;

	@Autowired
	AutoTradeService tradeService;

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public OrderResult order(@RequestParam("side") String side, @RequestParam("amt") BigDecimal amt) {
		log.info("注文API実行。side:" + Side.valueOf(side) + "、amt:" + amt);
		return orderService.order(Side.valueOf(side), amt);
	}

	@RequestMapping(value = "/order/history", method = RequestMethod.GET)
	public List<OrderResult> getOrderHistory() {
		log.info("注文履歴取得API実行。");
		List<OrderResult> list = orderService.getOrderResultAll();
		list.sort((a, b) -> a.getClOrdId().compareTo(b.getClOrdId()));
		return list;
	}
}