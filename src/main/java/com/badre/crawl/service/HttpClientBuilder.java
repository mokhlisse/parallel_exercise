package com.badre.crawl.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Builder of {@link HttpClient}.
 *
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre Edine Mokhlisse</a>
 */
public interface HttpClientBuilder {

	/**
	 * Creates a thread safe {@link CloseableHttpClient}.
	 * 
	 * @return the non null HttpClient
	 */
	CloseableHttpClient getHttpClient();

}