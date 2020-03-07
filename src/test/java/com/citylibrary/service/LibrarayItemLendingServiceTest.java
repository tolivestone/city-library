package com.citylibrary.service;

import org.assertj.core.api.Assertions;
import com.citylibrary.db.DataStore;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Customer;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class LibrarayItemLendingServiceTest {
/*
    @Test
    @Disabled
    public void borrowItem_happyPath_1() {
        DataStore mockDataStore = mock(DataStore.class);
        CSVDataService mockCSVDataService = new CSVDataService(mockDataStore);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book = new LibraryItem.LibraryItemBuilder(1,1, ItemType.BOOK,"Test Book").build();
        Person customer1 = new Customer(1,"Customer-1", "Customer Last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(7);

        when(mockCSVDataService.addLoan(customer1, book, today, dueDate)).thenReturn(true);

        Assertions
                .assertThat(librarayItemLendingService.borrowItem(customer1, book, today, dueDate))
                .isEqualTo(true);

        verify(mockCSVDataService, atMost(1))
                .addLoan(customer1, book, today, dueDate);
    }


    @Test
    public void borrowItem_happyPath() {
        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book = new LibraryItem.LibraryItemBuilder(1,1,ItemType.BOOK,"Test Book").build();
        Person customer1 = new Customer(1,"Customer-1", "Customer Last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(7);

        when(mockCSVDataService.addLoan(customer1, book, today, dueDate)).thenReturn(true);

        Assertions
                .assertThat(librarayItemLendingService.borrowItem(customer1, book, today, dueDate))
                .isEqualTo(true);

        verify(mockCSVDataService, atMost(1))
                .addLoan(customer1, book, today, dueDate);
    }

    @Test
    public void borrowItem_withNullParameters() {
        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        Assertions
                .assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        ()-> librarayItemLendingService
                                .borrowItem(null, null, null, null));

        verify(mockCSVDataService, atMost(1))
                .addLoan(null, null, null, null);
    }

    @Test
    public void returnItem_happyPath() {

        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book =  new LibraryItem.LibraryItemBuilder(1,1,ItemType.BOOK,"Test Book").build();
        book.setItemStatus(Status.LOANED);

        when(mockCSVDataService.returnLoanedItem(book)).thenReturn(true);

        Assertions.assertThat(librarayItemLendingService.returnItem(book))
                .isEqualTo(true);

        verify(mockCSVDataService, atMost(1)).returnLoanedItem(book);
    }

    @Test
    public void returnItem_withNonBorrowedItem() {

        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book =  new LibraryItem.LibraryItemBuilder(1,1,ItemType.BOOK,"Test Book").build();

        when(mockCSVDataService.returnLoanedItem(book)).thenReturn(false);

        Assertions.assertThat(librarayItemLendingService.returnItem(book))
                .isEqualTo(false);

        verify(mockCSVDataService, never()).returnLoanedItem(book);
    }

    @Test
    public void returnItem_withNullParameters() {

        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> librarayItemLendingService.returnItem(null));

        verify(mockCSVDataService, never()).isBorrowed(null);
        verify(mockCSVDataService, never()).returnLoanedItem(null);
    }*/
}