package de.sekmi.li2b2.services.impl.crc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import de.sekmi.li2b2.api.crc.ResultType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ResultTypeImpl implements ResultType{
	private String name;
	private String description;
	private String displayType;

	/** default constructor for JAXB */
	private ResultTypeImpl() {
	}
	public ResultTypeImpl(String name, String displayType, String description){
		this();
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
