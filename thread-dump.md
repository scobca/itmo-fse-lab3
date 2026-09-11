# Thread Dump

**Снято:** 2026-09-08 22:20:06  
**JVM:** OpenJDK 64-Bit Server VM 17.0.12+7-1 (mixed mode, sharing)  

---

## Сводка по группам

| Группа                                           | Кол-во потоков        | Состояние                |
|--------------------------------------------------|-----------------------|--------------------------|
| JVM System (Reference Handler, Finalizer, etc.)  | 4                     | RUNNABLE / WAITING       |
| MSC service thread 1-x                           | 8                     | WAITING                  |
| ServerDeploymentRepository-temp-threads          | 1                     | WAITING                  |
| ServerService Thread Pool                        | 4                     | WAITING / TIMED_WAITING  |
| DeploymentScanner-threads                        | 2                     | WAITING / TIMED_WAITING  |
| management I/O                                   | 2                     | RUNNABLE                 |
| management Accept                                | 1                     | RUNNABLE                 |
| management task                                  | 2                     | TIMED_WAITING            |
| default I/O                                      | 32                    | RUNNABLE                 |
| default Accept                                   | 1                     | RUNNABLE                 |
| default task                                     | 1                     | WAITING                  |
| XNIO-1 I/O / Accept                              | 2                     | RUNNABLE                 |
| External Management Request Threads              | 1                     | WAITING                  |
| Timer-x                                          | 3                     | WAITING / TIMED_WAITING  |
| IdleRemover                                      | 1                     | TIMED_WAITING            |
| ConnectionValidator                              | 1                     | TIMED_WAITING            |
| JCA (IronJacamar)                                | 2                     | TIMED_WAITING            |
| Transaction (Arjuna JTS)                         | 4                     | WAITING / TIMED_WAITING  |
| Reference Reaper                                 | 3                     | WAITING                  |
| Weld Thread Pool                                 | 17                    | WAITING                  |
| CacheContainerServiceConfigurator                | 1                     | WAITING                  |
| expiration-thread                                | 2                     | TIMED_WAITING            |
| RxCachedWorkerPoolEvictor                        | 1                     | TIMED_WAITING            |
| Jaeger Tracing                                   | 2                     | WAITING / TIMED_WAITING  |
| pool-9-thread                                    | 3                     | RUNNABLE / TIMED_WAITING |
| pool-12-thread / pool-13-thread / pool-14-thread | 3                     | WAITING / TIMED_WAITING  |
| JFR (Java Flight Recorder)                       | 2                     | RUNNABLE / TIMED_WAITING |
| DestroyJavaVM                                    | 1                     | RUNNABLE                 |

---

## 1. JVM System Threads

### `Reference Handler` (t@2) — RUNNABLE

**Описание:** Системный поток JVM. Обрабатывает очередь Pending References — объекты, для которых GC уже определил, что они достижимы только через Reference (SoftReference, WeakReference, PhantomReference). Всегда работает в фоне.

```
java.lang.ref.Reference.waitForReferencePendingList (Native)
java.lang.ref.Reference$ReferenceHandler.run
```

---

### `Finalizer` (t@3) — WAITING

**Описание:** Системный поток JVM. Выполняет метод `finalize()` объектов, помещённых GC в очередь финализации. Ожидает появления объектов в `ReferenceQueue`. Нормальное состояние при отсутствии объектов для финализации.

```
Object.wait → ReferenceQueue.remove → Finalizer$FinalizerThread.run
```

---

### `Signal Dispatcher` (t@4) — RUNNABLE

**Описание:** Системный поток JVM. Диспетчеризирует сигналы ОС (например, SIGTERM, SIGINT) к соответствующим обработчикам внутри JVM. Стека вызовов нет — это native-поток.

---

### `Common-Cleaner` (t@20) — TIMED_WAITING

**Описание:** Системный поток JVM (Java 9+). Реализует механизм `java.lang.ref.Cleaner` — замена финализаторов. Ожидает объекты в `ReferenceQueue` для выполнения зарегистрированных cleanup-действий.

