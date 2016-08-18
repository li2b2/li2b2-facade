package de.sekmi.li2b2.client.crc;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.sekmi.li2b2.hive.crc.QueryStatusType;

//<query_instance>
//	<query_instance_id>23236</query_instance_id>
//	<query_master_id>23217</query_master_id>
//	<user_id>demo</user_id>
//	<group_id>Demo</group_id>
//	<start_date>2016-08-18T20:00:35.000Z</start_date>
//	<end_date>2016-08-18T20:00:35.000Z</end_date>
//	<query_status_type>
//	    <status_type_id>6</status_type_id>
//	    <name>COMPLETED</name>
//	    <description>COMPLETED</description>
//	</query_status_type>
//</query_instance>

@XmlRootElement(name="query_instance")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryInstance {
	public String query_instance_id;
	public String query_master_id;
	public String user_id;
	public String group_id;
	// TODO  use java type adapter
	public Calendar start_date;
	public Calendar end_date;
	public QueryStatusType query_status_type;
}
