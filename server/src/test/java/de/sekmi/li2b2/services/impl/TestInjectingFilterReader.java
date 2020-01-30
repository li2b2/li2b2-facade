package de.sekmi.li2b2.services.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class TestInjectingFilterReader {

	@Test
	public void testInjectAfter() {
		InputStream in = getClass().getResourceAsStream("/inject-test.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
		InjectingFilterReader r = new InjectingFilterReader(20, br, Pattern.compile("^xxx$"), "yyy\n", false);
		BufferedReader ibr = new BufferedReader(r);
		String[] lines = ibr.lines().toArray(i -> new String[i]);
		Assert.assertEquals(12, lines.length);
		Assert.assertEquals("xxx", lines[5]);
		Assert.assertEquals("yyy", lines[6]);
	}
	@Test
	public void testInjectBefore() {
		InputStream in = getClass().getResourceAsStream("/inject-test.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
		InjectingFilterReader r = new InjectingFilterReader(20, br, Pattern.compile("^xxx$"), "yyy\n", true);
		BufferedReader ibr = new BufferedReader(r);
		String[] lines = ibr.lines().toArray(i -> new String[i]);
		Assert.assertEquals(12, lines.length);
		Assert.assertEquals("yyy", lines[5]);
		Assert.assertEquals("xxx", lines[6]);
		
	}
}
