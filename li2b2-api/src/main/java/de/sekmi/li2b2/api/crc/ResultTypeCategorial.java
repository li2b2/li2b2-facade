package de.sekmi.li2b2.api.crc;

public class ResultTypeCategorial implements ResultType {
	private String name;
	private String description;

	public ResultTypeCategorial(String name, String description){
		this.name = name;
		this.description = description;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayType() {
		return "CATNUM";
	}

	@Override
	public String getDescription() {
		return description;
	}

}
