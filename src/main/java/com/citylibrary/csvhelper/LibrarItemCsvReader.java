package com.citylibrary.csvhelper;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.citylibrary.enums.ItemType;
import com.citylibrary.model.item.LibraryItem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LibrarItemCsvReader {

    private static LibrarItemCsvReader instance;

    public static LibrarItemCsvReader getInstance() {

        if(instance == null) {
            synchronized (LibrarItemCsvReader.class) {
                if(instance == null) {
                    instance = new LibrarItemCsvReader();
                }
            }
        }
        return instance;
    }

    public  LibrarItemCsvReader(){}

    public List<LibraryItem> getLibraryItemsFromCsv(){

        File datafile = new File(
                getClass().getClassLoader().getResource("data/library.csv").getFile()
        );

        CSVReader csvReader;
        List<LibraryItem> libraryItems = new ArrayList<>();

        try{
            csvReader = new CSVReaderBuilder(new FileReader(datafile))
                    .withSkipLines(1)
                    .build();

            List<String[]> records = csvReader.readAll();
            for(String[] record: records) {

                switch (record[2].toUpperCase()) {
                    case "BOOK":
                        LibraryItem book =
                                new LibraryItem.LibraryItemBuilder(Integer.parseInt(record[0]), Integer.parseInt(record[1]),ItemType.BOOK,record[3])
                                .withDescription("Description for " + record[3])
                                .build();

                        libraryItems.add(book);
                        break;

                    case "DVD":
                        LibraryItem dvd =
                                new LibraryItem.LibraryItemBuilder(Integer.parseInt(record[0]), Integer.parseInt(record[1]),ItemType.DVD,record[3])
                                .withDescription("Description for " + record[3])
                                .build();

                        libraryItems.add(dvd);
                        break;

                    case "VHS":
                        LibraryItem vhs =
                                new LibraryItem.LibraryItemBuilder(Integer.parseInt(record[0]), Integer.parseInt(record[1]),ItemType.VHS,record[3])
                                .withDescription("Description for " + record[3])
                                .build();

                        libraryItems.add(vhs);
                        break;
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return libraryItems;
    }



}
