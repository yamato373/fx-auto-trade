package jp.yamato373.app.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.domain.model.entry.Position;
import jp.yamato373.domain.service.AutoTradeService;
import jp.yamato373.domain.service.shared.OrderService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TradeController {

	@Autowired
	OrderService orderService;

	@Autowired
	AutoTradeService tradeService;

	@RequestMapping(value = "/trade/positions", method = RequestMethod.GET)
	public Collection<Position> getpositions() {
		log.info("ポジション取得API実行。");
		List<Position> list = tradeService.getAllPosition();
		list.sort((a, b) -> a.getTrapPx().compareTo(b.getTrapPx()));
		return list;
	}
}