```
Object.wait → ReferenceQueue.remove → CleanerImpl.run → InnocuousThread.run
```

---

### `Notification Thread` (t@21) — RUNNABLE

**Описание:** Системный поток JVM. Отправляет уведомления через JMX (Java Management Extensions). Активен когда есть JMX-подписчики на нотификации.

---

### `DestroyJavaVM` (t@35) — RUNNABLE

**Описание:** Системный поток JVM. Является главным (main) потоком, переименованным после завершения метода `main()`. Ждёт завершения всех non-daemon потоков перед выходом из JVM. Нормальное состояние для запущенного сервера.

---

## 2. JBoss Modules — Reference Reaper

### `Reference Reaper` (t@22) — WAITING

**Описание:** Поток JBoss Modules. Периодически очищает слабые/фантомные ссылки, которые JBoss Modules использует для слежения за ClassLoader'ами и ресурсами модулей. Ожидает появления объектов в очереди.

```
Object.wait → ReferenceQueue.remove → org.jboss.modules.ref.References$ReaperThread.run
```

---

## 3. WildFly — MSC (Modular Service Container)

### `MSC service thread 1-1` ... `1-8` (t@25–t@32) — WAITING × 8

**Описание:** Пул рабочих потоков контейнера сервисов WildFly (MSC). Используются для запуска, остановки и управления жизненным циклом сервисов (деплой, анддеплой компонентов). В idle-состоянии ожидают задачи в `EnhancedQueueExecutor`. Нормально для работающего сервера без активных операций деплоя.

```
Unsafe.park → EnhancedQueueExecutor$ThreadBody.run
```

---

## 4. WildFly — Deployment Repository

### `ServerDeploymentRepository-temp-threads - 1` (t@33) — WAITING

**Описание:** Вспомогательный поток для временных операций с репозиторием деплоев WildFly (копирование артефактов при деплое). Ожидает задачи в `ScheduledThreadPoolExecutor`. В состоянии покоя, т.е. активных операций деплоя нет.

```
LockSupport.park → ScheduledThreadPoolExecutor$DelayedWorkQueue.take → ThreadPoolExecutor.runWorker
```

---

## 5. WildFly — Server Service Thread Pool

### `ServerService Thread Pool -- 1` (t@36), `-- 41` (t@78), `-- 80` (t@173), `-- 91` (t@227) — WAITING / TIMED_WAITING

**Описание:** Основной пул потоков для выполнения задач WildFly-сервисов: обработка запросов на управление, загрузка классов, инициализация сабсистем. Потоки ожидают задачи в очереди `ScheduledThreadPoolExecutor`. Нормальное состояние в idle-режиме.

Поток `-- 90` (t@216) — чуть отличается: ожидает в `EnhancedQueueExecutor` (другой тип пула), но назначение аналогичное.

```
LockSupport.park → ScheduledThreadPoolExecutor$DelayedWorkQueue.take → JBossThread.run
```

---

## 6. WildFly — Deployment Scanner

### `DeploymentScanner-threads - 1` (t@75) — TIMED_WAITING
### `DeploymentScanner-threads - 2` (t@76) — WAITING

**Описание:** Потоки сканера деплоев WildFly. Периодически проверяют директорию `deployments/` на наличие новых артефактов (`.war`, `.ear`, `.jar`). Поток-1 активно ждёт с таймаутом (TIMED_WAITING), поток-2 — пассивно (WAITING). Нормальное состояние.

```
LockSupport.park/parkNanos → ScheduledThreadPoolExecutor$DelayedWorkQueue.take → JBossThread.run
```

---

## 7. WildFly — XNIO / NIO I/O Threads

### `management I/O-1`, `management I/O-2` (t@115–t@116) — RUNNABLE × 2

**Описание:** XNIO-потоки для обработки I/O запросов management-интерфейса WildFly (HTTP/native management port 9990/9999). Блокируются в NIO-селекторе через `KQueue.poll` (macOS/BSD) ожидая входящих событий от управляющих соединений.

---

### `management Accept` (t@117) — RUNNABLE

