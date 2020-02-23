package de.sekmi.li2b2.api.pm;

import java.util.List;

/**
 * A collection of parameters. In i2b2 parameters can be 
 * stored at the cell, global, user, project and project-user level.
 * @author R.W.Majeed
 *
 */
public interface ParameterCollection {
	/**
	 * Parameter list. I2b2 allows multiple parameters with the same name.
	 * @return parameter list
	 */
	List<? extends Parameter> getParameters();
	Parameter addParameter(String name, String datatype, String value);
	Parameter updateParameter(int index, String name, String datatype, String value);

}
