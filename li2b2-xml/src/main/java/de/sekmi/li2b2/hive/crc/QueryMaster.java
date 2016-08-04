package de.sekmi.li2b2.hive.crc;

import java.time.Instant;

/**
 * Represents the i2b2 query_master object
 * <pre><![CDATA[
<query_master>
    <query_master_id>23050</query_master_id>
    <name>CEU:N/W-mild de@14:18:02</name>
    <user_id>demo</user_id>
    <group_id>Demo</group_id>
    <create_date>2016-08-03T11:18:10.000Z</create_date>
</query_master>
]]></pre>
 * @author R.W.Majeed
 *
 */

public class QueryMaster {
	public String query_master_id;
	public String name;
	public String user_id;
	public String group_id;
	// TODO: use instant and implement XmlTypeAdapter
	public String createDate;
	
	protected QueryMaster(){
	}
	public QueryMaster(String master_id, String name, String user_id, Instant createDate){
		this.query_master_id = master_id;
		this.name = name;
		this.user_id = user_id;
		this.createDate = createDate.toString();
	}
}
