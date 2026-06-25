package org.example.beans;

import lombok.Getter;
import lombok.Setter;
import org.example.mbeans.MBeanConfig;

import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class PointBean implements Serializable {

    private Double x;
    private Double y;
    private Double r;

    @Inject
    private ResultsBean resultsBean;

    @Inject
    private MBeanConfig mbeanConfig;

    public void submit() {
        long startTime = System.nanoTime();

        boolean hit = checkHit(x, y, r);

        try {
            mbeanConfig.getPointStats().addPoint(hit);
            mbeanConfig.getAverageTime().addClick(System.currentTimeMillis());
        } catch (Exception e) {
            System.err.println("Error updating MBean stats: " + e.getMessage());
        }

        PointResult result = new PointResult();
        result.setX(x);
        result.setY(y);
        result.setR(r);
        result.setHit(hit);
        result.setServerTime(LocalDateTime.now());
        result.setProcessingTime((System.nanoTime() - startTime)/ 1_000_000.0);

        resultsBean.addResult(result);
    }

    private boolean checkHit(double x, double y, double r) {
        // Треугольник
        if (x <= 0 && y <= 0 && y >= -x - r) return true;

        // Прямоугольник
        if (x >= 0 && y >= 0 && x <= r && y <= r/2) return true;

        // Четверть круга
        return x >= 0 && y <= 0 && (x * x + y * y <= r * r / 4);
    }
}