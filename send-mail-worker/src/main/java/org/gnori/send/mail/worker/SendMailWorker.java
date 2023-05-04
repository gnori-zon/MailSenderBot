package org.gnori.send.mail.worker;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class SendMailWorker {
  public static void main(String[] args) {
    SpringApplication.run(SendMailWorker.class, args);
  }
}
