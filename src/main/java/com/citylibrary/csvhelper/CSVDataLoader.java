package com.citylibrary.csvhelper;

import com.citylibrary.CityLibraryApplication;
import com.citylibrary.config.LibraryConfig;
import com.citylibrary.enums.ItemType;
import com.citylibrary.model.actor.Customer;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.citylibrary.constant.Constant.*;

@Component
public class CSVDataLoader {

    private final LibraryConfig libraryConfig;

    // constructor injection gives an opportunity to mock and run unit tests outside spring framework
    @Autowired
    public CSVDataLoader(final LibraryConfig libraryConfig) {
        this.libraryConfig = libraryConfig;
    }

    private static final Logger logger
            = LoggerFactory.getLogger(CityLibraryApplication.class);

    public List<LibraryItem> getLibraryItemsFromCsv() {

        InputStream datafile = getClass().getClassLoader().getResourceAsStream(libraryConfig.getLibraryItemFileName());

        CSVReader csvReader;
        List<LibraryItem> libraryItems = new ArrayList<>();

        try {
            csvReader = new CSVReaderBuilder(new InputStreamReader(datafile))
                    .withSkipLines(SKIP_HEADER)
                    .build();

            List<String[]> records = csvReader.readAll();
            for (String[] record : records) {

                switch (record[2].toUpperCase()) {
                    case "BOOK":
                        LibraryItem book = new LibraryItem.LibraryItemBuilder(
                                Integer.parseInt(record[LIBRARY_ID]), Integer.parseInt(record[ITEM_ID]), ItemType.BOOK, record[ITEM_TITLE])
                                .withDescription("Description for " + record[ITEM_TITLE])
                                .build();

                        libraryItems.add(book);
                        break;

                    case "DVD":
                        LibraryItem dvd = new LibraryItem.LibraryItemBuilder(
                                Integer.parseInt(record[LIBRARY_ID]), Integer.parseInt(record[ITEM_ID]), ItemType.DVD, record[ITEM_TITLE])
                                .withDescription("Description for " + record[ITEM_TITLE])
                                .build();

                        libraryItems.add(dvd);
                        break;

                    case "VHS":
                        LibraryItem vhs = new LibraryItem.LibraryItemBuilder(
                                Integer.parseInt(record[LIBRARY_ID]), Integer.parseInt(record[ITEM_ID]), ItemType.VHS, record[ITEM_TITLE])
                                .withDescription("Description for " + record[ITEM_TITLE])
                                .build();

                        libraryItems.add(vhs);
                        break;
                }
            }
        } catch (IOException ex) {
            logger.error("Error loading library items from CSV " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unknown error occurred while loading library items from CSV " + ex.getMessage());
        }

        return libraryItems;
    }

    public List<Person> getCustomersFromCsv() {

        InputStream datafile = getClass().getClassLoader().getResourceAsStream(libraryConfig.getCustomerFileName());

        CSVReader csvReader;
        List<Person> customers = new ArrayList<>();

        try {
            csvReader = new CSVReaderBuilder(new InputStreamReader(datafile))
                    .withSkipLines(SKIP_HEADER)
                    .build();

            List<String[]> records = csvReader.readAll();
            for (String[] record : records) {
                Person customer = new Customer(
                        Integer.parseInt(record[CUSTOMER_ID]), record[CUSTOMER_FIRST_NAME], record[CUSTOMER_LAST_NAME]);

                customers.add(customer);
            }
        } catch (IOException ex) {
            logger.error("Error loading customer data from CSV " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unknown error occurred while customer data from CSV " + ex.getMessage());
        }
        return customers;
    }
}
