package jp.yamato373.uitl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "appSettings")
@Data
public class AppSettings {
	private String cp;
	private String symbol;
}
