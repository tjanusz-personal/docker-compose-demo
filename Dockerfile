FROM tomcat:9
ARG WARROOT=./target

COPY $WARROOT/demo.war /usr/local/tomcat/webapps