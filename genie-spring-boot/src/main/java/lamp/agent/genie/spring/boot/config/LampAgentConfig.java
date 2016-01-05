package lamp.agent.genie.spring.boot.config;

import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.register.AgentRegistrationApplicationListener;
import lamp.agent.genie.spring.boot.register.support.http.LampHttpRequestInterceptor;
import lamp.agent.genie.spring.boot.register.service.AgentSecretKeyGenerator;
import lamp.agent.genie.spring.boot.register.support.AgentApiRegistrator;
import lamp.agent.genie.spring.boot.base.impl.LampContextImpl;
import lamp.agent.genie.spring.boot.register.AgentRegistrator;

import lamp.agent.genie.spring.boot.register.support.http.BasicAuthHttpRequestInterceptor;

import lamp.agent.genie.utils.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ LampAgentProperties.class })
public class LampAgentConfig {

	@Bean
	public LampContextImpl lampContext(LampAgentProperties clientProperties) {
		return new LampContextImpl(clientProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public AgentSecretKeyGenerator agentSecretKeyGenerator(LampAgentProperties clientProperties) {
		return new AgentSecretKeyGenerator(clientProperties);
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
		public AgentRegistrator lampClientRegistrator(LampServerProperties serverProperties, LampAgentProperties clientProperties) {
			return new AgentApiRegistrator(serverProperties, clientProperties, createRestTemplate(serverProperties, clientProperties));
		}

		protected RestTemplate createRestTemplate(LampServerProperties serverProperties, LampAgentProperties clientProperties) {
			RestTemplate template = new RestTemplate();
			template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
			interceptors.add(new LampHttpRequestInterceptor(clientProperties));

			String username = StringUtils.defaultString(serverProperties.getUsername(), clientProperties.getId());
			String password = serverProperties.getPassword();
			if (StringUtils.isNotBlank(password)) {
				interceptors.add(new BasicAuthHttpRequestInterceptor(username, password));
			}

			template.setInterceptors(interceptors);
			return template;
		}

		@Bean
		public AgentRegistrationApplicationListener registrationApplicationListener(AgentApiRegistrator lampClientApiRegistrator) {
			return new AgentRegistrationApplicationListener(lampClientApiRegistrator);
		}

	}

}
