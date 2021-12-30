# FROM tomcat:9
FROM tomcat:9-jdk17-corretto
ARG WARROOT=./target

COPY $WARROOT/demo.war /usr/local/tomcat/webapps