package lamp.agent.genie.spring.boot.config;

import lamp.agent.genie.spring.boot.base.assembler.SmartAssembler;
import lamp.agent.genie.spring.boot.base.impl.LampContextImpl;
import lamp.agent.genie.spring.boot.management.service.AppMonitorService;
import lamp.agent.genie.spring.boot.register.AgentEventPublisher;
import lamp.agent.genie.spring.boot.register.AgentRegistrationApplicationListener;
import lamp.agent.genie.spring.boot.register.AgentRegistrator;
import lamp.agent.genie.spring.boot.register.service.AgentSecretKeyGenerator;
import lamp.agent.genie.spring.boot.register.support.ApiAgentEventPublisher;
import lamp.agent.genie.spring.boot.register.support.ApiAgentRegistrator;
import lamp.agent.genie.spring.boot.register.support.DiskspacePublicMetrics;
import lamp.agent.genie.spring.boot.register.support.SigarPublicMetrics;
import lamp.agent.genie.spring.boot.register.support.http.BasicAuthHttpRequestInterceptor;
import lamp.agent.genie.spring.boot.register.support.http.LampHttpRequestInterceptor;
import lamp.agent.genie.utils.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ LampAgentProperties.class, MetricsDiskSpaceProperties.class })
public class LampAgentConfig {

	@Bean
	public LampContextImpl lampContext(ApplicationContext applicationContext, LampAgentProperties agentProperties) {
		return new LampContextImpl(applicationContext, agentProperties);
	}

	@Bean
	@ConditionalOnProperty(name = "lamp.agent.metrics-sigar-enabled", havingValue = "true")
	public SigarPublicMetrics sigarPublicMetrics() {
		return new SigarPublicMetrics();
	}

	@Bean
	@ConditionalOnProperty(name = "metrics.diskspace.enabled", havingValue = "true")
	public DiskspacePublicMetrics diskspaceMetrics() {
		return new DiskspacePublicMetrics();
	}

	@Bean
	@ConditionalOnMissingBean
	public AgentSecretKeyGenerator agentSecretKeyGenerator(LampAgentProperties agentProperties) {
		return new AgentSecretKeyGenerator(agentProperties);
	}

	@Bean
	public SmartAssembler smartAssembler() {
		return new SmartAssembler();
	}

	@Bean
	@ConditionalOnProperty(name = "lamp.agent.monitor", havingValue = "true")
	public ScheduledTaskRegistrar taskRegistrar(final LampAgentProperties agentProperties, final AppMonitorService appMonitorService) {
		ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
		Runnable registrationTask = new Runnable() {
			@Override
			public void run() {
				appMonitorService.monitor();
			}
		};

		registrar.addFixedRateTask(registrationTask, agentProperties.getMonitorPeriod());
		return registrar;
	}

	@ConditionalOnProperty(name = "lamp.server.type", havingValue = "rest")
	@EnableConfigurationProperties({ LampServerProperties.class })
	public static class LampServerConfig {

		@Bean
		@ConditionalOnMissingBean
		public AgentRegistrator agentRegistrator(LampServerProperties serverProperties, LampAgentProperties clientProperties) {
			return new ApiAgentRegistrator(serverProperties, clientProperties, createRestTemplate(serverProperties, clientProperties));
		}

		@Bean
		@ConditionalOnMissingBean
		public AgentEventPublisher agentEventPublisher(LampServerProperties serverProperties, LampAgentProperties clientProperties) {
			return new ApiAgentEventPublisher(serverProperties, clientProperties, createRestTemplate(serverProperties, clientProperties));
		}

		protected RestTemplate createRestTemplate(LampServerProperties serverProperties, LampAgentProperties clientProperties) {

			HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
			clientHttpRequestFactory.setConnectTimeout(serverProperties.getConnectTimeout());
			clientHttpRequestFactory.setConnectionRequestTimeout(serverProperties.getConnectionRequestTimeout());
			clientHttpRequestFactory.setReadTimeout(serverProperties.getReadTimeout());

			RestTemplate template = new RestTemplate(clientHttpRequestFactory);
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
		public AgentRegistrationApplicationListener registrationApplicationListener(ApiAgentRegistrator lampClientApiRegistrator,
			AgentEventPublisher agentEventPublisher) {
			return new AgentRegistrationApplicationListener(lampClientApiRegistrator, agentEventPublisher);
		}

	}

}
