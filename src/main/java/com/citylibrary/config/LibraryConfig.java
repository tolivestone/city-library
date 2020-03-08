package com.citylibrary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "library")
public class LibraryConfig {

    private String customerFileName;
    private String libraryItemFileName;

    public String getCustomerFileName() {
        return customerFileName;
    }

    public void setCustomerFileName(final String customerFileName) {
        this.customerFileName = customerFileName;
    }

    public String getLibraryItemFileName() {
        return libraryItemFileName;
    }

    public void setLibraryItemFileName(final String libraryItemFileName) {
        this.libraryItemFileName = libraryItemFileName;
    }
}
