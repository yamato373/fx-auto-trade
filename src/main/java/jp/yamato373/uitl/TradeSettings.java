package jp.yamato373.uitl;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "appSettings.trade")
@Data
public class TradeSettings {
	private BigDecimal uppoerLimit;
	private BigDecimal lowerLimit;
	private BigDecimal orderAmount;
	private BigDecimal trapRange;
	private BigDecimal trapTiming;
}
