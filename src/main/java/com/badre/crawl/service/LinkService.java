package com.badre.crawl.service;

import com.badre.crawl.model.Url;

/**
 * Service Interface to process url.
 * 
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre Edine Mokhlisse</a>
 */
public interface LinkService {

	/**
	 * start processing first url
	 */
	public void start();

	/**
	 * release resources
	 */
	public void destroy();

	/**
	 * process a url
	 * 
	 * @param url
	 */
	public void process(Url url);

	/**
	 * add url to resulting urls list
	 * 
	 * @param url
	 */
	public void addUrl(String url);

	/**
	 * write Urls to report file
	 */
	public void printToFile();
}
