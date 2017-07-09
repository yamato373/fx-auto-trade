package jp.yamato373.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.price.model.Rate;
import jp.yamato373.price.service.PriceService;

@RestController
public class PriceController {

	@Autowired
	PriceService priceService;

	@RequestMapping(value = "/price", method = RequestMethod.GET)
	public Rate getPrice(@RequestParam("cp") String cp, @RequestParam("symbol") String symbol) {

		return priceService.getRate(symbol);
	}
}
