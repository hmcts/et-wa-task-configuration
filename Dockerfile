ARG APP_INSIGHTS_AGENT_VERSION=3.4.11

# Application image

FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/et-wa-task-configuration.jar /opt/app/

EXPOSE 4551
CMD [ "et-wa-task-configuration.jar" ]
