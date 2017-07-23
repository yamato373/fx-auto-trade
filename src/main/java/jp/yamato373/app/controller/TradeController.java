package jp.yamato373.app.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
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
		return autoTradeService.getProfitReport();
	}

	@RequestMapping(value = "/trade/profit/all", method = RequestMethod.GET)
	public BigDecimal getProfitAll() {
		log.info("全利益API実行。");
		return autoTradeService.getProfitAll();
	}

	@RequestMapping(value = "/trade/profit/today", method = RequestMethod.GET)
	public BigDecimal getProfitToday() {
		log.info("今日の利益API実行。");
		Date now = new Date();
		return autoTradeService.getProfitByDate(DateUtils.truncate(now, Calendar.DAY_OF_MONTH), now);
	}

	@RequestMapping(value = "/trade/profit/yesterday", method = RequestMethod.GET)
	public BigDecimal getProfitYesterday() {
		log.info("昨日の利益API実行。");
		Date now = new Date();
		;
		return autoTradeService.getProfitByDate(
				DateUtils.truncate(DateUtils.addDays(now, -1), Calendar.DAY_OF_MONTH),
				DateUtils.truncate(now, Calendar.DAY_OF_MONTH));
	}
}