package jp.yamato373.fix.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "appSettings.fix")
@Data
public class FixSettings {
	private String password;
	private String indicativeText;
}