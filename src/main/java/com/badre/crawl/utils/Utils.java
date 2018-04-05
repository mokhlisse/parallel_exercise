package com.badre.crawl.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static final String HTML_HREF_TAG_REGX = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final Pattern LINK_PATTERN = Pattern.compile(HTML_HREF_TAG_REGX);
	private static final String HREF_INITIAL_REGX = "^https?://[^/]+";
	private static final Pattern HREF_INITIAL_PATTERN = Pattern.compile(HREF_INITIAL_REGX);

	public static Set<String> extractLinks(String html) {

		Set<String> urls = new HashSet<>();
		Matcher mLink = LINK_PATTERN.matcher(html);
		while (mLink.find()) {
			urls.add(mLink.group(1).replaceAll("^.|.$", ""));
		}

		return urls;
	}

	public static String toAbsolute(String link, String parentDomain) {

		if (!link.startsWith("http")) {// link is not absolute
			Matcher m = HREF_INITIAL_PATTERN.matcher(parentDomain);
			if (m.find()) {
				return m.group(0) + link;
			}
		}
		return link;
	}
}