package de.sekmi.li2b2.services.impl.crc;

import de.sekmi.li2b2.api.crc.ResultType;

public class ResultTypeImpl implements ResultType{
	private String name;
	private String description;
	private String displayType;

	public ResultTypeImpl(String name, String displayType, String description){
		this.name = name;
		this.displayType = displayType;
		this.description = description;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getDisplayType() {
		return displayType;
	}

}
