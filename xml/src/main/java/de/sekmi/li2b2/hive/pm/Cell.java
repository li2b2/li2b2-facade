package de.sekmi.li2b2.hive.pm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cell_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cell {

	@XmlAttribute
	public String id;
	public String name;
	public String url;
	public String project_path;
	public String method;
	public Boolean can_override;

	protected Cell(){
	}
	public Cell(String id, String name, String url){
		this.id = id;
		this.name = name;
		this.url = url;
		this.project_path = "/";
		this.method = "REST";
		this.can_override = true;
	}
	@Override
	public String toString() {
		return "Cell["+id+"]";
	}
}
