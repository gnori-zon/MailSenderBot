# MailSenderBot
telegram bot for mailings (spammer)

<p align="center">
   <img src="https://img.shields.io/badge/Version-1.1-important" alt="App Version">
   <img src="https://img.shields.io/badge/Lecense-MIT-9cf" alt="License">
</p>

## About

telegram bot for creating newsletters and their subsequent sending using the user's mail or using the bot's mail. The bot has integration with a database that stores account data (mail and key, encrypted). The database also stores the mailing history of each user. The sample letter contains (header, body text, attachment, recipients and the number of pieces for each recipient). 

## Documentation
If you will not use docker, then you need to have a working **RabbitMQ** server with the necessary **queue** and **exchange**. As well as the **postgreSQL** database. 
Use gradlew to build. You need to configure 2 yaml files in module **[client-telegram]** and **[send-mail-worker]**. Then use for build two service:

```
./gradlew :send-mail-worker:build
```

And

```
./gradlew :client-telegram:build
```

<details>
   <summary> Subject area</summary>
    <img width="541" alt="Снимок экрана 2024-02-24 в 12 27 49" src="https://github.com/gnori-zon/MailSenderBot/assets/108410527/ffff5e82-1903-4fee-a4df-e00c0df42789">
</details>

<details>
   <summary> Communication between services</summary>
   <img width="859" alt="Снимок экрана 2023-05-08 в 17 11 48" src="https://user-images.githubusercontent.com/108410527/236858295-3096cfca-dc88-4aa1-9086-a4f69f983d83.png">
</details>

<details>
   <summary> Model content</summary>
   Document and PhotoSize date types from telegram library 
   
   ```java
   public record Message(
           long accountId, 
           SendMode sendMode, 
           String title, 
           String text, 
           FileData fileData, 
           List<String> recipients, 
           int countForRecipient, 
           LocalDate sentDate
   ) implements Serializable {}

   enum SendMode {
    ANONYMOUSLY,
    CURRENT_MAIL;
   }
   
   record FileData(
        String id,
        String name,
        FileType type
   ) {}
   
   enum FileType {
        PHOTO,
        DOCUMENT;
   }
   ```
</details>

<details>
   <summary> Config for client-telegram </summary>
   
   ```yaml
   server:
    port: 8080
   #telegram
   bot:
      name: 
      token:
   service: 
      # (addresses to the tg-api file system)
      file-info:
         uri: https://api.telegram.org/bot{token}/getFile?file_id={fileId}
      file-storage:
         uri: https://api.telegram.org/file/bot{token}/{filePath} 

   # database
   spring:
      datasource:
         # (address of database)
         url: jdbc:postgresql://localhost:5432/ 
         username: 
         password: 
      jpa:
         # (schema generation for database)
         generate-ddl: true 
         show-sql: true
         hibernate:
            # (ddl mode)
            ddl-auto: create-drop 

   # rabbitMQ
      rabbitmq:
         # used default host and port
         username: guest 
         password: guest
         # (specifying the name of the queue to be created)
         queue-name: send.mail 
         # (specifying the name of the exchange to be created)
         exchange-name: exchange 
   #for Crypto !keys specified in .yaml(client) and .yaml(worker) must will be equals
   cipher:
      # (fist 128-bit key)
      initVector: F-JaNdRgUjXn2r5u 
      # (second 128-bit key)
      key: hVmYp3s6v9y$B&E) 
   ```
</details>

<details>
   <summary> Config for send-mail-worker </summary>
   
```yaml   
server:
  port: 8081
bot:
  name:
  token:
service:
  # (addresses to the tg-api file system)
  file-info:
    uri: https://api.telegram.org/bot{token}/getFile?file_id={fileId}
  file-storage:
    uri: https://api.telegram.org/file/bot{token}/{filePath}
# database
spring:
  datasource:
    # (address of database)
    url: jdbc:postgresql://localhost:5432/
    username: 
    password: 
  jpa:
    # (schema generation for database)
    generate-ddl: false
    show-sql: true
# rabbitMQ
  rabbitmq:
     addresses: localhost:5672
     username: guest 
     password: guest
     # (specifying the name of the queue to be listening)
     queue-name: send.mail 
#  for send-worker
base:
  # (specifying mails and keys(keys for app) to them through ' , ' which will be used by the bot for anonymous mailing (ONLY GMAIL))
  mails:
  keys:
#for Crypto !keys specified in .yaml(client) and .yaml(worker) must will be equals
cipher:
   # (fist 128-bit key)
   initVector: F-JaNdRgUjXn2r5u 
   # (second 128-bit key)
   key: hVmYp3s6v9y$B&E) 
```
</details>
   

<a href="https://github.com/gnori-zon/MailSenderBot/tree/master/docker">docker<a>

## Developers

- [gnori-zon](https://github.com/gnori-zon)
