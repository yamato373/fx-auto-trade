package jp.yamato373.app.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.model.Position;
import jp.yamato373.domain.service.AutoTradeServiceImpl;
import jp.yamato373.domain.service.shared.OrderServiceImpl;
import jp.yamato373.uitl.FxEnums.Side;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderController {

	@Autowired
	OrderServiceImpl orderService;

	@Autowired
	AutoTradeServiceImpl tradeService;

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public OrderResult order(
			@RequestParam("cp") String cp,
			@RequestParam("symbol") String symbol,
			@RequestParam("side") String side,
			@RequestParam("amt") BigDecimal amt) {

		log.info("注文API実行 cp:" + cp + " symbol:" + symbol + " side:" + Side.valueOf(side) + " amt:" + amt);

		return orderService.order(cp, symbol, Side.valueOf(side), amt);
	}

	@RequestMapping(value = "/order/history", method = RequestMethod.GET)
	public Map<String, OrderResult> getOrderHistory() {

		log.info("注文履歴取得API実行");

		return orderService.getOrderResultAll();
	}

	@RequestMapping(value = "/order/positions", method = RequestMethod.GET)
	public Map<BigDecimal,Position> getpositions() {

		log.info("ポジション取得API実行");

		return tradeService.getAllPosition();
	}
}
