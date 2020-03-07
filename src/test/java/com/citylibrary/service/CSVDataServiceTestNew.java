package com.citylibrary.service;

import com.citylibrary.db.DataStore;
import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;
import com.citylibrary.model.item.LibraryItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CSVDataServiceTestNew {


    @Autowired
    DataStore dataStore;

    @Autowired
    CSVDataService CSVDataService;

    @BeforeEach
    public void setUp() {
        CSVDataService.clearDataStore();
    }


    @Test
    public void getCurrentInventory() {

        List<LibraryItem> items = getLibraryItemList();

        items.parallelStream().forEach(item-> CSVDataService.addLibraryItem(item));

        //Checking inventory
        Assertions.assertThat(CSVDataService.getCurrentInventory())
                .isNotEmpty()
                .hasSize(5)
                .doesNotContainNull()
                .allMatch(d->d.getItemStatus() == Status.AVAILABLE);

        //Checking inventory with loaned items
        items.get(0).setItemStatus(Status.LOANED);
        items.get(4).setItemStatus(Status.LOANED);

        Assertions.assertThat(CSVDataService.getCurrentInventory())
                .isNotEmpty()
                .hasSize(5)
                .doesNotContainNull()
                .anyMatch(d->d.getItemStatus() == Status.LOANED);
    }

    private List<LibraryItem> getLibraryItemList() {
        return List.of(
                new LibraryItem.LibraryItemBuilder(1,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(2,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(3,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(4,2, ItemType.DVD, "Pi").build(),
                new LibraryItem.LibraryItemBuilder(5,3, ItemType.DVD, "Frozen").build()
        );
    }


   /* @Test
    public void getCurrentLoanableInventory() {

        List<LibraryItem> items = getLibraryItemList();

        items.parallelStream().forEach(item-> CSVDataService.addLibraryItem(item));

        //Checking inventory with loaned items
        items.get(0).setItemStatus(Status.LOANED);
        items.get(4).setItemStatus(Status.LOANED);

        Assertions.assertThat(CSVDataService.getCurrentLoanableInventory())
                .isNotEmpty()
                .hasSize(3)
                .extracting("itemStatus")
                .doesNotContain(Status.LOANED)
                .allMatch(d->d.equals(Status.AVAILABLE));
    }

    @Test
    public void searchItemsByTitle_withMatchingItems() {

        List<LibraryItem> items = getLibraryItemList();

        items.parallelStream().forEach(item-> CSVDataService.addLibraryItem(item));

        Assertions.assertThat(CSVDataService.searchItemsByTitle("Introduction to Algorithms"))
                .isNotEmpty()
                .hasSize(3)
                .flatExtracting(LibraryItem::getTitle)
                .allMatch(d->d.equals("Introduction to Algorithms"));
    }

    @Test
    public void searchItemsByTitle_withNoMatchingItems() {

        List<LibraryItem> items = getLibraryItemList();

        items.parallelStream().forEach(item-> CSVDataService.addLibraryItem(item));

        Assertions.assertThat(CSVDataService.searchItemsByTitle("Fake title"))
                .isEmpty();
    }

    @Test
    public void addItem_happyPath() {

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isEmpty();

        LibraryItem vhs =
                new LibraryItem.LibraryItemBuilder(1,2,ItemType.VHS,"WarGames").build();

        boolean isAdded = CSVDataService.addLibraryItem(vhs);

        Assertions.assertThat(isAdded)
                .isEqualTo(true);

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isNotEmpty()
                .hasSize(1)
                .contains(vhs)
                .flatExtracting(LibraryItem::getTitle)
                .allMatch(d-> d.equals("WarGames"));
    }

    @Test
    public void add_withNullParameter() {

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isEmpty();

        Assertions.assertThatNullPointerException()
                .isThrownBy(()-> CSVDataService.addLibraryItem(null))
                .withMessage("Item cannot be null");
    }

    @Test
    public void removeItem_happyPath() throws LibraryOperationException {

        LibraryItem vhs =
                new LibraryItem.LibraryItemBuilder(1,2,ItemType.VHS,"WarGames").build();

        CSVDataService.addLibraryItem(vhs);

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isNotEmpty()
                .hasSize(1)
                .contains(vhs)
                .flatExtracting(LibraryItem::getTitle)
                .allMatch(d-> d.equals("WarGames"));

        boolean isRemoved = CSVDataService.removeLibraryItem(vhs);

        Assertions.assertThat(isRemoved)
                .isEqualTo(true);

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isEmpty();
    }

    @Test
    public void remove_withNullParameter() {

        Assertions.assertThatNullPointerException()
                .isThrownBy(()-> CSVDataService.removeLibraryItem(null))
                .withMessage("Item cannot be null");
    }

    @Test
    public void remove_nonExistingLibraryItem() {

        Assertions.assertThat(CSVDataService.getCurrentInventory())
                .isEmpty();

        LibraryItem vhs =
                new LibraryItem.LibraryItemBuilder(1,2,ItemType.VHS,"WarGames").build();

        Assertions.assertThatExceptionOfType(LibraryOperationException.class)
                .isThrownBy(()-> CSVDataService.removeLibraryItem(vhs))
                .withMessage("Item does not exist");
    }


     private List<LibraryItem> getLibraryItemList() {
         return List.of(
                 new LibraryItem.LibraryItemBuilder(1,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                 new LibraryItem.LibraryItemBuilder(2,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                 new LibraryItem.LibraryItemBuilder(3,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                 new LibraryItem.LibraryItemBuilder(4,2, ItemType.DVD, "Pi").build(),
                 new LibraryItem.LibraryItemBuilder(5,3, ItemType.DVD, "Frozen").build()
         );
     }

    @Test
    public void isBorrowed() {
    }*/
}