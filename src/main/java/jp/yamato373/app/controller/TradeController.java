package jp.yamato373.app.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.domain.model.entry.Position;
import jp.yamato373.domain.model.entry.PositionHistory;
import jp.yamato373.domain.service.AutoTradeService;
import jp.yamato373.domain.service.AutoTradeService.ProfitReport;
import jp.yamato373.domain.service.shared.OrderService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TradeController {

	@Autowired
	OrderService orderService;

	@Autowired
	AutoTradeService autoTradeService;

	@RequestMapping(value = "/trade/positions", method = RequestMethod.GET)
	public Collection<Position> getPositions() {
		log.info("ポジション取得API実行。");
		return autoTradeService.getAllPosition();
	}

	@RequestMapping(value = "/trade/positionHistory", method = RequestMethod.GET)
	public Collection<PositionHistory> getPositionHistory() {
		log.info("ポジション履歴取得API実行。");
		return autoTradeService.getAllPositionHistory();
	}

	@RequestMapping(value = "/trade/profitReport", method = RequestMethod.GET)
	public Collection<ProfitReport> getProfitReport() {
		log.info("利益報告API実行。");
		return autoTradeService.profitReport();
	}
}