package com.treefinance.saas.taskcenter.web;

import com.treefinance.saas.assistant.annotation.EnableMonitorPlugin;
import com.treefinance.saas.assistant.variable.notify.annotation.EnableVariableNotifyListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.treefinance.saas.taskcenter", exclude = {DataSourceAutoConfiguration.class})
@ServletComponentScan("com.treefinance.saas.taskcenter.web")
@ImportResource("classpath:spring/applicationContext.xml")
@EnableScheduling
@EnableAsync
@EnableMonitorPlugin
@EnableVariableNotifyListener
public class TaskCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskCenterApplication.class);
    }
}
