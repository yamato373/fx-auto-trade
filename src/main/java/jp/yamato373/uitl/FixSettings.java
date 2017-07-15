package jp.yamato373.uitl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "appSettings.fix")
@Data
public class FixSettings {
	private String password;
	private String account;
	private String indicativeText;
	private int subscribeCheckInterval;
	private int delayThreshold;
}