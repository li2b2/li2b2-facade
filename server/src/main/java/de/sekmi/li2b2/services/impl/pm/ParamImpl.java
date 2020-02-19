package de.sekmi.li2b2.services.impl.pm;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.hive.pm.Param;

public class ParamImpl extends Param implements Parameter{

	public ParamImpl(String name, String value) {
		super(name,value);
	}
	/**
	 * Empty constructor for JAXB
	 */
	protected ParamImpl() {
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public String getDatatype() {
		return this.datatype;
	}

}
