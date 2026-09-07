# ОПИ Лаб 4

## Запуск wildfly на гелиосе

1. Билдим war-ник через `ant build`
2. Копируем war-ник в файл деплоя wildfly `cp dist/lab3.war ~/wildfly/standalone/deployments`
3. Запускаем приложение из директории wildfly (используем смещение портов)
   `./bin/standalone.sh -Djboss.socket.binding.port-offset=12420`
4. Открываем ssh-сессию с пробросом портов на локалхост
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