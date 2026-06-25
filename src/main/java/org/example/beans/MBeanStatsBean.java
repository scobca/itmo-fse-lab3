package org.example.beans;

import org.example.mbeans.MBeanConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("mbeanStatsBean")
@ApplicationScoped
public class MBeanStatsBean implements Serializable {

    @Inject
    private MBeanConfig mbeanConfig;

    public int getTotalPoints() {
        try {
            return mbeanConfig.getPointStats().getTotalPoints();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMissPoints() {
        try {
            return mbeanConfig.getPointStats().getMissPoints();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getHitPercentage() {
        try {
            int total = getTotalPoints();
            int miss = getMissPoints();

            if (total == 0) return "0.0";
            double percentage = ((double) (total - miss) / total) * 100;
            return String.format("%.1f", percentage);
        } catch (Exception e) {
            return "0.0";
        }
    }

    public String getAverageTime() {
        try {
            double avg = mbeanConfig.getAverageTime().getAverageTime();
            return String.format("%.2f", avg);
        } catch (Exception e) {
            return "0.00";
        }
    }

    public int getClickCount() {
        try {
            return mbeanConfig.getAverageTime().getClickCount();
        } catch (Exception e) {
            return 0;
        }
    }

    public void resetStats() {
        try {
            mbeanConfig.getPointStats().reset();
            mbeanConfig.getAverageTime().reset();
            System.out.println("MBean statistics reset successfully");
        } catch (Exception e) {
            System.err.println("Error resetting MBean statistics: " + e.getMessage());
        }
    }
}