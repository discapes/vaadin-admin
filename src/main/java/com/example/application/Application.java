package com.example.application;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@Theme(value = "flowcrmtutorial")
// components-folder injection disabled in V24
@CssImport(value = "./themes/flowcrmtutorial/components/vaadin-grid.css", themeFor = "vaadin-grid")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        ConfigurableEnvironment env = ctx.getEnvironment();
//        Logger logger = LoggerFactory.getLogger(Application.class);
//        try {
        APP_NAME = env.getRequiredProperty("application.title");
//        } catch (Exception e) {
//            logger.error("Error: ", e);
//            System.exit(1);
//        }
    }

    public static String APP_NAME;
}
