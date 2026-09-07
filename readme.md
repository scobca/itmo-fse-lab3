# ОПИ Лаб 4

## Запуск wildfly на гелиосе

1. Билдим war-ник через `ant build`
2. Копируем war-ник в файл деплоя wildfly `cp dist/lab3.war ~/wildfly/standalone/deployments`
3. Запускаем приложение из директории wildfly (используем смещение портов)
   `./bin/standalone.sh -Djboss.socket.binding.port-offset=12420 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=7199 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false`
4. Открываем ssh-сессию с пробросом портов на локалхост
   `ssh -L 22410:localhost:22410 -L 20500:localhost:20500 -p 2222 s467883@se.ifmo.ru`

## Подключение VisualVM к Wildfly

1. Ставим VisualVM на ноут локально
2. Определяем путь к исходникам вайлдфлая на ноуте (в качестве примера, команды будут использовать wildfly который лежит
   в корне этого проекта)
3. Запускаем VisualVM из командной строки следующей командой
   `/Applications/VisualVM.app/Contents/Resources/visualvm/bin/visualvm -cp:a ./wildfly/bin/client/jboss-client.jar`