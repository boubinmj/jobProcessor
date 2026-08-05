package com.example;

import com.example.worker.EventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@SpringBootApplication
public class MyApplication {

	private final EventProcessor eventProcessor;

	public MyApplication(EventProcessor eventProcessor) {
		this.eventProcessor = eventProcessor;
	}

	@RequestMapping("/")
	String home() {
		return "Event-Driven Processing Simulator. API available at http://localhost:8080/api/events";
	}

	/**
	 * Start worker threads when the application is ready.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void startWorkers() {
		if (!eventProcessor.isRunning()) {
			eventProcessor.startWorkers();
			log.info("Application started. Worker threads are now processing events.");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}

}