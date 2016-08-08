package de.sekmi.li2b2.hive.crc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="query_result_type")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryResultType {
	public Integer result_type_id;
	public String name;
	public String display_type;
	public String visual_attribute_type;
	public String description;
	
	protected QueryResultType(){
	}
	public QueryResultType(String name, String displayType, String description){
		this.name = name;
		this.display_type = displayType;
		this.description = description;
	}
}
