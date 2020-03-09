package com.citylibrary;

import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.manager.LibraryManager;
import com.citylibrary.model.actor.Customer;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import com.citylibrary.service.DataService;
import com.citylibrary.service.LendingService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.citylibrary.constant.Constant.LOAN_PERIOD;
import static com.citylibrary.constant.TestConstants.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LibraryManagerTest {

    @Mock
    private DataService mockDataService;
    @Mock
    private LendingService mockLendingService;
    @InjectMocks
    private LibraryManager libraryManager;

    private List<LibraryItem> items;
    private List<Loan> loans;
    private Customer customerOne;

    @BeforeEach
    public void setUp() {

        items = List.of(
                new LibraryItem.LibraryItemBuilder(1, 1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(2, 1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(3, 1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(4, 2, ItemType.DVD, "Pi").build(),
                new LibraryItem.LibraryItemBuilder(5, 3, ItemType.CD, "Frozen").build()
        );

        customerOne = new Customer(1, "Customer-One", "Customer's Last Name");

        loans = List.of(
                new Loan(customerOne, items.get(0), LocalDate.now(), LocalDate.now().plusDays(-2)),
                new Loan(customerOne, items.get(1), LocalDate.now(), LocalDate.now().plusDays(-3)),
                new Loan(customerOne, items.get(2), LocalDate.now(), LocalDate.now().plusDays(7))
        );


    }

    @Test
    public void canGetCurrentInventory() {

        //Given
        when(mockDataService.getCurrentInventory()).thenReturn(items);

        //When
        List<LibraryItem> currentInventory = libraryManager.getCurrentInventory();

        //Then
        Assertions.assertThat(currentInventory)
                .isNotEmpty()
                .hasSize(SIZE_FIVE);

        verify(mockDataService, atMost(INVOKED_ONCE)).getCurrentInventory();
    }


    @Test
    public void canGetCurrentLoanableInventory() {

        //Given
        when(mockDataService.getCurrentLoanableInventory()).thenReturn(items);

        //When
        List<LibraryItem> currentInventory = libraryManager.getCurrentLoanableInventory();

        //Then
        Assertions.assertThat(currentInventory)
                .isNotEmpty()
                .hasSize(SIZE_FIVE)
                .allMatch(item -> item.getItemStatus().equals(Status.AVAILABLE));

        verify(mockDataService, atMost(INVOKED_ONCE)).getCurrentLoanableInventory();
    }

    @Test
    public void canGetEmptyCurrentInventory() {

        //Given
        when(mockDataService.getCurrentInventory()).thenReturn(Collections.emptyList());

        //When
        List<LibraryItem> currentInventory = libraryManager.getCurrentInventory();

        //Then
        Assertions.assertThat(currentInventory)
                .isEmpty();

        verify(mockDataService, atMost(INVOKED_ONCE)).getCurrentInventory();
    }

    @Test
    public void canBorrowAvailableItem() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {

        //Given
        LibraryItem item = items.get(0);
        Person customer = new Customer(1, "Customer 1", "Customer's last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(LOAN_PERIOD);
        when(mockLendingService.borrowItem(customer, item, today, dueDate)).thenReturn(true);

        //When
        boolean success = libraryManager.borrowItem(customer, item);

        //Then
        Assertions.assertThat(success)
                .isTrue();

        verify(mockLendingService, atMost(INVOKED_ONCE))
                .borrowItem(customer, item, today, dueDate);
    }

    @Test
    public void cannotBorrowUnvailableItem() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {

        //Given
        LibraryItem frozenCD = new LibraryItem.LibraryItemBuilder(1, 2, ItemType.CD, "Frozen").build();
        Person customer = new Customer(1, "Customer 1", "Customer's last name");

        frozenCD.setItemStatus(Status.LOANED);
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(LOAN_PERIOD);

        when(mockLendingService.borrowItem(customer, frozenCD, today, dueDate)).thenReturn(false);

        //When
        boolean success = libraryManager.borrowItem(customer, frozenCD);

        //Then
        Assertions.assertThat(success)
                .isFalse();

        verify(mockLendingService, atMost(INVOKED_ONCE))
                .borrowItem(customer, frozenCD, today, dueDate);
    }

    @Test
    public void canReturnItem() throws LibraryOperationException {

        //Given
        LibraryItem item = items.get(0);
        when(mockLendingService.returnItem(item)).thenReturn(true);

        //When
        boolean success = libraryManager.returnItem(item);                         // checks success flag for true

        //Then
        Assertions.assertThat(success)
                .isTrue();

        verify(mockLendingService, atMost(INVOKED_ONCE))                     // verifies DataService borrowItem was called once
                .returnItem(item);
    }

    @Test
    public void getOverDueItems() {

        //Given
        when(mockDataService.getLoan()).thenReturn(loans);

        //When
        List<Loan> overDueItems = libraryManager.getOverDueItems();

        //Then
        Assertions.assertThat(overDueItems)
                .isNotEmpty()
                .hasSize(SIZE_TWO)
                .allMatch(loan -> loan.getDueDate().isBefore(LocalDate.now()));         //checks due date is in the past

        verify(mockDataService, atMost(INVOKED_ONCE)).getLoan();
    }

    @Test
    public void canGetItemBorrowedByUser() {

        //Given
        when(mockDataService.getLoan()).thenReturn(loans);

        //When
        List<Loan> borrowedItems = libraryManager.getItemBorrowedByUser(customerOne);

        //Then
        Assertions.assertThat(borrowedItems)
                .isNotEmpty()
                .hasSize(SIZE_THREE)
                .allMatch(loan -> loan.getCustomer().equals(customerOne));

        verify(mockDataService, atMost(INVOKED_ONCE))
                .getLoan();
    }

    @Test
    public void canCheckBookAvailablity() {

        //Given
        when(mockDataService.getCurrentLoanableInventory()).thenReturn(items);
        LibraryItem availableBook = items.get(0);

        //When
        boolean isBookAvailable = libraryManager.isBookAvailable(availableBook);


        //Then
        Assertions.assertThat(isBookAvailable)
                .isTrue();

        verify(mockDataService, atMost(INVOKED_ONCE))
                .getCurrentLoanableInventory();

    }

}