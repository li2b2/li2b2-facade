package de.sekmi.li2b2.services.impl;

import javax.ws.rs.Path;

import de.sekmi.li2b2.services.AbstractCell;
import de.sekmi.li2b2.services.Cell;

public class ServerCell extends de.sekmi.li2b2.hive.pm.Cell {

//	private Class<? extends AbstractCell> clazz;
	public <T extends AbstractCell> ServerCell(Class<T> cell) {
//		this.clazz = cell;
		Cell a = cell.getAnnotation(de.sekmi.li2b2.services.Cell.class);
		if( a == null ) {
			throw new IllegalArgumentException("Class needs Cell annotation:"+cell.getName());
		}
		super.id = a.id();
		super.name = cell.getSimpleName();
		
		Path path = cell.getAnnotation(Path.class);
		if( path != null ) {
			super.url = path.value();
		}
		super.method = "REST";
		super.project_path = "/";
	}
}
