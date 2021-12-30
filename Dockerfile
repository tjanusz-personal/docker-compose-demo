# FROM tomcat:9 (this didn't produce AWS security scan results)
FROM tomcat:9-jdk17-corretto
ARG WARROOT=./target

COPY $WARROOT/demo.war /usr/local/tomcat/webapps