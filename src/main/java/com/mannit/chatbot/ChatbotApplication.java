package com.mannit.chatbot;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.mannit.chatbot")
@EnableScheduling
public class ChatbotApplication {

	
	
	public static void main(String[] args) throws GeneralSecurityException, IOException {
		SpringApplication.run(ChatbotApplication.class, args);
		/*
		 * SheetsQuickstart sq = new SheetsQuickstart(); sq.getsheetdata();
		 */
	}

}
