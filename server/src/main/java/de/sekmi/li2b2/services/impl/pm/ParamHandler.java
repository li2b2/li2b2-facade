package de.sekmi.li2b2.services.impl.pm;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.hive.HiveMessage;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;

public abstract class ParamHandler {
	int idPathLength;
	private static final char ID_PATH_SEPARATOR = '/';
	/**
	 * Construct a new param handler.
	 * @param manager project manager
	 * @param idPathLength Length of string components before the actual parameter index. E.g. 0 for global params and 2 for project user params.
	 */
	public ParamHandler(int idPathLength) {
		this.idPathLength = idPathLength;
	}
	
	String compileId(int index, String ...path) {
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
	public String[] parseId(String paramId) {
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
	// TODO methods for add, get, list, delete and update parameter
	protected abstract List<? extends Parameter> getAllParam(String ... path);

	private Parameter locateParam(String paramId, boolean delete) {
		String[] parts = parseId(paramId);
		if( parts == null ) {
			return null;
		}
		// get path part only (without index)
		String[] path = Arrays.copyOf(parts, idPathLength);
		// last part is numeric index
		int index = Integer.parseInt(parts[idPathLength]);
		List<? extends Parameter> params = getAllParam(path);
		if( params == null ) {
			return null;
		}
		if( delete ) {
			return params.set(index,null);
		}else {
			return params.get(index);
		}
	}
	protected Parameter getParam(String paramId) {
		return locateParam(paramId, false);
	}
	/**
	 * Add a parameter to the parameter collection. Path length must equal {@link #idPathLength}
	 * @param name parameter name
	 * @param type parameter type
	 * @param value parameter value
	 * @param path path components where the parameter should be added
	 * @return parameter added to the parameter collection, {@code null} if the path cannot be found.
	 */
	protected abstract Parameter addParam(String name, String type, String value, String...path);
	protected Parameter deleteParam(String paramId) {
		return locateParam(paramId, true);
	}
	// TODO need updateParam function?

	public void newParamResponse(HiveResponse response, Element param, String...path) {
		Parameter added = addParam(param.getAttribute("name"), param.getAttribute("datatype"), param.getTextContent(), path);
		if( added == null ) {
			// unable to add parameter
			response.setResultStatus("ERROR", "Invalid path to parameter collection");
		}
	}
	public void getParamResponse(HiveResponse response, String paramId) {
		Parameter par = getParam(paramId);
		if( par == null ) {
			response.setResultStatus("ERROR", "Parameter not found");			
		}else {
			Element el = response.addBodyElement(I2b2Constants.PM_NS, "param");
			el.setPrefix("ns4");
			el.setTextContent(par.getValue());
			el.setAttribute("datatype", par.getDatatype());
			el.setAttribute("id", paramId);
			el.setAttribute("name", par.getName());	
		}
	}
	public void allParamsResponse(HiveResponse response, String... path) {
		Element parent = response.addBodyElement(I2b2Constants.PM_NS, "params");
		parent.setPrefix("ns4");
		List<?extends Parameter> params = getAllParam(path);
		if( params == null ) {
			// unable to retrieve params for path
			response.setResultStatus("ERROR", "Invalid path to parameter collection");
			return;
		}
		for( int i=0; i<params.size(); i++ ) {
			Parameter par = params.get(i);
			// skip parameters set to null
			if( par == null ) {
				continue;
			}
			Element pel = HiveMessage.appendTextElement(parent, "param", par.getValue());
			pel.setAttribute("datatype", par.getDatatype());
			pel.setAttribute("id", compileId(i, path));
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
