package lamp.agent.genie.spring.boot.register.support.jwt;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class JwtBuilder {

	private ObjectMapper objectMapper = new ObjectMapper();

	public String generateJWTToken(String canonicalUrl,
		String issuer, String sharedSecret) {

		try {
			JwtHeader header = JwtHeader.of(TokenType.JWT, TokenAlgorithm.HS256);

			JwtPayload payload = new JwtPayload();
			payload.setIssuer(issuer);
			payload.setIssuedAtTime(System.currentTimeMillis() / 1000L);
			payload.setExpirationTime(payload.getIssuedAtTime() + 180L);
			payload.setQueryStringHash(getQueryStringHash(canonicalUrl));


			return generateJWTToken(header, payload, sharedSecret);
		} catch (JwtException e) {
			throw e;
		} catch (Exception e) {
			throw new JwtException("Token generation failed", e);
		}
	}

	public String generateJWTToken(JwtHeader header, JwtPayload payload, String sharedSecret) {
		try {
			String headerDotPayload = getBase64EncodedHeaderAndPayload(header, payload);
			String signature = getBase64EncodedSignature(header, headerDotPayload, sharedSecret);
			return headerDotPayload + "." + signature;
		} catch (JsonProcessingException e) {
			throw new JwtException("Token generation failed", e);
		}
	}

	protected String getBase64EncodedHeaderAndPayload(JwtHeader header, JwtPayload payload) throws JsonProcessingException {
		String headerJsonString = objectMapper.writeValueAsString(header);
		String claimsJsonString = objectMapper.writeValueAsString(payload);

		StringBuilder signingInputSB = new StringBuilder();
		signingInputSB.append(Base64.encodeBase64URLSafeString(headerJsonString.getBytes()));
		signingInputSB.append(".");
		signingInputSB.append(Base64.encodeBase64URLSafeString(claimsJsonString.getBytes()));
		return signingInputSB.toString();
	}

	protected String getBase64EncodedSignature(JwtHeader header, String signingInput, String sharedSecret) {
		if (TokenAlgorithm.HS256.equals(header.getAlgorithm())) {
			try {
				return Base64.encodeBase64URLSafeString(signHmac256(signingInput, sharedSecret));
			} catch (Exception e) {
				throw new JwtException("Signature generation failed", e);
			}
		}
		throw new JwtException("Unsupported alogirthm : (" + header.getType() + ", " + header.getAlgorithm() + ")");
	}


	protected byte[] signHmac256(String signingInput, String sharedSecret)
		throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKey key = new SecretKeySpec(sharedSecret.getBytes(), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(key);
		return mac.doFinal(signingInput.getBytes());
	}


	private static String getQueryStringHash(String canonicalUrl) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(canonicalUrl.getBytes("UTF-8"));
		byte[] digest = md.digest();
		return new String(Hex.encode(digest));
	}

}
