package de.li2b2.client.work;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;

import org.w3c.dom.Element;

/**
 * {@code    
 <folder>
    <name>Female@02:51:39 [2-24-2020] [demo]</name>
    <user_id>demo</user_id>
    <group_id>Demo</group_id>
    <protected_access>N</protected_access>
    <share_id>Y</share_id>
    <index>\\demo\854I6FjW6315l22ozk1mA</index>
    <parent_index>73L7M14B2AjwJM2dzX4yH</parent_index>
    <visual_attributes>ZA </visual_attributes>
    <tooltip>Demo - Female@02:51:39 [2-24-2020] [demo]</tooltip>
    <work_xml>
        <ns5:plugin_drag_drop xmlns:ns5="http://www.i2b2.org/xsd/hive/plugin/">
            <ns4:query_master xmlns:ns4="http://www.i2b2.org/xsd/cell/crc/psm/1.1/">
                <query_master_id>53</query_master_id>
                <name>Female@02:51:39 [2-24-2020] [demo]</name>
                <user_id>demo</user_id>
                <group_id>Demo</group_id>
		</ns4:query_master>
	</ns5:plugin_drag_drop>
    </work_xml>
    <work_xml_i2b2_type>PREV_QUERY</work_xml_i2b2_type>
</folder>
}
 * @author R.W.Majeed
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Item {

	public String name;
	public String user_id;
	public String group_id;
	public char protected_access;
	public char share_id;
	public String index;
	public String parent_index;
	public String visual_attributes;
	public String tooltip;
	@XmlAnyElement
	public Element work_xml;
	public String work_xml_i2b2_type;
}
