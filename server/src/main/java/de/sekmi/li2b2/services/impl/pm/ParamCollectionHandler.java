package de.sekmi.li2b2.services.impl.pm;

import java.util.List;
import java.util.function.Function;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.api.pm.ParameterCollection;

public class ParamCollectionHandler extends ParamHandler {
	private Function<String[],ParameterCollection> lookup;
	public ParamCollectionHandler(int pathLength, Function<String[],ParameterCollection> lookup) {
		super(pathLength);
		this.lookup = lookup;
	}
	public ParamCollectionHandler(int pathLength, Function<String[],ParameterCollection> lookup, String get_param_wrapper) {
		super(pathLength);
		this.lookup = lookup;
		this.get_param_wrapper = get_param_wrapper;
	}
	@Override
	protected List<? extends Parameter> getAllParam(String... path) {
		ParameterCollection c = lookup.apply(path);
		if( c == null ) {
			return null;
		}else {
			return c.getParameters();
		}
	}

	@Override
	protected Parameter addParam(String name, String type, String value, String... path) {
		ParameterCollection c = lookup.apply(path);
		if( c == null ) {
			return null;
		}else {
			return c.addParameter(name, type, value);
		}
	}

	@Override
	protected Parameter updateParam(String name, String type, String value, int index, String... path) {
		ParameterCollection c = lookup.apply(path);
		if( c == null ) {
			return null;
		}else {
			return c.updateParameter(index, name, type, value);
		}
	}
}
