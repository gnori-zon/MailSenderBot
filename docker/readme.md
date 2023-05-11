Before running docker-compose you need to create two volumes ```db-data``` and ```rabbit-data```.
To do this, in the docker client, write:

```
docker create volume db-data
```

And:

```
docker create volume rabbit-data
```

These volumes are necessary in order to save information in the system files and not delete them when containers are stopped.

Then you need to run docker-compose-rabbit for configerate broker:

```
docker-compose -f docker-compose-rabbit.yaml up
```


After that, you can open the RabbitMQ web interface in a browser using the link "http://127.0.0.1:15672/ ". Then create 2 elements (**queue** and **exchange**) and link them.
For this you need:
- log in using login: **guest** and password: **guest**.
<img width="377" alt="1" src="https://github.com/gnori-zon/MailSenderBot/assets/108410527/0d78fbdb-e1e8-4fd5-a8d8-56ab29343f87">

- Create a queue. **Specify a name** that you will need to specify later for ```client``` and ```worker```.
<img width="735" alt="2" src="https://github.com/gnori-zon/MailSenderBot/assets/108410527/113ce352-97f3-4a5e-8392-96d35b00ff4a">

- Create an exchange. **Specify a name** that you will need to specify later for ```client```. And also choose **fanout type**.
<img width="646" alt="3" src="https://github.com/gnori-zon/MailSenderBot/assets/108410527/a9ddea77-83b8-45db-88a6-a0839050ddd7">

- Then click on the created exchange. 
<img width="565" alt="4" src="https://github.com/gnori-zon/MailSenderBot/assets/108410527/53f507c1-a100-4cfd-8f53-98297ecb54ee">

- And then you **only need to specify the name** of the previously created queue and click "Bind".
<img width="649" alt="5" src="https://github.com/gnori-zon/MailSenderBot/assets/108410527/2a0f5309-d2f0-41a9-831a-cfd7040cadd6">


After creating the queue, you can stop the container:

```
docker-compose -f docker-compose-rabbit.yaml down
```

Then you can move on to configuring the main application.
- In service "bd" you can change ```POSTGRES_USER```, ```POSTGRES_PASSWORD``` and ```POSTGRES_DB```.
If the setting is changed, then the ```SPRING_DATASOURCE_USERNAME```, ```SPRING_DATASOURCE_PASSWORD``` and ```SPRING_DATASOURCE_URL``` settings in the ```client``` and ```worker``` will also need to be changed.
- Also, in ```client``` and ```worker```, you need to specify ```BOT_NAME``` and ```BOT_TOKEN```, which are responsible for the name of the bot and its token, respectively. 
- You also need to specify two keys (128-bit) in each ```CIPHER_INITVECTOR``` and ```CIPHER_KEY```, which are used to encrypt passwords and store them securely. They must be kept secret
- After for worker. It is necessary to add mailboxes and keys (password for the application), ```BASE_MAILS```, ```BASE_KEYS```, respectively. They are used to be sent anonymously by the user. Mailboxes must be **STRICTLY gmail**.

Once configured, you can run it by typing:

```
docker-compose -f docker-compose.yaml up
```

And also stop:

```
docker-compose -f docker-compose.yaml down
```
