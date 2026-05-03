package com.offgrid.OffGrid;

import com.offgrid.OffGrid.config.MeshPulseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(MeshPulseProperties.class)
public class OffGridApplication {

    public static void main(String[] args) {
        SpringApplication.run(OffGridApplication.class, args);
    }
}
