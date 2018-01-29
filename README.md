# sample-atmosphere-gwt-rpc
copied from https://github.com/Atmosphere/atmosphere-samples/tree/master/extensions-samples/gwt

using modular-webapp archetype
https://github.com/tbroyer/gwt-maven-archetypes

### Start the development mode

Change directory to your generated project and issue the following commands:

1. In one terminal window: `mvn gwt:codeserver -pl *-client -am`
2. In another terminal window: `mvn jetty:run -pl *-server -am -Denv=dev`

Or if you'd rather use Tomcat than Jetty, use `mvn tomcat7:run` instead of `mvn jetty:run`.

Note that the `-pl` and `-am` are not strictly necessary, they just tell Maven not to
build the client module when you're dealing with the server one, and vice versa.
