package de.sekmi.li2b2.services.impl.pm;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.hive.HiveMessage;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;

public abstract class ParamHandler {
	int idPathLength;
	private static final char ID_PATH_SEPARATOR = '/';
	protected String get_param_wrapper;
	private static class Location{
		String[] path;
		int index;
		List<? extends Parameter> params;

		public Location(String[] path, int index) {
			this.path = path;
			this.index = index;
		}
	}

	/**
	 * Construct a new param handler.
	 * @param idPathLength Length of string components before the actual parameter index. E.g. 0 for global params and 2 for project user params.
	 */
	public ParamHandler(int idPathLength) {
		this.idPathLength = idPathLength;
		this.get_param_wrapper = null;
	}
	
	final String compileId(int index, String ...path) {
		if( path.length != idPathLength ) {
			throw new IllegalArgumentException("Path array length differs from idPathLength");
		}
		StringBuilder b = new StringBuilder();
		for( int i=0; i<idPathLength; i++ ) {
			b.append(path[i]);
			b.append(ID_PATH_SEPARATOR);
		}
		b.append(Integer.toString(index));
		return b.toString();
	}

	/**
	 * Parse a parameter id into path components and index.
	 * E.g. {@code "Project/User/3"} has a path length of 2 and 
	 * will be parsed into the parts {@code ["Project", "User", "3"]}.
	 * @param paramId parameter id
	 * @return components or {@code null} if not enough parts
	 */
	public final String[] parseId(String paramId) {
		String[] parts = new String[idPathLength+1];
		int sep = paramId.length();
		int i=0;
		for( i=0; i<idPathLength; i++ ) {
			int pos = paramId.lastIndexOf(ID_PATH_SEPARATOR,sep-1);
			if( pos == -1 ) {
				// separator not found, abort parse operation
				return null;
			}else {
				parts[idPathLength-(i)] = paramId.substring(pos+1,sep);
				sep = pos;
			}
		}
		// first path component
		parts[0] = paramId.substring(0,sep);
		return parts;
	}

	protected abstract List<? extends Parameter> getAllParam(String ... path);


	private final Location locateParam(String paramId) {
		String[] parts = parseId(paramId);
		if( parts == null ) {
			return null;
		}
		// get path part only (without index)
		String[] path = Arrays.copyOf(parts, idPathLength);
		// last part is numeric index
		int index = Integer.parseInt(parts[idPathLength]);
		Location loc = new Location(path, index);
		loc.params = getAllParam(path);
		if( loc.params == null || index < 0 || index >= loc.params.size() ) {
			// invalid path or invalid index in path params
			return null; 
		}
		return loc;
	}
	protected final Parameter getParam(String paramId) {
		Location loc = locateParam(paramId);
		if( loc != null ) {
			return loc.params.get(loc.index);
		}else {
			// not found
			return null;
		}
	}
	/**
	 * Add a parameter to the parameter collection. Path length must equal {@link #idPathLength}
	 * @param name parameter name
	 * @param type parameter type
	 * @param value parameter value
	 * @param path path components where the parameter should be added
	 * @return parameter added to the parameter collection, {@code null} if the path cannot be found or the parameter could not be added.
	 */
	protected abstract Parameter addParam(String name, String type, String value, String...path);
	/**
	 *  Update the parameter at the specified location (path and index) with the given values.
	 *  If the path is invalid or the parameter cannot be updated, returns {@code null}.
	 *  Otherwise if the update was successful, returns the previously replaced parameter.
	 * @param name replacement name
	 * @param type replacement type
	 * @param value replacement value
	 * @param index parameter index within path
	 * @param path collection path
	 * @return previous parameter settings if successful; {@code null} if unsuccessful.
	 */
	protected abstract Parameter updateParam(String name, String type, String value, int index, String...path);
	/**
	 * Called to ask whether the parameter can be deleted. Default implementation always returns true.
	 * Override this method to prevent deleting (some) parameters
	 * @param index parameter index
	 * @param path path to parameter list
	 * @return whether delete is ok. If {@code true} is returned, the parameter will be deleted automatically.
	 */
	protected boolean deleteParamAllowed(int index, String...path) {
		return true;
	}
	protected final Parameter deleteParam(String paramId) {
		Location loc = locateParam(paramId);
		if( loc == null ) {
			return null;
		}
		// check if delete is ok
		if( true == deleteParamAllowed(loc.index, loc.path) ) {
			return loc.params.set(loc.index, null);
		}else {
			// delete rejected/forbidden
			return null;
		}
	}

	public final void setParamResponse(HiveResponse response, Element param, String...path) {
		String id = param.getAttribute("id");
		if( id.length() == 0 ) {
			id = null;
		}
		Parameter result;
		if( id == null ) {
			// no id given, create new param
			result = addParam(param.getAttribute("name"), param.getAttribute("datatype"), param.getTextContent(), path);
			if( result == null ) {
				// unable to add parameter
				response.setResultStatus("ERROR", "Invalid path to parameter collection");
			}
		}else {
			// existing id given, update existing parameter
			Location loc = locateParam(id);
			if( loc == null ) {
				response.setResultStatus("ERROR", "Unknown parameter id "+id);
			}
			result = updateParam(param.getAttribute("name"), param.getAttribute("datatype"), param.getTextContent(), loc.index, loc.path);
		}
	}
	public void getParamResponse(HiveResponse response, String paramId) {
		Parameter par = getParam(paramId);
		if( par == null ) {
			response.setResultStatus("ERROR", "Parameter not found");			
		}else {
			Element el;
			if( get_param_wrapper != null ) {
				Element parent = response.addBodyElement(I2b2Constants.PM_NS, get_param_wrapper);
				parent.setPrefix("ns4");
				el = parent.getOwnerDocument().createElement("param");
				parent.appendChild(el);				
			}else {
				el = response.addBodyElement(I2b2Constants.PM_NS, "param");				
			}
			el.setTextContent(par.getValue());
			el.setAttribute("datatype", par.getDatatype());
			el.setAttribute("id", paramId);
			el.setAttribute("name", par.getName());	
		}
	}
	public void allParamsResponse(HiveResponse response, String... path) {
		Element parent = response.addBodyElement(I2b2Constants.PM_NS, "params");
		parent.setPrefix("ns4");
		appendParams(parent, true, path);
	}
	public void appendParams(Element parent, boolean includeId, String ...path) {
		List<?extends Parameter> params = getAllParam(path);
		for( int i=0; i<params.size(); i++ ) {
			Parameter par = params.get(i);
			// skip parameters set to null
			if( par == null ) {
				continue;
			}
			Element pel = HiveMessage.appendTextElement(parent, "param", par.getValue());
			pel.setAttribute("datatype", par.getDatatype());
			if( includeId ) {
				pel.setAttribute("id", compileId(i, path));
			}
			pel.setAttribute("name", par.getName());
		}		
	}

	public void deleteParamResponse(HiveResponse response, String paramId) {
		Parameter par = deleteParam(paramId);
		if( par == null ) {
			response.setResultStatus("ERROR", "Parameter not found");
		}
	}

}
