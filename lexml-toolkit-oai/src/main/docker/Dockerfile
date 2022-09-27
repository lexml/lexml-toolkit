FROM tomcat:8.5.82-jdk8-temurin-focal
#RUN apt-get update && \
#    apt-get install -y vim
COPY maven/ /usr/local/tomcat/webapps/
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh","run"]
