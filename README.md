# MailSenderBot
telegram bot for mailings (spammer)

<p align="center">
   <img src="https://img.shields.io/badge/Version-1.0-important" alt="App Version">
   <img src="https://img.shields.io/badge/Lecense-MIT-9cf" alt="License">
</p>

## About

telegram bot for creating newsletters and their subsequent sending using the user's mail or using the bot's mail. The bot has integration with a database that stores account data (mail and key, encrypted). The database also stores the mailing history of each user. The sample letter contains (header, body text, attachment, recipients and the number of pieces for each recipient).

## Documentation

<details>
   <summary> Subject area</summary>
<img width="501" alt="subject area" src="https://user-images.githubusercontent.com/108410527/236857271-7d948c00-a254-44dd-b49c-c46ee2f02510.png">
</details>

<details>
   <summary> Communication between services</summary>
   <img width="859" alt="Снимок экрана 2023-05-08 в 17 11 48" src="https://user-images.githubusercontent.com/108410527/236858295-3096cfca-dc88-4aa1-9086-a4f69f983d83.png">
</details>

<details>
   <summary> Model content</summary>
   Document and PhotoSize date types from telegram library 
   
   ```java
   public class Message implements Serializable {
    Long chatId;
    SendMode sendMode;
    String title;
    String text;
    Document docAnnex;
    PhotoSize photoAnnex;
    byte[] binaryContentForAnnex;

    List<String> recipients;
    Integer countForRecipient = 1;
    Date sentDate;
    
   public enum SendMode {
    ANONYMOUSLY,
    CURRENT_MAIL;
}
   ```
</details>

<details>
   <summary> Config for client-telegram </summary>
</details>

<details>
   <summary> Config for send-mail-worker </summary>
</details>
## Developers

- [gnori-zon](https://github.com/gnori-zon)
