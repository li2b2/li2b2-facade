package de.sekmi.li2b2.api.pm;

/**
 * Interface for parameters. Parameters can be used to attach information
 * to projects, users and project-user-assignments.
 *
 * @author R.W.Majeed
 *
 */
public interface Parameter {
	String getName();
	String getValue();
	String getDatatype();
}