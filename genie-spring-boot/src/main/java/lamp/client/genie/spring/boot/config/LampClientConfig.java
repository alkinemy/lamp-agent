package lamp.client.genie.spring.boot.config;

import lamp.client.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.client.genie.spring.boot.register.LampClientRegistrationApplicationListener;
import lamp.client.genie.spring.boot.register.service.LampClientSecretKeyService;
import lamp.client.genie.spring.boot.register.support.LampClientApiRegistrator;
import lamp.client.genie.spring.boot.base.impl.LampContextImpl;
import lamp.client.genie.spring.boot.register.LampClientRegistrator;

import lamp.client.genie.spring.boot.register.support.http.BasicAuthHttpRequestInterceptor;
import lamp.client.genie.spring.boot.register.support.http.LampHttpRequestInterceptor;

import lamp.client.genie.utils.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ LampClientProperties.class })
public class LampClientConfig {

	@Bean
	public LampContextImpl lampContext(LampClientProperties clientProperties) {
		return new LampContextImpl(clientProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public LampClientSecretKeyService lampClientSecretKeyService(LampClientProperties clientProperties) {
		return new LampClientSecretKeyService(clientProperties);
	}


	@Bean
	public SmartAssembler smartAssembler() {
		return new SmartAssembler();
	}

	@ConditionalOnProperty(name = "lamp.server.type", havingValue = "rest")
	@EnableConfigurationProperties({ LampServerProperties.class })
	public static class LampServerConfig {

		@Bean
		@ConditionalOnMissingBean
		public LampClientRegistrator lampClientRegistrator(LampServerProperties serverProperties, LampClientProperties clientProperties) {
			return new LampClientApiRegistrator(serverProperties, clientProperties, createRestTemplate(serverProperties, clientProperties));
		}

		protected RestTemplate createRestTemplate(LampServerProperties serverProperties, LampClientProperties clientProperties) {
			RestTemplate template = new RestTemplate();
			template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
			String userClient = clientProperties.getType() + "/" + clientProperties.getVersion();
			interceptors.add(new LampHttpRequestInterceptor(userClient));

			String username = StringUtils.defaultString(serverProperties.getUsername(), clientProperties.getId());
			String password = serverProperties.getPassword();
			if (StringUtils.isNotBlank(password)) {
				interceptors.add(new BasicAuthHttpRequestInterceptor(username, password));
			}

			template.setInterceptors(interceptors);
			return template;
		}

		@Bean
		public LampClientRegistrationApplicationListener registrationApplicationListener(LampClientApiRegistrator lampClientApiRegistrator) {
			return new LampClientRegistrationApplicationListener(lampClientApiRegistrator);
		}

	}

}
