package org.example.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Named
@ApplicationScoped
public class ClockPushBean implements Serializable {

    @Inject
    @Push(channel = "clockChannel")
    private PushContext clockChannel;

    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::tick, 0, 9, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdownNow();
    }

    private void tick() {
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        clockChannel.send(time);
    }
}
