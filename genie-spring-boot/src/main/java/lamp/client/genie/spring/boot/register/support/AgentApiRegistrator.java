package lamp.client.genie.spring.boot.register.support;


import lamp.client.genie.spring.boot.register.model.AgentRegisterForm;
import lamp.client.genie.spring.boot.register.model.AgentRegisterResult;
import lamp.client.genie.spring.boot.register.AgentRegistrator;
import lamp.client.genie.spring.boot.config.LampClientProperties;
import lamp.client.genie.spring.boot.config.LampServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
public class AgentApiRegistrator implements AgentRegistrator {

	private final LampServerProperties lampServerProperties;
	private final LampClientProperties lampClientProperties;

	private final RestTemplate restTemplate;

	public AgentApiRegistrator(
			LampServerProperties lampServerProperties, LampClientProperties lampClientProperties,
			RestTemplate restTemplate) {
		this.lampServerProperties = lampServerProperties;
		this.lampClientProperties = lampClientProperties;

		this.restTemplate = restTemplate;
	}

	@Override
	public void register() {
		AgentRegisterForm form = createLampClientRegistrationForm();
		String url = lampServerProperties.getUrl() + "/api/agent";
		log.info("registerUrl : {}", url);
		log.info("registerForm : {}", form);

		AgentRegisterResult result = restTemplate.postForObject(url, form, AgentRegisterResult.class);
		lampClientProperties.setAddress(result.getAddress());
	}

	@Override
	public void deregister() {
//		Long registrationId = lampClientProperties.getRegistrationId();
//		if (registrationId == null) {
//			log.warn("deregister Error : registrationId is null");
//			return;
//		}
//		String url = lampServerProperties.getUrl() + "/api/agent/" + registrationId;
//		log.info("deregisterUrl : {}", url);
//		restTemplate.delete(url);
	}

	protected AgentRegisterForm createLampClientRegistrationForm() {
		AgentRegisterForm agentRegisterForm = new AgentRegisterForm();
		BeanUtils.copyProperties(lampClientProperties, agentRegisterForm, AgentRegisterForm.class);
		agentRegisterForm.setId(lampClientProperties.getId());
		agentRegisterForm.setName(lampClientProperties.getName());
		agentRegisterForm.setType(lampClientProperties.getType());
		agentRegisterForm.setVersion(lampClientProperties.getVersion());
		agentRegisterForm.setSecretKey(lampClientProperties.getSecretKey());
		agentRegisterForm.setHomeDirectory(lampClientProperties.getMountPointPath());
		agentRegisterForm.setTime(new Date());
		return agentRegisterForm;
	}

}
