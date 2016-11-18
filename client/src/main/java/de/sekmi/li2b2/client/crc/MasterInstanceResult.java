package de.sekmi.li2b2.client.crc;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import de.sekmi.li2b2.hive.crc.QueryMaster;

@XmlAccessorType(XmlAccessType.FIELD)
public class MasterInstanceResult {
	protected MasterInstanceResult(){
	}
	public QueryMaster query_master;
	public QueryInstance query_instance;
	public List<QueryResultInstance> query_result_instance;
	public String getMasterId(){
		return query_master.query_master_id;
	}
}
