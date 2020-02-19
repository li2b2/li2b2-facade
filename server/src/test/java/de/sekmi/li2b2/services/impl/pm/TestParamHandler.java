package de.sekmi.li2b2.services.impl.pm;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.services.impl.pm.ParamHandler;

public class TestParamHandler {
	

	@Test
	public void testSingleComponentIds() {
		ParamHandler ph = new ParamHandler(1) {
			
			@Override
			protected List<? extends Parameter> getAllParam(String... path) {
				return Collections.emptyList();
			}
			
			@Override
			protected Parameter addParam(String name, String type, String value, String... path) {
				return null;
			}
		};
		Assert.assertEquals("bla/1", ph.compileId(1, "bla"));
		Assert.assertArrayEquals(new String[] {"bla", "1"}, ph.parseId("bla/1"));

		
	}
	@Test
	public void testDoubleComponentIds() {
		ParamHandler ph = new ParamHandler(2) {
			
			@Override
			protected List<? extends Parameter> getAllParam(String... path) {
				return Collections.emptyList();
			}
			
			@Override
			protected Parameter addParam(String name, String type, String value, String... path) {
				return null;
			}
		};
		Assert.assertEquals("bla/blub/1", ph.compileId(1, "bla", "blub"));
		Assert.assertArrayEquals(new String[] {"bla", "blub", "1"}, ph.parseId("bla/blub/1"));
		
	}

}
