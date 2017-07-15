package jp.yamato373.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.service.PriceService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PriceController {

	@Autowired
	PriceService priceService;

	@RequestMapping(value = "/price", method = RequestMethod.GET)
	public Rate getPrice(@RequestParam("cp") String cp, @RequestParam("symbol") String symbol) {

		log.info("プライス取得実行");

		return priceService.getRate(symbol);
	}
}
