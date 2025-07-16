package com.veritas.notification.api;

import com.veritas.notification.api.event.TransactionEvent;
import com.veritas.notification.api.service.NotificationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Main class for the Notification Api.
 */
@SpringBootApplication
@EnableFeignClients
@Slf4j
public class NotificationApiApplication {

	@Autowired
	private NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(NotificationApiApplication.class, args);
	}

	/**
	 *This method is a Kafka message listener for the "notificationTopic" topic.
	 *It handles incoming messages and processes the TransactionEvent object.
	 *@param transactionEvent The TransactionEvent object received from the Kafka message.
	 */
	@KafkaListener(topics = "notificationTopic")
	public void handleNotification(TransactionEvent transactionEvent) {
		log.info("Received notification for transaction: {}", transactionEvent.getTransactionId());
		notificationService.sendTransactionNotifications(transactionEvent);
	}
}
