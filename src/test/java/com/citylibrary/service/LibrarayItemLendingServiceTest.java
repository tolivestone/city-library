package com.citylibrary.service;

import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Customer;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class LibrarayItemLendingServiceTest {

    @Mock
    DataService csvDataService;

    @InjectMocks
    LibraryItemLendingService librarayItemLendingService;
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
    public void borrowItem_happyPath() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        CSVDataService mockCSVDataService = mock(CSVDataService.class);
       // LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book = new LibraryItem.LibraryItemBuilder(1,1,ItemType.BOOK,"Test Book").build();
        Person customer1 = new Customer(1,"Customer-1", "Customer Last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(7);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation){
                return null;
            }
        }).when(mockCSVDataService).addLoan(customer1, book, today, dueDate);

        //when(mockCSVDataService.addLoan(customer1, book, today, dueDate)).thenReturn(void);

        Assertions
                .assertThat(librarayItemLendingService.borrowItem(customer1, book, today, dueDate))
                .isEqualTo(true);

        verify(csvDataService, atMost(1))
                .addLoan(customer1, book, today, dueDate);
    }*/

    @Test
    public void borrowItem_withNullParameters() {
        //CSVDataService mockCSVDataService = mock(CSVDataService.class);
        //LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        Assertions
                .assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        ()-> librarayItemLendingService
                                .borrowItem(null, null, null, null));

        verify(csvDataService, atMost(1))
                .addLoan(null, null, null, null);
    }

    @Test
    public void returnItem_happyPath() throws LibraryOperationException {

        //CSVDataService mockCSVDataService = mock(CSVDataService.class);
        //LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book =  new LibraryItem.LibraryItemBuilder(1,1,ItemType.BOOK,"Test Book").build();
        Customer c= new Customer(1,"Test","Test");
        book.setItemStatus(Status.LOANED);
        List<Loan> l = List.of(new Loan(c,book,LocalDate.now(), LocalDate.now().plusDays(7)));

        when(csvDataService.getLoan()).thenReturn(l);
        when(csvDataService.returnLoanedItem(book)).thenReturn(true);

        Assertions.assertThat(librarayItemLendingService.returnItem(book))
                .isEqualTo(true);

        verify(csvDataService, atMost(1)).returnLoanedItem(book);
    }

    @Test
    public void returnItem_withNonBorrowedItem() throws LibraryOperationException {

        //CSVDataService mockCSVDataService = mock(CSVDataService.class);
        //LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book =  new LibraryItem.LibraryItemBuilder(1,1,ItemType.BOOK,"Test Book").build();

        when(csvDataService.returnLoanedItem(book)).thenReturn(false);

        Assertions.assertThatExceptionOfType(LibraryOperationException.class)
                .isThrownBy(()-> librarayItemLendingService.returnItem(book))
                 .withMessage("Cannot return Item 1 Test Book. It has not been loaned");


        verify(csvDataService, never()).returnLoanedItem(book);
    }

    @Test
    public void returnItem_withNullParameters() {

       // CSVDataService mockCSVDataService = mock(CSVDataService.class);
        //LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> librarayItemLendingService.returnItem(null));

        verify(csvDataService, never()).returnLoanedItem(null);
    }
}