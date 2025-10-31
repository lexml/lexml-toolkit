FROM tomcat:9.0.111-jdk8-temurin-jammy
#RUN apt-get update && \
#    apt-get install -y vim
COPY maven/ /usr/local/tomcat/webapps/
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh","run"]
