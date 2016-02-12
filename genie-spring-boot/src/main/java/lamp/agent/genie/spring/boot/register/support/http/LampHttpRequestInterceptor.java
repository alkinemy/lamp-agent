package lamp.agent.genie.spring.boot.register.support.http;

import lamp.agent.genie.spring.boot.config.LampAgentProperties;
import lamp.agent.genie.spring.boot.register.support.jwt.JwtBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

public class LampHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private final String userAgent;
	private LampAgentProperties clientProperties;

	private JwtBuilder jwtBuilder = new JwtBuilder();

	public LampHttpRequestInterceptor(LampAgentProperties clientProperties) {
		this.clientProperties = clientProperties;
		this.userAgent = clientProperties.getType() + "/" + clientProperties.getVersion();
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
		throws IOException {
		HttpHeaders headers = request.getHeaders();
		headers.set("User-Agent", userAgent);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		URI uri = request.getURI();
		StringBuilder canonicalUrlSB = new StringBuilder();
		canonicalUrlSB.append(request.getMethod().name());
		canonicalUrlSB.append("&");
		canonicalUrlSB.append(uri.getPath());
		canonicalUrlSB.append("&");
		canonicalUrlSB.append(uri.getQuery());

		String issuer = clientProperties.getId();
		String secret = clientProperties.getSecretKey();
		String token = jwtBuilder.generateJWTToken(canonicalUrlSB.toString(), issuer, secret);
		headers.add("Authorization", "Bearer " + token);

		return execution.execute(request, body);
	}

}
