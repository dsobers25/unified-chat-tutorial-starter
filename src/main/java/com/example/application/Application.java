package com.example.application;

// tag::snippet[]
import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;

@SpringBootApplication
// tag::snippet[]
@Push 
@Theme("chat-theme")
public class Application implements AppShellConfigurator { 
// end::snippet[]

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}