package com.citylibrary.integration;

import com.citylibrary.Library;
import com.citylibrary.db.CSVLibraryDataStore;
import com.citylibrary.db.DataStore;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import com.citylibrary.service.CSVDataService;
import com.citylibrary.service.DataService;
import com.citylibrary.service.LendingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Autowired
    DataStore dataStore;

    @BeforeEach
    public void setUp() {
    /*    dataStore = new CSVLibraryDataStore();
        dataService = new CSVDataService(dataStore);
        dataService.reloadDataStore();
        lendingService = new LibrarayItemLendingService(dataService);
        library = new Library();*/
        //library = new Library(dataService,lendingService);
        customer = dataStore.getCustomers().get(0);
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
    public void canBorrowBook() {
        //Given
        LibraryItem borrowBook = library.findItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = library.borrowItem(customer,borrowBook);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

    @Test
    public void canBorrowDvd() {
        //Given
        LibraryItem borrowDvd = library.findItemByTitleAndType("Pi", ItemType.DVD);

        //When
        boolean ret = library.borrowItem(customer,borrowDvd);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowDvd)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

    @Test
    public void canBorrowVhs() {
        //Given
        LibraryItem borrowVhs = library.findItemByTitleAndType("Hackers", ItemType.VHS);

        //When
        boolean ret = library.borrowItem(customer,borrowVhs);

        //Then
        assertThat(ret).isTrue();
        assertThat(borrowVhs)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

     @Test
    public void canReturnBorrowedItem() {

        //Given
         LibraryItem borrowBook = library.findItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);
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
    public void canNotReturnUnBorrowedItem() {

        //Given
        LibraryItem borrowBook = library.findItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = library.returnItem(borrowBook);

        //Then
        assertThat(ret)
                .isFalse();

        assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.AVAILABLE.toString());
    }

    @Disabled
    @Test
    public void getOverDueItems() {
        LibraryItem borrowBook1 = library.getCurrentInventory().get(0);
        LibraryItem borrowBook2 = library.getCurrentInventory().get(1);

        library.borrowItem(customer,borrowBook1);
        library.borrowItem(customer,borrowBook2);

        // Make any one item overdue
        //dataService.getLoan().stream().findAny().ifPresent(item->item.setDueDate(LocalDate.now().plusDays(-3)));

        assertThat(library.getOverDueItems())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    public void canGetItemsBorrowedByGivenUser() {

        //Given
        dataService.reloadDataStore();
        LibraryItem borrowSoftwareBook = library.findItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);
        LibraryItem borrowJavaBook = library.findItemByTitleAndType("Java Concurrency In Practice", ItemType.BOOK);
        LibraryItem borrowHackersVhs = library.findItemByTitleAndType("Hackers", ItemType.VHS);

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
    public void isBookAvailable() {
        //Given
        LibraryItem book = library.findItemByTitleAndType("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean available = library.isBookAvailable(book);

        //Then
        assertThat(available)
                .isTrue();
    }
}