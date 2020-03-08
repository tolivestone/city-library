package com.citylibrary.service;

import com.citylibrary.db.DataStore;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Customer;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.citylibrary.constant.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CSVDataServiceTest {

    @Mock
    DataStore dataStore;

    @InjectMocks
    CSVDataService libraryCsvDataService;

    private final Object PRESENT = new Object();

    @Test
    public void canGetCurrentInventory() {

        //Given
        ConcurrentMap<Integer, LibraryItem> items = getLibraryItemMap();
        when(dataStore.getLibraryItems()).thenReturn(items);

        //When
        List<LibraryItem> returnedItems = libraryCsvDataService.getCurrentInventory();

        //Then
        assertThat(returnedItems)
                .isNotEmpty()
                .hasSize(SIZE_FIVE)
                .doesNotContainNull()
                .allMatch(d -> d.getItemStatus() == Status.AVAILABLE);
    }

    @Test
    public void canGetCurrentLoanableInventory() {

        //Given
        ConcurrentMap<Integer, LibraryItem> items = getLibraryItemMap();
        items.get(1).setItemStatus(Status.LOANED);
        items.get(3).setItemStatus(Status.LOANED);

        when(dataStore.getLibraryItems()).thenReturn(items);

        //When
        List<LibraryItem> returnedItems = libraryCsvDataService.getCurrentLoanableInventory();

        //Then
        assertThat(returnedItems)
                .isNotEmpty()
                .hasSize(SIZE_THREE)
                .extracting(LibraryItem::getItemStatus)
                .doesNotContain(Status.LOANED)
                .allMatch(d -> d.equals(Status.AVAILABLE));
    }

    @Test
    public void canSearchItemsByTitle() {

        //Given
        ConcurrentMap<Integer, LibraryItem> items = getLibraryItemMap();
        when(dataStore.getLibraryItems()).thenReturn(items);

        //When
        List<LibraryItem> returnedItems = libraryCsvDataService.getItemsByTitle("Introduction to Algorithms");

        //Then
        assertThat(returnedItems)
                .isNotEmpty()
                .hasSize(SIZE_THREE)
                .flatExtracting(LibraryItem::getTitle)
                .allMatch(d -> d.equals("Introduction to Algorithms"));
    }

    @Test
    public void cannotSearchItemsByTitleWhenItemDoesNotExist() {

        //Given
        ConcurrentMap<Integer, LibraryItem> items = getLibraryItemMap();
        when(dataStore.getLibraryItems()).thenReturn(items);

        //When
        List<LibraryItem> returnedItems = libraryCsvDataService.getItemsByTitle("Fake Title");

        //Then
        assertThat(returnedItems).isEmpty();
    }

    @Test
    public void canThrowExceptionWithNullParameterToGetItemsByTitle() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> libraryCsvDataService.getItemsByTitle(null))
                .withMessage("One or  more invalid method parameter(s) passed to getItemsByTitle. Title cannot be null");
    }

    @Test
    public void canAddItemToDataStore() {

        //Given
        LibraryItem vhs =
                new LibraryItem.LibraryItemBuilder(7, 2, ItemType.VHS, "WarGames").build();
        ConcurrentMap<Integer, LibraryItem> items = getLibraryItemMap();
        when(dataStore.getLibraryItems()).thenReturn(items);

        //When
        libraryCsvDataService.addLibraryItem(vhs);

        //Then
        assertThat(items.values()).contains(vhs);
        verify(dataStore, atMost(INVOKED_ONCE)).getLibraryItems();
    }

    @Test
    public void canThrowExceptionWithNullParameterToAddLibrary() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> libraryCsvDataService.addLibraryItem(null))
                .withMessage("One or  more invalid method parameter(s) passed to addLibraryItem. Item cannot be null");
    }

    @Test
    public void canRemoveItemFromDataStore() {

        //Given
        ConcurrentMap<Integer, LibraryItem> items = getLibraryItemMap();
        when(dataStore.getLibraryItems()).thenReturn(items);
        LibraryItem frozenDvd = items.get(5);

        //When
        boolean isRemoved = libraryCsvDataService.removeLibraryItem(frozenDvd);

        //Then
        assertThat(isRemoved).isEqualTo(true);
        assertThat(items.values()).doesNotContain(frozenDvd);
        assertThat(libraryCsvDataService.getItemsByLibraryId(5)).isNull();
    }

    @Test
    public void canThrowExceptionWithNullParameterToRemoveLibraryItem() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> libraryCsvDataService.removeLibraryItem(null))
                .withMessage("One or  more invalid method parameter(s) passed to removeLibraryItem. Item cannot be null");
    }

    @Test
    public void cannotRemoveNonExistingLibraryItem() {

        //Given
        ConcurrentMap<Integer, LibraryItem> items = getLibraryItemMap();
        when(dataStore.getLibraryItems()).thenReturn(items);
        LibraryItem nonExistingVhs =
                new LibraryItem.LibraryItemBuilder(100, 200, ItemType.VHS, "Fake Item").build();

        //When
        boolean isRemoved = libraryCsvDataService.removeLibraryItem(nonExistingVhs);

        //Then
        assertThat(isRemoved).isFalse();
        assertThat(items).hasSize(SIZE_FIVE);
        verify(dataStore, atMost(INVOKED_ONCE)).getLibraryItems();
    }


    @Test
    public void canGetLoanList() {
        //Given
        ConcurrentMap<Loan, Object> loans = getLoanMap();
        when(dataStore.getLoans()).thenReturn(loans);

        //When
        List<Loan> loanList = libraryCsvDataService.getLoan();

        //Then
        assertThat(loanList).isNotEmpty()
                .hasSize(SIZE_THREE);
        verify(dataStore, atMost(INVOKED_ONCE)).getLoans();
    }

    @Test
    public void canAddLoan() {
        //Given
        ConcurrentMap<Loan, Object> loans = getLoanMap();
        when(dataStore.getLoans()).thenReturn(loans);

        LibraryItem vhs =
                new LibraryItem.LibraryItemBuilder(7, 2, ItemType.VHS, "WarGames").build();
        Person customer = new Customer(4, "Customer 4", "Customer Last Name");

        //When
        libraryCsvDataService.addLoan(customer, vhs, LocalDate.now(), LocalDate.now().plusDays(7));

        //Then
        assertThat(loans).isNotEmpty()
                .hasSize(SIZE_FOUR);
        verify(dataStore, atMost(INVOKED_ONCE)).getLoans();
    }

    @Test
    public void canReturnLoanItem() {
        //Given
        ConcurrentMap<Loan, Object> loans = getLoanMap();
        when(dataStore.getLoans()).thenReturn(loans);

        LibraryItem vhs =
                new LibraryItem.LibraryItemBuilder(7, 2, ItemType.VHS, "WarGames").build();
        Person customer = new Customer(4, "Customer 4", "Customer Last Name");
        Loan newLoan = new Loan(customer, vhs, LocalDate.now(), LocalDate.now().plusDays(SEVEN_DAYS));

        loans.put(newLoan, PRESENT);

        //When
        boolean isReturnSuccess = libraryCsvDataService.returnLoanedItem(vhs);

        //Then
        assertThat(isReturnSuccess).isTrue();
        assertThat(loans).isNotEmpty()
                .hasSize(SIZE_THREE)
                .doesNotContainKeys(newLoan);
        verify(dataStore, atMost(INVOKED_TWICE)).getLoans();
    }

    @Test
    public void isBorrowed() {
    }

    private ConcurrentMap<Integer, LibraryItem> getLibraryItemMap() {

        ConcurrentMap<Integer, LibraryItem> libItems = new ConcurrentHashMap<>();
        libItems.putAll(
                Map.of(
                        1, new LibraryItem.LibraryItemBuilder(1, 1, ItemType.BOOK, "Introduction to Algorithms").build(),
                        2, new LibraryItem.LibraryItemBuilder(2, 1, ItemType.BOOK, "Introduction to Algorithms").build(),
                        3, new LibraryItem.LibraryItemBuilder(3, 1, ItemType.BOOK, "Introduction to Algorithms").build(),
                        4, new LibraryItem.LibraryItemBuilder(4, 2, ItemType.DVD, "Pi").build(),
                        5, new LibraryItem.LibraryItemBuilder(5, 3, ItemType.DVD, "Frozen").build()
                ));
        return libItems;
    }

    private ConcurrentMap<Loan, Object> getLoanMap() {

        final ConcurrentMap<Loan, Object> loanItems = new ConcurrentHashMap<>();
        final Person customer1 = new Customer(1, "Customer 1", "Custmer 1 Last Name");
        final Person customer2 = new Customer(2, "Customer 2", "Custmer 2 Last Name");

        loanItems.putAll(
                Map.of(
                        new Loan(customer1, getLibraryItemMap().get(1), LocalDate.now(), LocalDate.now().plusDays(SEVEN_DAYS)), PRESENT,
                        new Loan(customer1, getLibraryItemMap().get(2), LocalDate.now(), LocalDate.now().plusDays(SEVEN_DAYS)), PRESENT,
                        new Loan(customer2, getLibraryItemMap().get(3), LocalDate.now(), LocalDate.now().plusDays(SEVEN_DAYS)), PRESENT
                ));
        return loanItems;
    }
}