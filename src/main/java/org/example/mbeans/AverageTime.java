package org.example.mbeans;

import org.example.mbeans.interfaces.IAverageTime;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class AverageTime extends NotificationBroadcasterSupport implements IAverageTime {

    private double averageTime = 0.0;
    private long lastClickTime = 0;
    private int clickCount = 0;
    private long totalTime = 0;
    private long sequenceNumber = 1;
    private final int NOTIFICATION_THRESHOLD = 10; // оповещение после 10 кликов

    public synchronized void addClick(long currentTime) {
        updateStats(currentTime - lastClickTime);

        if (lastClickTime == 0) {
            lastClickTime = currentTime;
            return;
        } else lastClickTime = currentTime;

        Notification notification = new Notification(
                "averageTime.calculated",
                this,
                sequenceNumber++,
                String.format("Среднее время между кликами: %.2f мс (на основе %d кликов)",
                        averageTime, clickCount)
        );
        sendNotification(notification);
    }

    private void updateStats(long interval) {
        clickCount++;
        totalTime += interval;
        averageTime = (double) totalTime / clickCount;
    }

    @Override
    public double getAverageTime() {
        return 0;
    }

    @Override
    public void reset() {
        averageTime = 0.0;
        lastClickTime = 0;
        clickCount = 0;
        totalTime = 0;
    }
}