**Описание:** XNIO-поток, принимающий входящие TCP-соединения на management-интерфейсе WildFly. Постоянно слушает через `KQueue` selector.

---

### `management task-1`, `management task-2` (t@229, t@232) — TIMED_WAITING × 2

**Описание:** Рабочие потоки для выполнения задач по запросам management-интерфейса (например, JMX-запросы, CLI-команды). В данный момент простаивают в `EnhancedQueueExecutor`.

---

### `default I/O-1` ... `default I/O-32` (t@119–t@150) — RUNNABLE × 32

**Описание:** Основные XNIO NIO-потоки для обработки HTTP-трафика приложений (обычно порт 8080). 32 потока обслуживают входящие соединения через NIO KQueue selector. Большое количество потоков обеспечивает высокую пропускную способность. Все потоки активны (`RUNNABLE`) — нормально для NIO-сервера в режиме ожидания соединений.

```
KQueue.poll (Native) → KQueueSelectorImpl.doSelect → SelectorImpl.select → WorkerThread.run
```

---

### `default Accept` (t@151) — RUNNABLE

**Описание:** Поток принятия входящих соединений для дефолтного коннектора (HTTP 8080). Постоянно слушает новые TCP-соединения через KQueue.

---

### `default task-1` (t@237) — WAITING

**Описание:** Рабочий поток пула задач дефолтного XNIO-воркера. Ожидает HTTP-запросы для обработки.

---

### `XNIO-1 I/O-1` (t@156), `XNIO-1 Accept` (t@157) — RUNNABLE × 2

**Описание:** Дополнительный XNIO-воркер (XNIO-1), вероятно, связан с AJP или дополнительным коннектором. Назначение аналогично `default I/O` и `default Accept`.

---

### `External Management Request Threads -- 1` (t@236) — WAITING

**Описание:** Поток для обработки внешних управляющих запросов к WildFly (например, через JBoss CLI или remote management API). Ожидает задачи.

---

## 8. JCA (IronJacamar) — Пул соединений

### `IdleRemover` (t@152) — TIMED_WAITING

**Описание:** Поток JBoss IronJacamar (JCA). Периодически удаляет простаивающие соединения из пулов (DataSource, JMS ConnectionFactory). Просыпается по таймеру, проверяет idle-timeout и закрывает устаревшие соединения. Нормальное фоновое поведение.

```
LockSupport.parkNanos → AbstractQueuedSynchronizer$ConditionObject.await → IdleRemover$IdleRemoverRunner.run
```

---

### `ConnectionValidator` (t@153) — TIMED_WAITING

**Описание:** Поток JBoss IronJacamar (JCA). Периодически проверяет валидность соединений в пуле (background validation). Нормальное поведение для управления пулом DataSource.

```
LockSupport.parkNanos → ConnectionValidator$ConnectionValidatorRunner.run
```

---

## 9. Transaction Manager (Arjuna / JTS)

### `Transaction Expired Entry Monitor` (t@158) — TIMED_WAITING

**Описание:** Поток JBoss JTS (Java Transaction Service). Периодически сканирует журнал транзакций и удаляет просроченные записи (expired transaction log entries). Пробуждается по таймеру.

```
Object.wait → ExpiredEntryMonitor.run
```

---

### `Periodic Recovery` (t@155) — TIMED_WAITING

**Описание:** Поток периодического восстановления транзакций (Arjuna). Регулярно запускает процедуру recovery — поиск и восстановление незавершённых транзакций после сбоев. Критически важен для JTA-целостности.

```
Object.wait → PeriodicRecovery.doPeriodicWait → PeriodicRecovery.run
```

---

### `Transaction Reaper` (t@159) — TIMED_WAITING

**Описание:** Поток контроля timeout транзакций (Arjuna). Отслеживает транзакции, превысившие допустимый timeout, и инициирует их принудительный откат (rollback). Ждёт с таймаутом.

```
Object.wait → ReaperThread.run
```

---

### `Transaction Reaper Worker 0` (t@160) — WAITING

