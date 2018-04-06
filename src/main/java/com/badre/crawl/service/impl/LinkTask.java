package com.badre.crawl.service.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badre.crawl.model.Url;
import com.badre.crawl.service.LinkService;
import com.badre.crawl.utils.Utils;

/**
 * Crawl task to crawl a link and submit sub links to threading pool
 * 
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre Edine Mokhlisse</a>
 */
public class LinkTask extends RecursiveAction {

	private static final long serialVersionUID = 4396653414862146805L;
	private static final Logger logger = LogManager.getLogger(LinkTask.class);
	private LinkService urlService;
	private CloseableHttpClient client;
	private Url url;

	public LinkTask(LinkService urlService, CloseableHttpClient client, Url url) {
		super();
		this.urlService = urlService;
		this.client = client;
		this.url = url;
	}

	@Override
	protected void compute() {

		CloseableHttpResponse response = null;
		String content = null;

		try {

			// download url content
			if (logger.isDebugEnabled()) {
				logger.debug("execute http request url=" + url + " ...");
			}
			response = client.execute(new HttpGet(url.getUrl()));
			HttpEntity entity = response.getEntity();

			if (logger.isDebugEnabled()) {
				logger.debug("execute http request url=" + url + " --> status code=" + response.getStatusLine());
			}
			if (entity != null) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"execute http request url=" + url + " --> response Length=" + entity.getContentLength());
				}
				content = EntityUtils.toString(entity);
				if (content != null && logger.isDebugEnabled()) {
					logger.debug("Response content: " + StringUtils.abbreviate(content, 50));
				}

				// parse html, extract urls
				Set<String> urls = Utils.extractLinks(content);
				for (String item : urls) {
					urlService.process(new Url(Utils.toAbsolute(item, url.getUrl()), 1 + url.getLevel()));
				}
			}
		} catch (ClientProtocolException e) {
			logger.error("error downloading url = " + url, e);
		} catch (IOException e) {
			logger.error("error downloading url = " + url, e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("error downloading url = " + url, e);
				}
			}
		}
	}
}
