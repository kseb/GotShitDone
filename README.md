# GotShitDone

## Intro

GotShitDone is a spring boot app which can send you a 
notification via [Telegram](https://telegram.org/) when a device connected to a
[fritz!dect](https://avm.de/produkte/fritzdect/fritzdect-200) starts and stops
working. It can handle as many devices as you want and the standby power consumption is
configurable as well as the notification messages. The stop message includes the cost and 
power consumption for the last job.

## Create your own Telegram bot

You have to start a Telegram conversation with @BotFather and create a new bot.
The token for your bot has to be configured in the application.yml.

## Build the app

Since it is a spring boot gradle project, you can build it with `./gradlew build`.
The executable jar file lies in `build/libs/GotShitDone.jar` and is ready to use with e.g. init.d.
You just have to create a link to the jar.

## Configuration

The configuration has to be in a config directory next to the jar file. It looks like this:

    fritzbox:
      url: http://fritz.box
      user: admin
      password: "xxx"
      deviceConfigs:
        -
          name: "FRITZ!DECT 200 #1"
          standbyInWatt: 10
          waitInSeconds: 600
          startMessage: "Washingmachine running."
          stopMessage: "Washingmachine ready. Costs: {COST}, Consumption: {WH}."
          pricePerKwh: 0.28025
    telegram:
      token: xxx
      botUsername: botusername
      allowedNumbers:
        - +49xxx
    intervalInMS: 2000
    logging:
      level.de.veltrus.gotShitDone.telegram.KnownChatIds: debug
    spring.jpa.hibernate.ddl-auto: update
    spring.datasource:
        url: jdbc:h2:./h2.database
        username: sa
        password:
    spring:
        jpa:
            database-platform: org.hibernate.dialect.H2Dialect
    
`waitInSeconds` is the time the device has to use less power than `standbyInWatt` to 
send the `stopMessage`. The `pricePerKwh` is used to generate the `{COST}` in the `stopMessage`.

## Authorization

When starting a telegram conversation with your new bot via `/start` you are asked to send your
contact details. The numbers which are allowed to get notifications by the app can be
configured with `allowedNumbers`. The chat ids are persisted in a h2 database which is located next
to the jar file.