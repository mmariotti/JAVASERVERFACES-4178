# JAVASERVERFACES-4178

this is a test webapp for facelet cache expiring check happening too often.

I used a modified Wildfly 10.0.0.Final to run the test app.
Installed JSF 2.3.0-m06 (http://search.maven.org/remotecontent?filepath=org/glassfish/javax.faces/2.3.0-m06/javax.faces-2.3.0-m06.jar) following this method: http://stackoverflow.com/a/35901363/2911357

I'm using elipse debugger to run the **manual** test:

1. setup an adeguate wildfly (or equivalent) in eclipse 
2. download/clone this repo and import the project in eclipse
3. start wildfly in debug mode
4. deploy the webapp
5. with a browser go to: `http://localhost:8080/JAVASERVERFACES-4178`
6. clear log
7. refresh the page: around 4-10 lines like: `---------------------------------> opening connection!! ...` 
8. clear log
9. refresh the page (again): around 4-10 lines like: `---------------------------------> opening connection!! ...` 
10. go take a coffe (wait some minute)
11. refresh the page (again): around 200 lines like: `---------------------------------> opening connection!! ...`

this is a proof that:

* `Util.getLastModified(url)` is called **unnecessarily** many times in a row (it's a time consuming op)
* incrementing the threshold is conceptually wrong and leads to this kind of effect

Note that this is a simple page with 100 composite component instance of the same type.
In an average high-level management app, the size could easily be around 5000.


I added a custom facelet cache implementation.

To enable it, just uncomment the line from `faces-config.xml` 
