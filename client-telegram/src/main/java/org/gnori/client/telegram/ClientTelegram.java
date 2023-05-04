package org.gnori.client.telegram;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ClientTelegram {
    public static void main(String[] args) {
        SpringApplication.run(ClientTelegram.class, args);
    }
}
