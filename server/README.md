

Development and testing
-----------------------
For development/testing, the server can be started from eclipse
by executing `li2b2-server/src/test/java/de.sekmi.li2b2.services.TestServer`. 

If you want to include the webclient in the demo server,
you do the following:

Download the webclient source code from https://www.i2b2.org/software/ and install 
the bundle into your local maven repository manually via
```
 mvn install:install-file -Dfile=i2b2webclient-1709c.zip -DgroupId=org.i2b2 -DartifactId=webclient -Dversion=1.7.09c -Dpackaging=zip
```
To import the webclient into eclipse, run `mvn -P webclient clean eclipse:eclipse`.

To update the eclipse project files and dependencies, right-click on `li2b2-server` project
and choose `Refresh`.

Now run the `TestServer` again, and go to http://localhost:8080/webclient/default.htm