**Описание:** Рабочий поток Transaction Reaper. Выполняет фактический откат просроченных транзакций по сигналу от `Transaction Reaper`. В данный момент нет просроченных транзакций.

```
Object.wait → TransactionReaper.waitForWork → ReaperWorkerThread.run
```

---

## 10. WildFly Common — Reference Reapers

### `Reference Reaper #1`, `#2`, `#3` (t@161–t@163) — WAITING × 3

**Описание:** Потоки `org.wildfly.common` для очистки слабых/фантомных ссылок, используемых внутри WildFly. Все три ожидают на одном и том же `ReferenceQueue` (адрес `<4021d6f8>`). Нормальное фоновое поведение.

```
Object.wait → ReferenceQueue.remove → org.wildfly.common.ref.References$ReaperThread.run
```

---

## 11. RxJava

### `RxCachedWorkerPoolEvictor-1` (t@166) — TIMED_WAITING

**Описание:** Фоновый поток RxJava (реактивное программирование). Периодически выгружает (evict) неиспользуемые воркеры из кэшированного пула `CachedThreadScheduler`. Нормальное поведение для RxJava runtime.

```
LockSupport.parkNanos → ScheduledThreadPoolExecutor$DelayedWorkQueue.take
```

---

## 12. Jaeger Distributed Tracing

### `jaeger.RemoteReporter-QueueProcessor` (t@167) — WAITING

**Описание:** Поток клиента Jaeger (distributed tracing). Считывает spans из внутренней очереди и отправляет их в агент/коллектор Jaeger по UDP/HTTP. В данный момент очередь пуста — ожидает новых span'ов.

```
ArrayBlockingQueue.take → RemoteReporter$QueueProcessor.run
```

---

### `jaeger.RemoteReporter-FlushTimer` (t@168) — TIMED_WAITING

**Описание:** Таймерный поток Jaeger. Принудительно сбрасывает (flush) накопленные spans в агент по истечении интервала, даже если буфер не заполнен. Работает в паре с `QueueProcessor`.

```
Object.wait → TimerThread.mainLoop
```

---

## 13. Infinispan / Cache — Expiration Threads

### `expiration-thread--p5-t1` (t@170), `expiration-thread--p10-t1` (t@171) — TIMED_WAITING × 2

**Описание:** Потоки Infinispan (распределённый кэш WildFly). Периодически сканируют кэш-контейнеры на наличие просроченных записей (expired entries) и удаляют их. `p5` и `p10` — приоритет потока. Нормальное фоновое обслуживание кэша.

```
LockSupport.parkNanos → ScheduledThreadPoolExecutor$DelayedWorkQueue.take → ContextReferenceExecutor.execute → ContextualExecutor$1.run
```

---

### `CacheContainerServiceConfigurator - 1` (t@172) — WAITING

**Описание:** Рабочий поток, связанный с конфигурацией сервисов кэш-контейнера Infinispan. Ожидает задачи в `LinkedBlockingQueue`. Нормальное idle-состояние.

```
LinkedBlockingQueue.take → ThreadPoolExecutor.runWorker → ContextualExecutor$1.run → JBossThread.run
```

---

## 14. Weld (CDI Container)

### `Weld Thread Pool -- 1` ... `-- 17` (t@175–t@192) — WAITING × 17

**Описание:** Пул потоков CDI-контейнера Weld (реализация Jakarta CDI для WildFly). Используются для асинхронных CDI-событий (`@Observes @Asynchronous`), параллельной инициализации бинов при деплое и других CDI-операций. Все 17 потоков ожидают задачи в `LinkedBlockingQueue`. Нормальное idle-состояние — нет активных асинхронных событий.

```
LockSupport.park → LinkedBlockingQueue.take → ThreadPoolExecutor.runWorker → JBossThread.run
```

---

## 15. Таймеры (`java.util.Timer`)

### `Timer-0` (t@118), `Timer-1` (t@154), `Timer-2` (t@242) — WAITING / TIMED_WAITING × 3

