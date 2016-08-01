package de.sekmi.li2b2.services.impl.crc;

import de.sekmi.li2b2.api.crc.ResultType;

public class ResultTypeImpl extends de.sekmi.li2b2.hive.crc.ResultType implements ResultType{

	@Override
	public Integer getId() {
		return this.result_type_id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

}
