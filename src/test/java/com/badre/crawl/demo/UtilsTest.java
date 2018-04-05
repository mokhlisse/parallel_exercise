package com.badre.crawl.demo;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

import com.badre.crawl.utils.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {

	@Test
	@Ignore
	public void extractLinks() throws IOException {

		String fileName = "europe.html";
		String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName));
		Set<String> urls = Utils.extractLinks(content);

		assertTrue(urls.contains("/wiki/Eastern_states_of_Australia"));
		assertTrue(urls.contains(
				"http://query.nytimes.com/mem/archive-free/pdf?res=9406E4D8143EE433A25754C2A9619C946996D6CF"));
		assertTrue(urls.contains("#cite_ref-10"));
		assertTrue(urls.contains("/wiki/Protestant_Reformation"));
	}

	@Test
	public void toAbslute() {

		String parent = "https://en.wikipedia.org/wiki/Java_Transaction_API";
		String link = "/wiki/Eastern_states_of_Australia";

		System.out.println(Utils.toAbsolute(link, parent));
	}
}
