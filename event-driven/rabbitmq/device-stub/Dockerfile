FROM openjdk:8-jre-alpine

LABEL "service"="${project.name}:devicesStub" \
      "version"="${project.version}" \
      "revision"="${revision}" \
      "description"="${project.description}"

ENV FATJAR_FILE build/libs/${project.name}-${project.version}.jar

ENV APP_HOME /opt/apps

#ARG SERVICE_PORT
#ENV SERVICE_PORT \${SERVICE_PORT}
#EXPOSE \$SERVICE_PORT

COPY \$FATJAR_FILE \$APP_HOME/

WORKDIR \$APP_HOME

ENTRYPOINT ["sh", "-c"]

CMD ["java \$JAVA_OPTS -jar ${project.name}-${project.version}.jar"]