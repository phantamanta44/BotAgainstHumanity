package io.github.phantamanta44.botah.util.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.phantamanta44.botah.util.MathUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class HttpUtils {
	
	private static final JsonParser JSON_PARSE = new JsonParser();
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
	private static final CloseableHttpClient HTTP_CLI = HttpClients.custom()
			.setUserAgent(USER_AGENT)
			.build();
	
	public static String requestXml(String uri, String... headers) throws HttpException, IOException {
		return requestXml(URI.create(uri.replaceAll("\\s", "+")), headers);
	}
	
	public static String requestXml(URI uri, String... headers) throws HttpException, IOException {
		HttpUriRequest req = new HttpGet(uri);
		if (headers.length % 2 != 0)
			throw new IllegalArgumentException("Headers must come in name-value pairs!");
		for (int i = 0; i < headers.length; i+= 2)
			req.addHeader(headers[i], headers[i + 1]);
		try (CloseableHttpResponse resp = HTTP_CLI.execute(req)) {
			int status = resp.getStatusLine().getStatusCode();
			if (MathUtils.bounds(status, 400, 600))
				throw new HttpException(status);
			return EntityUtils.toString(resp.getEntity());
		}
	}
	
	public static JsonElement requestJson(String uri, String... headers) throws HttpException, IOException {
		return requestJson(URI.create(uri.replace("\\s", "+")), headers);
	}
	
	public static JsonElement requestJson(URI uri, String... headers) throws HttpException, IOException {
		return JSON_PARSE.parse(requestXml(uri, headers));
	}
	
}
