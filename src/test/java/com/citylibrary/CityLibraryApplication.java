package com.citylibrary;

import com.citylibrary.manager.LibraryManager;
import com.citylibrary.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CityLibraryApplication {

    @Autowired
    private LibraryManager libraryManager;
    @Autowired
    DataService csvDataService;

    private static final Logger logger = LoggerFactory.getLogger(CityLibraryApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CityLibraryApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}



