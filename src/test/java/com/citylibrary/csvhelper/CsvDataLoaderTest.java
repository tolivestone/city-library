package com.citylibrary.csvhelper;

import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvDataLoaderTest {

    @Autowired
    private CsvDataLoader csvReader;

    @Test
    void canGetLibraryItemsFromCsv() {

        //Given

        //When
        List<LibraryItem> libraryItemList = csvReader.getLibraryItemsFromCsv();

        //Then
        Assertions.assertThat(libraryItemList)
                .isNotEmpty()
                .hasSize(12);
    }

    @Test
    void getCustomersFromCsv() {

        //Given

        //When
        List<Person> customerList = csvReader.getCustomersFromCsv();

        //Then
        Assertions.assertThat(customerList)
                .isNotEmpty()
                .hasSize(3);
    }
}