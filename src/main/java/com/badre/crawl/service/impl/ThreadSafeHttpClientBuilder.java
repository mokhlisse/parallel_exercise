package com.badre.crawl.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Service;

import com.badre.crawl.service.HttpClientBuilder;

/**
 * Base implementation of {@link HttpClientBuilder}.
 * 
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre Edine Mokhlisse</a>
 */
@SuppressWarnings("deprecation")
@Service
public class ThreadSafeHttpClientBuilder implements HttpClientBuilder {

	private static final Integer HTTP_PARAM_MAX_REDIRECTS = 7;
	private static final int TIMEOUT = 20 * 1000; // 20 seconds
	private static final int NB_MAX_CONNECTIONS = 200;
	private static final int NB_MAX_CONNECTIONS_PER_HOST = 10;
	private static final int RETRY_COUNT = 0;
	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient client;
	private org.apache.http.impl.client.HttpClientBuilder builder;

	private org.apache.http.impl.client.HttpClientBuilder createBuilder() {

		HttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		RequestConfig config = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectionRequestTimeout(TIMEOUT)
				.setConnectTimeout(TIMEOUT).setMaxRedirects(HTTP_PARAM_MAX_REDIRECTS).build();

		// SSL context for secure connections can be created either based on
		// system or application specific properties
		SSLContext sslcontext = getSslContext();

		// Use custom hostname verifier to customize SSL hostname
		// verification
		HostnameVerifier hostnameVerifier = getHostnameVerifier();

		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext,
				hostnameVerifier);

		Registry<ConnectionSocketFactory> schemeRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory)
				.build();

		// Configure the connection manager to use connection configuration
		// multi-threads connection manager
		connectionManager = new PoolingHttpClientConnectionManager(schemeRegistry);
		connectionManager.setMaxTotal(NB_MAX_CONNECTIONS);
		connectionManager.setDefaultMaxPerRoute(NB_MAX_CONNECTIONS_PER_HOST);
		connectionManager.setValidateAfterInactivity(5000);

		builder = HttpClients.custom().setConnectionManager(poolingConnManager);

		builder.setConnectionManager(connectionManager).setDefaultRequestConfig(config).setConnectionManagerShared(true)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(RETRY_COUNT, false));

		return builder;

	}

	private SSLContext getSslContext() {

		return SSLContexts.createSystemDefault();
	}

	private HostnameVerifier getHostnameVerifier() {

		return new DefaultHostnameVerifier();
	}

	/**
	 * Initializes the HTTP client.
	 */
	@PostConstruct
	public synchronized void init() {
		if (client == null) {
			client = createBuilder().build();
		}
	}

	/**
	 * Closes the HTTP client resources.
	 */
	@PreDestroy
	public synchronized void close() {
		if (client != null) {
			try {
				client.close();
				client = null;
			} catch (IOException e) {
				; // ignore
			}
		}
		if (connectionManager != null) {
			connectionManager.close();
		}
	}

	@Override
	public CloseableHttpClient getHttpClient() {
		if (client == null) {
			throw new IllegalStateException(
					"HTTP client is not defined yet, please call init() before using the client");
		}
		return client;
	}
}
