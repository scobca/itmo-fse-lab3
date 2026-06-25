package org.example.mbeans;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.management.MBeanServer;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Getter
public class MBeanConfig {

    private PointStats pointStats;
    private AverageTime averageTime;
    private MBeanServer server;
    private ObjectName pointStatsName;
    private ObjectName averageTimeName;

    @PostConstruct
    public void init() {
        try {
            server = ManagementFactory.getPlatformMBeanServer();

            pointStats = new PointStats();
            averageTime = new AverageTime();

            pointStatsName = new ObjectName("org.example:type=PointStats");
            averageTimeName = new ObjectName("org.example:type=AverageTime");

            Map<NotificationEmitter, ObjectName> beans = new HashMap<>();
            beans.put(pointStats, pointStatsName);
            beans.put(averageTime, averageTimeName);

            for (Map.Entry<NotificationEmitter, ObjectName> entry : beans.entrySet()) {
                server.registerMBean(entry.getKey(), entry.getValue());
                System.out.println("Registered MBean: " + entry.getValue());
            }

        } catch (Exception e) {
            System.err.println("Error registering MBeans: " + e.getLocalizedMessage());
        }
    }
}
