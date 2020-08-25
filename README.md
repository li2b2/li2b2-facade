li2b2
=====
li2b2 - Lightweight i2b2 server and client libraries

This project is meant to be used by other software projects. Except for demos,
you can not run the software on its own.

Development of the li2b2-facate library was motivated by the AKTIN project https://aktin.org/

Example: Export ontology from i2b2 into XML file
--------
1. Put the release artifacts li2b2-client-*.jar and li2b2-xml-*.jar into a local directory
2. Run from command line: E.g. in Windows/git-bash: `java -cp "li2b2-client-0.7.jar;li2b2-xml-0.7.jar" de.sekmi.li2b2.client.ont.XMLExport`

The tool will provide an example on the command line output. E.g. to download a partial tree from the online demo server, use the following command:
```
java -cp "li2b2-client-0.7.jar;li2b2-xml-0.7.jar" de.sekmi.li2b2.client.ont.XMLExport http://services.i2b2.org/i2b2/services/PMService/ demo@i2b2demo demouser '\\i2b2_REP\i2b2\Reports\'
```

To export the full ontology, you can omit the last argument (subtree to export). Use full exports with care, as they will produce large files and load on the server.


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