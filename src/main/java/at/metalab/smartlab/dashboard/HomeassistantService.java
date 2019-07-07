package at.metalab.smartlab.dashboard;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HomeassistantService {

	@Value("${dashboard.haEndpoint:http://10.20.30.97:8123/api}")
	private String haEndpoint;

	@Value("${dashboard.haApiToken:}")
	private String haApiToken;

	public void service(String domain, String serviceName, String body) {
		new Thread() {
			public void run() {
				try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
					String strUrl = String.format("%s/services/%s/%s", haEndpoint, domain, serviceName);

					HttpPost httpPost = new HttpPost(strUrl);
					httpPost.addHeader("Content-Type", "application/json");
					httpPost.addHeader("Authorization", String.format("Bearer %s", haApiToken));
					httpPost.setEntity(new StringEntity(body));

					httpclient.execute(httpPost);

					System.out.println(strUrl);
				} catch (Exception ignore) {
					ignore.printStackTrace();
				}
			};
		}.start();
	}

	public void haLightTurn(String entityId, boolean on) {
		service("light", on ? "turn_on" : "turn_off", "{ \"entity_id\" : \"" + entityId + "\" }");
	}

	public void haSwitchTurn(String entityId, boolean on) {
		service("switch", on ? "turn_on" : "turn_off", "{ \"entity_id\" : \"" + entityId + "\" }");
	}

}
