package org.example.mbeans;

import lombok.Getter;
import org.example.mbeans.interfaces.IPointStats;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

@Getter
public class PointStats extends NotificationBroadcasterSupport implements IPointStats {

    private int totalPoints = 0;
    private int missPoints = 0;
    private int missSequence = 0;
    private long sequenceNumber = 1;

    public synchronized void addPoint(boolean isHit) {
        totalPoints++;

        if (!isHit) {
            missPoints++;
            missSequence++;
        } else resetMissSequence();

        if (missSequence == 4) {
            Notification notification = new Notification(
                    "points.4misses",
                    this,
                    sequenceNumber++,
                    "Совершено 4 промаха подряд."
            );

            sendNotification(notification);
            resetMissSequence();
        }
    }

    @Override
    public void reset() {
        totalPoints = 0;
        missPoints = 0;
    }

    private void resetMissSequence() {
        missSequence = 0;
    }
}
