package org.example.mbeans;

import lombok.Getter;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

@Getter
public class AverageTime extends NotificationBroadcasterSupport implements AverageTimeMBean {

    private double averageTime = 0.0;
    private Long lastClickTime = null;
    private int clickCount = 0;
    private long totalTime = 0;
    private long sequenceNumber = 1;

    public synchronized void addClick(long currentTimeMillis) {
        if (lastClickTime == null) {
            lastClickTime = currentTimeMillis;
            System.out.println("First click recorded at: " + currentTimeMillis);
            return;
        }

        long interval = currentTimeMillis - lastClickTime;

        if (interval < 0) {
            System.err.println("Negative interval detected: " + interval + " ms. Resetting.");
            lastClickTime = currentTimeMillis;
            return;
        }

        lastClickTime = currentTimeMillis;
        clickCount++;
        totalTime += interval;
        averageTime = (double) totalTime / clickCount;

        System.out.println("Click " + clickCount + ": interval = " + interval + " ms, average = " + averageTime + " ms");

        Notification notification = new Notification(
                "averageTime.calculated",
                this,
                sequenceNumber++,
                String.format("Среднее время между кликами: %.2f мс (на основе %d кликов)",
                        averageTime, clickCount)
        );
        sendNotification(notification);
        System.out.println("Сообщение отправлено.");
    }

    public synchronized void reset() {
        averageTime = 0.0;
        lastClickTime = null;
        clickCount = 0;
        totalTime = 0;
        System.out.println("AverageTime statistics reset");
    }
}