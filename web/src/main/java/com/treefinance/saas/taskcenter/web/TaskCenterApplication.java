package com.treefinance.saas.taskcenter.web;

import com.treefinance.saas.assistant.annotation.EnableMonitorPlugin;
import com.treefinance.saas.assistant.variable.notify.annotation.EnableVariableNotifyListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan("com.treefinance.saas.taskcenter.web")
@ImportResource("classpath:spring/applicationContext.xml")
@EnableAsync
@EnableMonitorPlugin
@EnableVariableNotifyListener
public class TaskCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskCenterApplication.class);
    }
}
