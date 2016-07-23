package de.sekmi.histream.i2b2.services;


import java.util.logging.Logger;

import javax.inject.Singleton;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

@Singleton
public class Settings {
	private static final Logger log = Logger.getLogger(Settings.class.getName());

	private Configuration fmConfig;
	
	public Settings(){
		log.info("Creating freemarker config");
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.25) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		cfg.setTemplateLoader(new ClassTemplateLoader(Settings.class, "/"));

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		cfg.setLogTemplateExceptions(false);
		this.fmConfig = cfg;		
	}


	public Configuration getFreemarkerConfiguration(){
		return fmConfig;
	}
}
