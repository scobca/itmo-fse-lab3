package org.example.mbeans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.management.MBeanServer;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class MBeanConfig {

    @PostConstruct
    public void init() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            Map<NotificationEmitter, ObjectName> beans = new HashMap<>();

            beans.put(new PointStats(), new ObjectName("org.example:type=PointStats"));
            beans.put(new AverageTime(), new ObjectName("org.example:type=AverageTime"));

            for (Map.Entry<NotificationEmitter, ObjectName> entry : beans.entrySet()) {
                server.registerMBean(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
