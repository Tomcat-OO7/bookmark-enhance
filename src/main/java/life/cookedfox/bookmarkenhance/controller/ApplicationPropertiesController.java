package life.cookedfox.bookmarkenhance.controller;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class ApplicationPropertiesController {

    @Resource
    ApplicationContext applicationContext;

    @Value("${spring.config.location:classpath:application.properties}")
    private String propertiesFilePath;

    @PostConstruct
    public void after() {
        if (propertiesFilePath.startsWith("classpath:") || propertiesFilePath.startsWith("file:")) {
        } else {
            this.propertiesFilePath = "file:" + this.propertiesFilePath;
        }
    }

    @GetMapping("/properties")
    public String readProperties() throws IOException {
        return applicationContext.getResource(propertiesFilePath).getContentAsString(StandardCharsets.UTF_8);
    }

    @PostMapping("/properties")
    public void saveProperties(@RequestBody String propertiesContent) throws IOException {
        Files.write(Path.of(applicationContext.getResource(propertiesFilePath).getURI()), propertiesContent.getBytes());
    }
}