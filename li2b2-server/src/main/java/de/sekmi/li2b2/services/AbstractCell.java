package de.sekmi.li2b2.services;

import javax.ws.rs.Path;

import de.sekmi.li2b2.services.token.TokenManager;
public abstract class AbstractCell{
	/**
	 * Service name (for communication to client).
	 * <p>
	 *  The default implementation returns {@link Class#getSimpleName()}.
	 * </p>
	 * @return service name, e.g. Workplace Cell
	 */
	public String getName(){
		return getClass().getSimpleName();
	}
	/**
	 * Service version (for communication to client)
	 * <p>
	 * The default implementation returns {@link Package#getImplementationVersion()}
	 * for the implementing class.
	 * </p>
	 * @return service version, e.g. 1.700
	 */
	public String getVersion(){
		return getClass().getPackage().getImplementationVersion();
	}
	public String getCellId(){
		Cell cell = getClass().getAnnotation(Cell.class);
		if( cell == null ){
			throw new UnsupportedOperationException("Cell annotation not found. Use that or override the methods of AbstractCell");
		}
		return cell.id();
	}
	
	public String getURLPath() {
		Path path = getClass().getAnnotation(Path.class);
		if( path == null ){
			throw new UnsupportedOperationException("javax.ws.rs.Path annotation not found. Use that or override getURLPath()");
		}
		return path.value();
	}
}
