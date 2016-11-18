package de.sekmi.li2b2.hive.crc;
//<query_status_type>
//	<status_type_id>6</status_type_id>
//	<name>COMPLETED</name>
//	<description>COMPLETED</description>
//</query_status_type>

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="query_status_type")
public class QueryStatusType {
	public int status_type_id;
	public String name;
	public String description;
}
