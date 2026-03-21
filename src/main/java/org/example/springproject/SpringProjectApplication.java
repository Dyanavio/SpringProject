package org.example.springproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// implementation 'org.springframework.boot:spring-boot-starter-aspectj' !!! в dependencies файла build.gradle
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
class SpringProjectApplication {
    static void main(String[] args) {
        SpringApplication.run(SpringProjectApplication.class, args);
    }
}

@Aspect
@Component
class StatsAspect
{
    private final HashMap<String, List<Long>> executions = new HashMap<>();
    private StopWatch stopwatch = new StopWatch();
    private final String FILE_NAME = "stats.txt";

    @Before("execution(* org.example.springproject.HelloController.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint)
    {
        String methodName = joinPoint.getSignature().getName();
        if(!executions.containsKey(methodName)) executions.put(methodName, new ArrayList<Long>());
        stopwatch = new StopWatch();
        stopwatch.start();

        //System.out.println("Заходимо в метод: " + joinPoint.getSignature().getName());
    }

    @After("execution(* org.example.springproject.HelloController.*(..))")
    public void logAfterMethod(JoinPoint joinPoint)
    {
        stopwatch.stop();
        long elapsedTime = stopwatch.getTotalTimeMillis();
        String methodName = joinPoint.getSignature().getName();

        List<Long> times = executions.get(methodName);
        times.add(elapsedTime);
        executions.replace(methodName, times);

        WriteIntoFile();

        System.out.println(methodName + " finished execution; total time elapsed: " + elapsedTime + "; total times executed: " + executions.get(methodName).size());
    }

    private void WriteIntoFile()
    {
        List<String> lines = new ArrayList<>();
        try
        {
            for(var entry : executions.entrySet())
            {
                lines.add("Method: " + entry.getKey());
                lines.add("\t> Total times called: " + entry.getValue().size());

                int count = 1;
                for(long elapsedTime : entry.getValue())
                {
                    lines.add("\t\t- " + (count++) + ") " + "Elapsed time: " + elapsedTime + " ms");
                }
            }

            Files.write(Paths.get(FILE_NAME), lines, StandardCharsets.UTF_8);
        }
        catch(Exception e)
        {
            System.out.println("Inner exception: " + e.getMessage());
        }
    }
}

@RestController
class HelloController {
    @GetMapping("/")
    public String greetings()
    {
        try
        {
            Thread.sleep(new Random().nextInt(1000, 10000));
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return "Hello Spring with AOP!";
    }

    @GetMapping("/sing")
    public String sing()
    {
        try
        {
            Thread.sleep(new Random().nextInt(1000, 10000));
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return "Never gonna give you up...";
    }
}


@Component
class BrowserLauncher {
    @EventListener(ApplicationReadyEvent.class)
    public void launchBrowser() {
        System.setProperty("java.awt.headless", "false"); // вимикаємо headless-режим
        var desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://localhost:8080")); // відкриваємо браузер
        } catch (IOException | URISyntaxException e) {
            // ігноруємо помилки, якщо браузер не вдалося відкрити
        }
    }
}