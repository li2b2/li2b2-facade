

Dependencies
------------
For development/testing, the server can be started with
`mvn -P jetty jetty:run-war`. This functionality needs i2b2 webclient. 
You may need to download the webclient from i2b2.org/software and 
install the bundle into your local maven repository manually via
```
 mvn install:install-file -Dfile=i2b2webclient-1707c.zip -DgroupId=org.i2b2 -DartifactId=webclient -Dversion=1.7.07c -Dpackaging=zip
```

 