FROM tomcat:8.5.61-jdk8-openjdk-slim-buster
RUN apt-get update && \
    apt-get install -y vim
COPY maven/ /usr/local/tomcat/webapps/
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh","run"]
