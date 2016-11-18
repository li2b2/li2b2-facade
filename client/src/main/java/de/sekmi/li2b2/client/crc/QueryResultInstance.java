package de.sekmi.li2b2.client.crc;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.sekmi.li2b2.hive.crc.QueryResultType;
import de.sekmi.li2b2.hive.crc.QueryStatusType;

//<query_result_instance>
//	<result_instance_id>48833</result_instance_id>
//	<query_instance_id>23239</query_instance_id>
//	<description>Number of patients for "Female@23:33:09"</description>
//	<query_result_type>
//	    <result_type_id>4</result_type_id>
//	    <name>PATIENT_COUNT_XML</name>
//	    <display_type>CATNUM</display_type>
//	    <visual_attribute_type>LA</visual_attribute_type>
//	    <description>Number of patients</description>
//	</query_result_type>
//	<set_size>51</set_size>
//	<start_date>2016-08-18T21:33:13.000Z</start_date>
//	<end_date>2016-08-18T21:33:13.000Z</end_date>
//	<query_status_type>
//	    <status_type_id>3</status_type_id>
//	    <name>FINISHED</name>
//	    <description>FINISHED</description>
//	</query_status_type>
//</query_result_instance>

@XmlRootElement(name="query_result_instance")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryResultInstance {
	public String result_instance_id;
	public String query_instance_id;
	public String description;
	public QueryResultType query_result_type;
	public Integer set_size;
	public Calendar start_date;
	public Calendar end_date;
	public QueryStatusType query_status_type;
}
