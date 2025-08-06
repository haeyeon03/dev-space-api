package kh.devspaceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // Spring Scheduler 활성화
@SpringBootApplication
public class DevSpaceApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(DevSpaceApiApplication.class, args);
	}

}
