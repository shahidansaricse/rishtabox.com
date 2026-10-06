package com.rishtabox.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${rishtabox.slider.upload-dir:./uploads/sliders}")
    private String sliderUploadDir;


    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        Path sliderPath = Paths
                .get(sliderUploadDir)
                .toAbsolutePath()
                .normalize();


        System.out.println(
                "========================================"
        );

        System.out.println(
                "SLIDER DIRECTORY:"
        );

        System.out.println(
                sliderPath
        );

        System.out.println(
                "SLIDER DIRECTORY EXISTS: "
                        + sliderPath.toFile().exists()
        );

        System.out.println(
                "========================================"
        );


        registry
                .addResourceHandler(
                        "/uploads/sliders/**"
                )
                .addResourceLocations(
                        sliderPath.toUri().toString()
                );
    }
}