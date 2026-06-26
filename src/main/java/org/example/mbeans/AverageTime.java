package org.example.mbeans;

import lombok.Getter;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class AverageTime extends NotificationBroadcasterSupport implements AverageTimeMBean {

    private double averageTime = 0.0;
    private Long lastClickTime = null;
    private int clickCount = 0;
    private long totalTime = 0;
    private long sequenceNumber = 1;

    // Пул потоков для имитации тяжелых вычислений
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile long threadCounter = 0;

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

        // При каждом клике создаем новый поток с тяжелыми вычислениями
        for (int n = 0; n < 50; n++) {
            performHeavyComputation();
        }

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

    /**
     * Метод, который создает новый поток и выполняет 1000 вычислений
     */
    private void performHeavyComputation() {
        final long threadId = ++threadCounter;
        Thread heavyThread = new Thread(() -> {
            try {
                System.out.println("🔄 Поток #" + threadId + " начал вычисления");

                // Выполняем 1000 итераций тяжелых вычислений
                for (int i = 1; i <= 1000; i++) {
                    double result = 0;
                    for (int j = 0; j < 100; j++) {
                        // Вычисление ряда Тейлора для синуса
                        double x = i * 0.01;
                        double sinResult = 0;
                        for (int k = 0; k < 20; k++) {
                            // Вычисление факториала и степени
                            long factorial = 1;
                            for (int f = 1; f <= (2*k + 1); f++) {
                                factorial *= f;
                            }
                            double term = Math.pow(-1, k) * Math.pow(x, 2*k + 1) / factorial;
                            sinResult += term;
                        }
                        result += sinResult;
                    }

                    if (i % 100 == 0) {
                        // Создаем массив и сразу заполняем его
                        double[] tempArray = new double[1000];
                        for (int a = 0; a < tempArray.length; a++) {
                            tempArray[a] = Math.sin(a) * Math.cos(a) * Math.tan(a * 0.01);
                        }
                        result += tempArray[0] + tempArray[tempArray.length - 1];
                    }

                    if (i % 250 == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }

                System.out.println("Поток #" + threadId + " завершил вычисления");

            } catch (Exception e) {
                System.err.println("Ошибка в потоке #" + threadId + ": " + e.getMessage());
            }
        });

        heavyThread.setName("Heavy-Computation-Thread-" + threadId);
        heavyThread.setDaemon(false);

        heavyThread.start();

        System.out.println("Создан новый поток #" + threadId + " для тяжелых вычислений");
    }

    public synchronized void reset() {
        averageTime = 0.0;
        lastClickTime = null;
        clickCount = 0;
        totalTime = 0;
        System.out.println("AverageTime statistics reset");
    }

    /**
     * Метод для очистки потоков при завершении
     */
    public void shutdown() {
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("ExecutorService did not terminate");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}