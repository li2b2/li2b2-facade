

Development and testing
-----------------------
For development/testing, the server can be started from eclipse
by executing `li2b2-server/src/test/java/de.sekmi.li2b2.services.TestServer`. 

If you want to include the webclient in the demo server,
you do the following:

Download the webclient source code from https://www.i2b2.org/software/ 
or https://github.com/i2b2/i2b2-webclient/releases and install 
the bundle into your local maven repository manually via
```
 mvn install:install-file -Dfile=i2b2-webclient-1.7.12.0001.zip -DgroupId=org.i2b2 -DartifactId=webclient -Dversion=1.7.12.0001 -Dpackaging=zip
```
To import the webclient into eclipse, run `mvn -P webclient clean eclipse:eclipse`.

To update the eclipse project files and dependencies, right-click on `li2b2-server` project
and choose `Refresh`.

Now run the `TestServer` again, and go to http://localhost:8080/webclient/default.htm


Testing with standalone application
-----------------------------------
You can also run the demo server with i2b2 webclient in standalone mode.
First download and import the webclient zip file as described above.
 
To run the demo server as a standalon application, do as follows:
```
# Build the li2b2-facade server and dependencies
cd li2b2-facade
mvn -P webclient clean install
# go to server and collect transitive dependencies
cd server
mvn dependency:copy-dependencies
# put server.jar together with dependencies to execute it
cd target
cp li2b2-server-*.jar dependency/
cd dependency
# run http service (jdk8)
java -cp \* de.sekmi.li2b2.services.TestServer
# run http service (jdk9-10)
java --add-modules java.xml.bind -cp \* de.sekmi.li2b2.services.TestServer
```

Now point your browser to `http://localhost:8080/webclient/` and press login.

Accessing li2b2 server with a separate i2b2 webclient 
-----------------------------------------------------
In general and for production use, we recommend using the i2b2 webclient separately.

As mentioned above, you need to obtain the i2b2 webclient source code 
from GitHub: https://github.com/i2b2/i2b2-webclient/releases

For testing, unpack the i2b2 webclient source to a local folder.
In the file `i2b2_config_data.js` remove the line starting with `urlProxy` 
and change the value of `urlCellPM` to the URL of your running li2b2 server (shown in the console when running).
To serve the files from the local directory you can use e.g. python by 
executing `python -m http.server 8080` from within the webclient directory. Then, 
point your browser to `http://localhost:8080/default.htm`.

For production use, we recommend serving the i2b2 webclient as intended by the 
developers by using Apache and PHP. 
 


