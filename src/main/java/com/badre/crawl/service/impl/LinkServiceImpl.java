package com.badre.crawl.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badre.crawl.model.Url;
import com.badre.crawl.service.HttpClientBuilder;
import com.badre.crawl.service.LinkService;

/**
 * Base implementation of {@link LinkService}.
 * 
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre Edine Mokhlisse</a>
 */
@Service
public class LinkServiceImpl implements LinkService {

	private static final Logger logger = LogManager.getLogger(LinkServiceImpl.class);
	private static final String REPORT_FILE_NAME = "report.txt";
	private static final int MAX_LEVEL = 1;
	private static final int POOL_SIZE = 10;
	private static ForkJoinPool MAIN_POOL;
	private static final String INITIAL_URL = "https://en.wikipedia.org/wiki/Java_Transaction_API";
	private ConcurrentHashMap<String, AtomicInteger> urls = new ConcurrentHashMap<>();
	static ReentrantLock counterLock = new ReentrantLock(true);

	@Autowired
	private HttpClientBuilder httpClientBuilder;
	private CloseableHttpClient client;

	@PostConstruct
	public void init() {

		client = httpClientBuilder.getHttpClient();
		MAIN_POOL = new ForkJoinPool(POOL_SIZE);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreDestroy
	@Override
	public void destroy() {

		MAIN_POOL.shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(Url url) {

		if (logger.isDebugEnabled()) {
			logger.debug("process url=" + url + " ...");
		}
		if (url.getLevel() < MAX_LEVEL) {
			LinkTask task = new LinkTask(this, client, url);
			MAIN_POOL.invoke(task);
			if (logger.isDebugEnabled()) {
				logger.debug("submitted url=" + url);
			}
		}
		addUrl(url.getUrl());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {

		process(new Url(INITIAL_URL, 0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUrl(String url) {

		counterLock.lock();
		try {
			if (urls.containsKey(url)) {
				urls.get(url).getAndIncrement();
			} else {
				urls.put(url, new AtomicInteger(0));
			}
		} finally {
			counterLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printToFile() {

		try (FileWriter writer = new FileWriter(REPORT_FILE_NAME); PrintWriter printWriter = new PrintWriter(writer);) {
			for (String url : sortUrlsMap().keySet()) {
				printWriter.println(urls.get(url).incrementAndGet() + " " + url);
			}
		} catch (IOException e) {
			logger.error("error writing urls to report file ", e);
		}
	}

	/**
	 * sort urls by occurrence counter
	 * 
	 * @return
	 */
	private Map<String, AtomicInteger> sortUrlsMap() {

		List<Entry<String, AtomicInteger>> list = new LinkedList<Entry<String, AtomicInteger>>(urls.entrySet());
		Collections.sort(list, new Comparator<Entry<String, AtomicInteger>>() {
			public int compare(Entry<String, AtomicInteger> o1, Entry<String, AtomicInteger> o2) {
				return o2.getValue().intValue() - o1.getValue().intValue();
			}
		});

		Map<String, AtomicInteger> sortedMap = new LinkedHashMap<String, AtomicInteger>();
		for (Entry<String, AtomicInteger> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
