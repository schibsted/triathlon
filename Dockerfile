FROM netflixoss/java:8

COPY ./build/distributions/Triathlon-1.0.tar /tmp/Triathlon-1.0.tar

RUN tar -xf /tmp/Triathlon-1.0.tar -C /opt
RUN ln -s /opt/Triathlon-1.0 /opt/Triathlon
ADD misc/eureka2-core-2.0.0-SNAPSHOT.jar /opt/Triathlon/lib/eureka2-core-2.0.0-rc.2.jar
RUN rm /tmp/Triathlon-1.0.tar

ENV archaius.deployment.environment=pro

CMD ["/opt/Triathlon/bin/Triathlon", "com.schibsted.triathlon.main.TriathlonServer"]
