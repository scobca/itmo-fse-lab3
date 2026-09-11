# ОПИ Лаб 4

## Запуск wildfly на гелиосе

1. Билдим war-ник через `ant build`
2. Копируем war-ник в файл деплоя wildfly `cp dist/lab3.war ~/wildfly/standalone/deployments`
3. Обновляем JAVA_OPTIONS `JAVA_OPTS="$JAVA_OPTS \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=20500 \
-Dcom.sun.management.jmxremote.rmi.port=20500 \
-Djava.rmi.server.hostname=127.0.0.1 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false
-Xmx3g \
-Xms512m \
-XX:MaxMetaspaceSize=512m"`
4. Запускаем приложение из директории wildfly (используем смещение портов)
   `./bin/standalone.sh -Djboss.socket.binding.port-offset=12420`
5. Открываем ssh-сессию с пробросом портов на localhost
   `ssh -L 22410:localhost:22410 -L 20500:localhost:20500 -p 2222 s467883@se.ifmo.ru`

## Подключение JConsole к Wildfly (ура)

1. Добавляем **ТАКОЙ ЖЕ** инстанс wildfly как на гелиосе к нам на ноут (для примера положил его в корень проекта)
2. Создаем ssh подключение и запускаем Wildfly
   `ssh -L 22410:localhost:22410 -L 20500:localhost:20500 -p 2222 s467883@se.ifmo.ru`,
   `./wildfly/bin/standalone.sh -Djboss.socket.binding.port-offset=12420`
3. В **локальном** терминале запускаем JConsole из пакета Wildfly `bash wildfly/bin/jconsole.sh`
4. В открытом окне выбираем **Remote Process** и вставляем туда эту ссылочку
   `service:jmx:remote+http://127.0.0.1:22410`, в поля username и password вводим данные админ аккаунта (насколько я
   помню, я тебе его добавлял, username=admin, password=admin123)
5. Нажимаем **Connect** и кайфуем (вряд ли)

## Подключение VisualVM

1. Устанавливаем VisualVm локально в виде приложения
2. Запускаем из командной строки `/Applications/VisualVM.app/Contents/MacOS/visualvm \                            
  --cp:a ~/wildfly/bin/client/jboss-cli-client.jar`
3. В открывшемся окне выбираем File -> Add new JMX connection
4. В поле Connection вводим `service:jmx:http-remoting-jmx://localhost:22410`, сохраняем и подключаемся
5. При подключении вводим логин и пароль от админки wildfly
6. Выбираем наше новое соединение, переходим во вкладку MBeans, ищем org.example и наблюдаем наши кастомные бины