package com.citylibrary;

import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Customer;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import com.citylibrary.service.CSVDataService;
import com.citylibrary.service.DataService;
import com.citylibrary.service.LendingService;
import com.citylibrary.service.LibraryItemLendingService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.citylibrary.constant.Constant.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LibraryTest {

    private DataService mockDataService;
    private LendingService mockLendingService;
    private Library library;
    private List<LibraryItem> items;
    private List<Loan> loans;
    private Customer customerOne;

    @BeforeEach
    public void setUp() {

        //Mocking dependent services
        mockDataService = mock(CSVDataService.class);
        mockLendingService = mock(LibraryItemLendingService.class);
        library = new Library();
        //library = new Library(mockDataService,mockLendingService);

        items = List.of(
                new LibraryItem.LibraryItemBuilder(1,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(2,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(3,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(4,2, ItemType.DVD, "Pi").build(),
                new LibraryItem.LibraryItemBuilder(5,3, ItemType.CD, "Frozen").build()
        );

        customerOne = new Customer(1,"Customer-One", "Customer's Last Name");

        loans = List.of(
                new Loan(customerOne,items.get(0),LocalDate.now(),LocalDate.now().plusDays(-2)),
                new Loan(customerOne,items.get(1),LocalDate.now(),LocalDate.now().plusDays(-3)),
                new Loan(customerOne,items.get(2),LocalDate.now(),LocalDate.now().plusDays(7))
        );


    }

    @Test
    public void getCurrentInventory() {

        //Given
        when(mockDataService.getCurrentInventory()).thenReturn(items);

        //When
        List<LibraryItem> currentInventory = library.getCurrentInventory();

        //Then
        Assertions.assertThat(currentInventory)                     // checks list is not empty, has size 5
                .isNotEmpty()
                .hasSize(FIVE);

        verify(mockDataService, atMost(ONE)).getCurrentInventory(); // verifies DataService getCurrentInventory was called once
    }

    @Test
    public void canGetEmptyCurrentInventory() {

        //Given
        when(mockDataService.getCurrentInventory()).thenReturn(Collections.emptyList());

        //When
        List<LibraryItem> currentInventory = library.getCurrentInventory();

        //Then
        Assertions.assertThat(currentInventory)                     // checks list is empty, has size 5
                .isEmpty();

        verify(mockDataService, atMost(ONE)).getCurrentInventory(); // verifies DataService getCurrentInventory was called once
    }

    //TODO: Mokito expectation
    @Test
    public void canborrowAvailableItem() {

        //Given
        LibraryItem item = items.get(0);
        Person customer = new Customer(1,"Customer 1","Customer's last name");
        Person customer1 = new Customer(2,"Customer 2","Customer's last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(LOAN_PERIOD);
        when(mockLendingService.borrowItem(customer,item, today, dueDate)).thenReturn(true);

        //When
        boolean success = library.borrowItem(customer,item);

        //Then
        Assertions.assertThat(success)                              // checks operation was successful
                .isTrue();

        verify(mockLendingService, atMost(ONE))
                .borrowItem(customer1,item, today, dueDate);         // verifies DataService borrowItem was called once
    }

    @Test
    public void canborrowUnvailableItem() {

        //Given
        LibraryItem frozenCD  = new LibraryItem.LibraryItemBuilder(1,2,ItemType.CD,"Frozen").build();
        frozenCD.setItemStatus(Status.LOANED);

        Person customer = new Customer(1,"Customer 1","Customer's last name");

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(LOAN_PERIOD);
        when(mockLendingService.borrowItem(customer,frozenCD, today, dueDate)).thenReturn(false);

        //When
        boolean success = library.borrowItem(customer,frozenCD);

        //Then
        Assertions.assertThat(success)                              // checks operation was successful
                .isFalse();

        verify(mockLendingService, atMost(ONE))
                .borrowItem(customer,frozenCD, today, dueDate);         // verifies DataService borrowItem was called once
    }

    @Test
    public void returnItem() {

        //Given
        LibraryItem item = items.get(0);
        when(mockLendingService.returnItem(item)).thenReturn(true);

        //When
        boolean success = library.returnItem(item);

        //Then
        Assertions.assertThat(success)                              // checks operation was successful
                .isTrue();

        verify(mockLendingService, atMost(ONE))                     // verifies DataService borrowItem was called once
                .returnItem(item);
    }

    @Test
    public void getOverDueItems() {

        //Given
        when(mockDataService.getLoan()).thenReturn(loans);

        //When
        List<Loan> overDueItems = library.getOverDueItems();

        //Then
        Assertions.assertThat(overDueItems)
                .isNotEmpty()
                .hasSize(TWO);

        verify(mockDataService, atMost(ONE)).getLoan();
    }

    @Test
    public void getItemBorrowedByUser() {

        //Given
        when(mockDataService.getLoan()).thenReturn(loans);

        //When
        List<Loan> borrowedItems = library.getItemBorrowedByUser(customerOne);

        //Then
        Assertions.assertThat(borrowedItems)
                .isNotEmpty()
                .hasSize(THREE);

        verify(mockDataService, atMost(ONE))
                .getLoan();
    }

    @Test
    public void isBookAvailable() {

        //Given
        when(mockDataService.getCurrentLoanableInventory()).thenReturn(items);
        LibraryItem availableBook = items.get(0);

        //When
        boolean isBookAvailable  = library.isBookAvailable(availableBook);


        //Then
        Assertions.assertThat(isBookAvailable)
                .isTrue();

        verify(mockDataService, atMost(ONE))
                .getCurrentLoanableInventory();

    }

}