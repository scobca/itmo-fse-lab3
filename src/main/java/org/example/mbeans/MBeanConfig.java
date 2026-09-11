package org.example.mbeans;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

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


            server.registerMBean(pointStats, pointStatsName);
            server.registerMBean(averageTime, averageTimeName);
            System.out.println("Registered MBean");

        } catch (Exception e) {
            System.err.println("Error registering MBeans: " + e.getLocalizedMessage());
        }
    }
}
