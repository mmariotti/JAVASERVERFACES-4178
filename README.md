# JAVASERVERFACES-4178

this is a test webapp for facelet cache expiring check happening too often.

I used a modified Wildfly 10.0.0.Final to run the test app.
Installed JSF 2.3.0-m06 (http://search.maven.org/remotecontent?filepath=org/glassfish/javax.faces/2.3.0-m06/javax.faces-2.3.0-m06.jar) following this method: http://stackoverflow.com/a/35901363/2911357

I'm using elipse debugger to run the **manual** test:

1. setup an adeguate wildfly (or equivalent) in eclipse 
2. download/clone this repo and import the project in eclipse
3. start wildfly in debug mode
4. deploy the webapp
5. put a condition breakpoint on `com.sun.faces.facelets.impl.DefaultFaceletCache:225` - condition: `url.toString().endsWith("/comp.xhtml")`
6. with a browser go to: `http://localhost:8080/JAVASERVERFACES-4178` (the breakpoint should not be triggered): the page shows 100 dummy lines
7. immediately refresh the page
8. proceed ("Resume" around 4-10 times) with debugger until page fully loads
9. immediately refresh the page (again)
10. proceed ("Resume" around 4-10 times) with debugger until page fully loads (again)
11. go take a coffe (wait some minute)
12. refresh the page (again)
13. proceed ("Resume" many many times) with debugger until page fully loads (again)

this is a proof that:

* `Util.getLastModified(url)` is called many times in a single request (it's a time consuming op)
* incrementing the threshold is conceptually wrong and leads to this kind of effect

Note that this is a simple page with 100 composite component instance of the same type.
In an average high-level management app, the size could easily be around 5000.



