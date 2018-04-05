package com.badre.crawl.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import com.badre.crawl.service.UrlService;

/**
 * Base implementation of {@link UrlService}.
 * 
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre Edine Mokhlisse</a>
 */
@Service
public class UrlServiceImpl implements UrlService {

	private static final Logger logger = LogManager.getLogger(UrlServiceImpl.class);
	private static final String REPORT_FILE_NAME = "report.txt";
	private static final int MAX_LEVEL = 2;
	private static final int POOL_SIZE = 10;
	private static ExecutorService EXECUTOR_SERVICE_POOL;
	private static List<Future<Integer>> futures;
	private static final String INITIAL_URL = "https://en.wikipedia.org/wiki/Java_Transaction_API";
	private ConcurrentHashMap<String, AtomicInteger> urls = new ConcurrentHashMap<>();
	static ReentrantLock counterLock = new ReentrantLock(true);

	@Autowired
	private HttpClientBuilder httpClientBuilder;
	private CloseableHttpClient client;

	@PostConstruct
	public void init() {

		client = httpClientBuilder.getHttpClient();
		EXECUTOR_SERVICE_POOL = Executors.newFixedThreadPool(POOL_SIZE);
		futures = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@PreDestroy
	@Override
	public void destroy() {

		EXECUTOR_SERVICE_POOL.shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(Url url) {

		if (logger.isDebugEnabled()) {
			logger.debug("process url=" + url + " ...");
		}
		addUrl(url.getUrl());
		if (url.getLevel() < MAX_LEVEL) {
			CrawlTask callable = new CrawlTask(this, client, url);
			Future<Integer> future = EXECUTOR_SERVICE_POOL.submit(callable);
			futures.add(future);
			if (logger.isDebugEnabled()) {
				logger.debug("submitted url=" + url);
			}
		}
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
	public void urlsToFile() {

		waitTermination();
		try (FileWriter writer = new FileWriter(REPORT_FILE_NAME); PrintWriter printWriter = new PrintWriter(writer);) {
			for (String url : sortUrlsMap().keySet()) {
				printWriter.println(urls.get(url).incrementAndGet() + " " + url);
			}
		} catch (IOException e) {
			logger.error("error writing urls to report file ", e);
		}
	}

	/**
	 * wait threads to finish
	 */
	private void waitTermination() {

		while (!futures.isEmpty()) {
			Iterator<Future<Integer>> iter = futures.iterator();
			while (iter.hasNext()) {
				try {
					Future<Integer> future = iter.next();
					future.get();
					iter.remove();
				} catch (InterruptedException e) {
					logger.error("error waiting thread to terminate", e);
				} catch (ExecutionException e) {
					logger.error("error waiting thread to terminate", e);
				}
			}
		}
	}

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