**Описание:** Потоки стандартного `java.util.Timer`. Каждый экземпляр Timer создаёт свой поток. Ожидают запланированных задач в `TaskQueue`. Конкретные задачи не видны из стека — зависит от того, кто создал Timer. Вероятно, используются компонентами WildFly или приложениями для периодических задач.

```
Object.wait → TimerThread.mainLoop → TimerThread.run
```

---

## 16. Java Flight Recorder (JFR)

### `JFR Recorder Thread` (t@239) — RUNNABLE

**Описание:** Системный поток JVM. Отвечает за сбор данных JFR (Java Flight Recorder) и запись событий в файл/буфер. Активен всегда при включённой записи JFR.

---

### `JFR Periodic Tasks` (t@240) — TIMED_WAITING

**Описание:** Системный поток JVM. Выполняет периодические задачи JFR: ротацию chunk-файлов, сбор метрик по расписанию. Периодически просыпается по таймеру.

```
Object.wait → PlatformRecorder.takeNap → PlatformRecorder.periodicTask
```

---

## 17. pool-N-thread — Анонимные пулы

### `pool-12-thread-1` (t@235) — TIMED_WAITING

**Описание:** Поток анонимного `ScheduledThreadPoolExecutor`. Выполняет периодические задачи по расписанию. Конкретный владелец пула не определяется из стека — вероятно, создан кодом приложения или одной из библиотек.

---

### `pool-13-thread-1` (t@228) — WAITING

**Описание:** Поток анонимного `ScheduledThreadPoolExecutor`. Аналогично pool-12, ожидает задачу.

---

### `pool-14-thread-1` (t@238) — TIMED_WAITING

**Описание:** Поток анонимного `ScheduledThreadPoolExecutor`. Аналогично pool-12.

---

### `pool-9-thread-5`, `pool-9-thread-7` (t@243, t@245) — TIMED_WAITING × 2

**Описание:** Потоки анонимного пула с `SynchronousQueue` (cached thread pool). Ожидают задачу с таймаутом keepAlive — если задачи нет, поток завершится. Нормально для `Executors.newCachedThreadPool()`.

```
SynchronousQueue.poll → ThreadPoolExecutor.getTask → ThreadPoolExecutor.runWorker
```

---

### --> `pool-9-thread-6` (t@244) — RUNNABLE 

**Описание:** Поток, который в момент снятия дампа **выполнял сам дамп потоков** через JMX. Виден полный стек вызова от VisualVM → JMX → `ThreadImpl.dumpAllThreads`. Это мета-поток: именно он инициировал этот thread dump.

```
ThreadImpl.dumpThreads0 (Native)
→ sun.management.ThreadImpl.dumpAllThreads
→ JMX invoke chain (JmxMBeanServer → PluggableMBeanServerImpl → AuthorizingMBeanServer)
→ org.jboss.remotingjmx ServerProxy (remote JMX)
→ SecurityIdentity.runAs (Elytron security)
→ ThreadPoolExecutor.runWorker
```

---

## Общие наблюдения

**Состояние сервера:** нормальное, idle. Нет зависших потоков, deadlock'ов или аномально долгих операций.

**RUNNABLE-потоки:** большинство из них (32× `default I/O`, `management I/O`, `management Accept`, `default Accept`, `XNIO-1`) заблокированы в нативном `KQueue.poll` — это нормально для NIO-сервера: поток помечается RUNNABLE, но фактически спит в ядре ОС, ожидая I/O-событий.

**Отсутствие application-потоков:** нет потоков бизнес-логики или HTTP-воркеров с кодом приложения, что подтверждает: в момент снятия дампа запросов к приложению не поступало.

**Размер пулов:**
- `default I/O`: 32 потока — высокая конфигурация XNIO для обработки HTTP
- `Weld Thread Pool`: 17 потоков — стандартный CDI-пул
- `MSC service thread`: 8 потоков — управление сервисами

**Технологический стек:** WildFly 26.x / JBoss EAP, Weld CDI, Infinispan, Arjuna JTS, IronJacamar, XNIO 3.8.7, Jaeger tracing, RxJava.
