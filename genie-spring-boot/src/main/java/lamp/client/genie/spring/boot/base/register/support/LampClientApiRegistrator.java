package lamp.client.genie.spring.boot.base.register.support;


import lamp.client.genie.spring.boot.base.register.model.LampClientApiRegisterForm;
import lamp.client.genie.spring.boot.base.register.model.LampClientApiRegisterResult;
import lamp.client.genie.spring.boot.base.register.LampClientRegistrator;
import lamp.client.genie.spring.boot.config.LampClientProperties;
import lamp.client.genie.spring.boot.config.LampServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class LampClientApiRegistrator implements LampClientRegistrator {

	private final LampServerProperties lampServerProperties;
	private final LampClientProperties lampClientProperties;

	private final RestTemplate restTemplate;

	public LampClientApiRegistrator(
			LampServerProperties lampServerProperties, LampClientProperties lampClientProperties,
			RestTemplate restTemplate) {
		this.lampServerProperties = lampServerProperties;
		this.lampClientProperties = lampClientProperties;

		this.restTemplate = restTemplate;
	}

	@Override
	public void register() {
		LampClientApiRegisterForm form = createLampClientRegistrationForm();
		String url = lampServerProperties.getUrl() + "/api/lamps";
		log.info("registerUrl : {}", url);
		log.info("registerForm : {}", form);

		LampClientApiRegisterResult result = restTemplate.postForObject(url, form, LampClientApiRegisterResult.class);
		lampClientProperties.setRegistrationId(result.getRegisterId());
		lampClientProperties.setAddress(result.getAddress());
	}

	@Override
	public void deregister() {
		Long registrationId = lampClientProperties.getRegistrationId();
		if (registrationId == null) {
			log.warn("deregister Error : registrationId is null");
			return;
		}
		String url = lampServerProperties.getUrl() + "/api/lamps/" + registrationId;
		log.info("deregisterUrl : {}", url);
		restTemplate.delete(url);
	}

	protected LampClientApiRegisterForm createLampClientRegistrationForm() {
		LampClientApiRegisterForm lampClientApiRegisterForm = new LampClientApiRegisterForm();
		BeanUtils.copyProperties(lampClientProperties, lampClientApiRegisterForm, LampClientApiRegisterForm.class);
		lampClientApiRegisterForm.setId(lampClientProperties.getId());
		lampClientApiRegisterForm.setName(lampClientProperties.getName());
		lampClientApiRegisterForm.setType(lampClientProperties.getType());
		lampClientApiRegisterForm.setVersion(lampClientProperties.getVersion());
		return lampClientApiRegisterForm;
	}

}
