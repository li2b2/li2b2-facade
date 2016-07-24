package de.sekmi.li2b2.services;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.TemplateScalarModel;

public class TestTemplates {

	@Test
	public void test() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException{
		Settings s = new Settings();
		Template t = s.getFreemarkerConfiguration().getTemplate("templates/response_header.xml");
		t.process(new TemplateHashModel() {
			@Override
			public boolean isEmpty() throws TemplateModelException {
				return false;
			}
			
			@Override
			public TemplateModel get(String key) throws TemplateModelException {
				return new TemplateScalarModel() {
					@Override
					public String getAsString() throws TemplateModelException {
						return "dummy";
					}
				};
			}
		}, new PrintWriter(System.out));
	}
}
