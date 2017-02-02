li2b2
=====
li2b2 - Lightweight i2b2 server and client libraries

This project is meant to be used by other software projects. Except for demos,
you can not run the software on its own.

Examples
--------
TODO code examples

Users
-----
The li2b2-facade library is used by the following projects:
- AKTIN https://aktin.org/
- DZL https://www.dzl.de/
- GBN http://www.bbmri.de/

If you are using our software or know others who do, please let us know.


For Developers
==============

Compiling/Building the source code
----------------------------------
li2b2 is written in Java and requires a Java 8 Runtime Environment.
To build the project from source, you need to have 
[maven](https://maven.apache.org/) installed.

Build the project by executing `mvn clean install` in the
top directory (which also contains this README file).


Contributing
------------
If you would like to contribute by working on the source
code of this project, you might want to use an IDE.

The following instructions will show how to setup Eclipse
for this purpose, although you can use any IDE you want.

For Eclipse, first run `mvn clean install` to build the project and
then `mvn eclipse:eclipse` to generate Eclipse project files.

Open in Eclipse any workspace you want and import the projects
by choosing `File/Import...` and then `General/Existing Projects into Workspace`.
If you navigate the file browser to the top level directory, you should
see four projects which can be imported.