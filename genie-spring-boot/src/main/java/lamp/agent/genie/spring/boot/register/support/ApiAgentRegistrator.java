package lamp.agent.genie.spring.boot.register.support;

import lamp.agent.genie.spring.boot.config.LampAgentProperties;
import lamp.agent.genie.spring.boot.config.LampServerProperties;
import lamp.agent.genie.spring.boot.register.AgentRegistrator;
import lamp.agent.genie.spring.boot.register.model.AgentRegisterForm;
import lamp.agent.genie.spring.boot.register.model.AgentRegisterResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
public class ApiAgentRegistrator implements AgentRegistrator {

	private final LampServerProperties lampServerProperties;
	private final LampAgentProperties lampClientProperties;

	private final RestTemplate restTemplate;

	public ApiAgentRegistrator(
		LampServerProperties lampServerProperties, LampAgentProperties lampClientProperties,
		RestTemplate restTemplate) {
		this.lampServerProperties = lampServerProperties;
		this.lampClientProperties = lampClientProperties;

		this.restTemplate = restTemplate;
	}

	@Override
	public void register() {
		AgentRegisterForm form = createLampClientRegistrationForm();
		String url = lampServerProperties.getUrl() + "/api/agent";
		log.debug("registerUrl : {}", url);
		log.debug("registerForm : {}", form);

		AgentRegisterResult result = restTemplate.postForObject(url, form, AgentRegisterResult.class);
		lampClientProperties.setAddress(result.getAddress());
	}

	@Override
	public void deregister() {
		String url = lampServerProperties.getUrl() + "/api/agent/" + lampClientProperties.getId();
		log.debug("deregisterUrl : {}", url);
		restTemplate.delete(url);
	}

	protected AgentRegisterForm createLampClientRegistrationForm() {
		AgentRegisterForm agentRegisterForm = new AgentRegisterForm();
		BeanUtils.copyProperties(lampClientProperties, agentRegisterForm, AgentRegisterForm.class);
		agentRegisterForm.setId(lampClientProperties.getId());
		agentRegisterForm.setName(lampClientProperties.getName());
		agentRegisterForm.setGroupId(lampClientProperties.getGroupId());
		agentRegisterForm.setArtifactId(lampClientProperties.getArtifactId());
		agentRegisterForm.setVersion(lampClientProperties.getVersion());
		agentRegisterForm.setSecretKey(lampClientProperties.getSecretKey());
		agentRegisterForm.setAppDirectory(lampClientProperties.getMountPointPath());
		agentRegisterForm.setTime(new Date());
		return agentRegisterForm;
	}

}
