package com.citylibrary.csvhelper;

import com.citylibrary.csvhelper.LibrarItemCsvReader;
import com.citylibrary.model.item.LibraryItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
@SpringBootTest
public class LibrarItemCsvReaderTest {

    @Autowired
    private LibrarItemCsvReader csvReader;
    @Test
    public void getLibraryItemsFromCsv() {
        List<LibraryItem> libraryItemList = csvReader.getLibraryItemsFromCsv();

        Assertions.assertThat(libraryItemList)
                .isNotEmpty()
                .hasSize(12);
    }
}