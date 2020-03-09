package com.citylibrary.integration;

import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.constant.TestConstants;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.manager.LibraryManager;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import com.citylibrary.service.DataService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.citylibrary.constant.TestConstants.SIZE_THREE;
import static org.assertj.core.api.Assertions.assertThat;

// These are E2E integration tests, Not using mock but real dependencies

@SpringBootTest
public class LibraryIntegrationTest {

    @Autowired
    DataService dataService;

    @Autowired
    LibraryManager libraryManager;

    Person customer;

    @BeforeEach
    public void setUp() {
        dataService.reloadDataStore();
        customer = dataService.getCustomerById(1);
    }

    @Test
    public void getCurrentInventory() {

        //Given
        dataService.reloadDataStore();

        //When
        List<LibraryItem> inventory = libraryManager.getCurrentInventory();

        //Then
        assertThat(inventory)
                .isNotEmpty()
                .hasSize(TestConstants.SIZE_TWELVE);
    }

    @Test
    public void canGetCurrentInventoryWhenLibraryHasNoItems() {

        //Given
        dataService.clearDataStore();

        //When
        List<LibraryItem> inventory = libraryManager.getCurrentInventory();

        //Then
        assertThat(inventory)
                .isEmpty();
    }

    @Test
    public void canBorrowBook() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowBook = libraryManager.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = libraryManager.borrowItem(customer, borrowBook);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .isEqualTo(Status.LOANED);
    }

    @Test
    public void canBorrowDvd() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowDvd = libraryManager.getItemByTitleAndType("Pi", ItemType.DVD);

        //When
        boolean ret = libraryManager.borrowItem(customer, borrowDvd);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowDvd)
                .extracting(LibraryItem::getItemStatus)
                .isEqualTo(Status.LOANED);
    }

    @Test
    public void canBorrowVhs() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowVhs = libraryManager.getItemByTitleAndType("Hackers", ItemType.VHS);

        //When
        boolean ret = libraryManager.borrowItem(customer, borrowVhs);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowVhs)
                .extracting(LibraryItem::getItemStatus)
                .isEqualTo(Status.LOANED);
    }

    @Test
    public void canNotBorrowAlreadyBorrowedBook() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowBook = libraryManager.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = libraryManager.borrowItem(customer, borrowBook);

        //Then
        Assertions.assertThatExceptionOfType(LibraryItemNotLoanableException.class)
                .isThrownBy(() -> libraryManager.borrowItem(customer, borrowBook));
    }

    @Test
    public void canReturnBorrowedItem() throws LibraryOperationException {

        //Given
        LibraryItem borrowBook = libraryManager.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);
        libraryManager.borrowItem(customer, borrowBook);

        //When
        boolean ret = libraryManager.returnItem(borrowBook);

        //Then
        assertThat(ret)
                .isTrue();

        assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .isEqualTo(Status.AVAILABLE);
    }

    @Test
    public void canNotReturnUnBorrowedItem() throws LibraryItemNotFoundException {

        //Given
        LibraryItem borrowBook = libraryManager.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //Then
        Assertions.assertThatExceptionOfType(LibraryOperationException.class)
                .isThrownBy(() -> libraryManager.returnItem(borrowBook));

        //When
        assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .isEqualTo(Status.AVAILABLE);
    }

    @Test
    public void canGetOverDueItems() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowBook1 = libraryManager.getItemByLibraryId(1);
        LibraryItem borrowBook2 = libraryManager.getItemByLibraryId(2);

        //When
        libraryManager.borrowItem(customer, borrowBook1);
        libraryManager.borrowItem(customer, borrowBook2);

        //Then
        assertThat(libraryManager.getOverDueItems())
                .isEmpty();
    }

    @Test
    public void canGetItemsBorrowedByGivenUser() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {

        //Given
        dataService.reloadDataStore();
        LibraryItem borrowSoftwareBook = libraryManager.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);
        LibraryItem borrowJavaBook = libraryManager.getItemByTitleAndType("Java Concurrency In Practice", ItemType.BOOK);
        LibraryItem borrowHackersVhs = libraryManager.getItemByTitleAndType("Hackers", ItemType.VHS);

        libraryManager.borrowItem(customer, borrowSoftwareBook);
        libraryManager.borrowItem(customer, borrowJavaBook);
        libraryManager.borrowItem(customer, borrowHackersVhs);

        //When
        List<Loan> loans = libraryManager.getItemBorrowedByUser(customer);

        //Then
        assertThat(loans)
                .isNotEmpty()
                .hasSize(SIZE_THREE)
                .flatExtracting(Loan::getCustomer)
                .allMatch(c -> c.equals(customer));
    }

    @Test
    public void canCheckBookAvailable() throws LibraryItemNotFoundException {
        //Given
        LibraryItem book = libraryManager.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean available = libraryManager.isBookAvailable(book);

        //Then
        assertThat(available)
                .isTrue();
    }
}