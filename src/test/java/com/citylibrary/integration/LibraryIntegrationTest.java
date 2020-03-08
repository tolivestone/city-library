package com.citylibrary.integration;

import com.citylibrary.Library;
import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import com.citylibrary.service.DataService;
import com.citylibrary.service.LendingService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// These are E2E integration tests, Not using mock but real dependencies

@SpringBootTest
public class LibraryIntegrationTest {

    @Autowired
    DataService dataService;

    @Autowired
    LendingService lendingService;

    @Autowired
    Library library;

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
        List<LibraryItem> inventory = library.getCurrentInventory();

        //Then
        assertThat(inventory)
                .isNotEmpty()
                .hasSize(12);
    }

    @Test
    public void canGetCurrentInventoryWhenLibraryHasNoItems() {

        //Given
        dataService.clearDataStore();

        //When
        List<LibraryItem> inventory = library.getCurrentInventory();

        //Then
        assertThat(inventory)
                .isEmpty();
    }

    @Test
    public void canBorrowBook() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowBook = library.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = library.borrowItem(customer,borrowBook);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

    @Test
    public void canBorrowDvd() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowDvd = library.getItemByTitleAndType("Pi", ItemType.DVD);

        //When
        boolean ret = library.borrowItem(customer,borrowDvd);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowDvd)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

    @Test
    public void canBorrowVhs() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowVhs = library.getItemByTitleAndType("Hackers", ItemType.VHS);

        //When
        boolean ret = library.borrowItem(customer,borrowVhs);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowVhs)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

    @Test
    public void canNotBorrowAlreadyBorrowedBook() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        //Given
        LibraryItem borrowBook = library.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = library.borrowItem(customer,borrowBook);

        //Then
        Assertions.assertThatExceptionOfType(LibraryItemNotLoanableException.class)
                .isThrownBy(()-> library.borrowItem(customer,borrowBook));
    }

     @Test
    public void canReturnBorrowedItem() throws LibraryOperationException {

        //Given
         LibraryItem borrowBook = library.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);
         library.borrowItem(customer,borrowBook);

         //When
         boolean ret = library.returnItem(borrowBook);

         //Then
         assertThat(ret)
                 .isTrue();

         assertThat(borrowBook)
                 .extracting(LibraryItem::getItemStatus)
                 .as(Status.AVAILABLE.toString());
    }

    @Test
    public void canNotReturnUnBorrowedItem() throws LibraryOperationException {

        //Given
        LibraryItem borrowBook = library.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //Then
        Assertions.assertThatExceptionOfType(LibraryOperationException.class)
                .isThrownBy(()-> library.returnItem(borrowBook));

        assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.AVAILABLE.toString());
    }

    @Test
    public void canGetOverDueItems() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        LibraryItem borrowBook1 = library.getItemByLibraryId(1);
        LibraryItem borrowBook2 = library.getItemByLibraryId(2);

        library.borrowItem(customer,borrowBook1);
        library.borrowItem(customer,borrowBook2);

        // Make any one item overdue
        //dataService.getLoan().stream().findAny().ifPresent(item->item.setDueDate(LocalDate.now().plusDays(-3)));

        assertThat(library.getOverDueItems())
                .isEmpty();

    }

    @Test
    public void canGetItemsBorrowedByGivenUser() throws LibraryItemNotLoanableException, LibraryItemNotFoundException {

        //Given
        dataService.reloadDataStore();
        LibraryItem borrowSoftwareBook = library.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);
        LibraryItem borrowJavaBook = library.getItemByTitleAndType("Java Concurrency In Practice", ItemType.BOOK);
        LibraryItem borrowHackersVhs = library.getItemByTitleAndType("Hackers", ItemType.VHS);

        library.borrowItem(customer,borrowSoftwareBook);
        library.borrowItem(customer,borrowJavaBook);
        library.borrowItem(customer,borrowHackersVhs);

        //When
        List<Loan> loans = library.getItemBorrowedByUser(customer);

        //Then
        assertThat(loans)
                .isNotEmpty()
                .hasSize(3)
                .flatExtracting(Loan::getCustomer)
                .allMatch(b->b.equals(customer));
    }

    @Test
    public void canCheckBookAvailable() {
        //Given
        LibraryItem book = library.getItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean available = library.isBookAvailable(book);

        //Then
        assertThat(available)
                .isTrue();
    }
}