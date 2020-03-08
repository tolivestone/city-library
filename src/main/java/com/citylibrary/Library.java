package com.citylibrary;

import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.enums.ItemType;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import com.citylibrary.service.DataService;
import com.citylibrary.service.LendingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.citylibrary.constant.Constant.LOAN_PERIOD;

@Component
public final class Library {


    private final DataService dataService;
    private final LendingService lendingService;
    private static final Logger logger = LoggerFactory.getLogger(Library.class);

    // constructor injection gives an opportunity to mock and run unit tests outside spring framework
    @Autowired
    public Library(final DataService dataService, final LendingService lendingService) {
        this.dataService = dataService;
        this.lendingService = lendingService;
    }

    public boolean borrowItem(final Person customer, final LibraryItem item) throws
            LibraryItemNotLoanableException, LibraryItemNotFoundException {
        if (customer == null || item == null) {
            String msg = "One or  more invalid method parameter(s) passed to borrowItem. customer and item cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD);

        return lendingService.borrowItem(customer, item, issueDate, dueDate);
    }

    public boolean returnItem(final LibraryItem item) throws LibraryOperationException {
        if (item == null) {
            String msg = "One or  more invalid method parameter(s) passed to returnItem. Item cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return lendingService.returnItem(item);
    }

    public List<Loan> getOverDueItems() {
        return dataService
                .getLoan()
                .parallelStream()
                .filter(item -> item.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Loan> getItemBorrowedByUser(final Person customer) {
        if (customer == null) {
            String msg = "One or  more invalid method parameter(s) passed to getItemBorrowedByUser. customer cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return dataService
                .getLoan()
                .parallelStream()
                .filter(item -> item.getCustomer().equals(customer))
                .collect(Collectors.toList());
    }

    public List<LibraryItem> getCurrentInventory() {
        return dataService
                .getCurrentInventory();
    }

    public List<LibraryItem> getCurrentLoanableInventory() {
        return dataService
                .getCurrentLoanableInventory();
    }

    public boolean isBookAvailable(final LibraryItem libraryItem) {
        if (libraryItem == null) {
            String msg = "One or  more invalid method parameter(s) passed to libraryItem. libraryItem cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return dataService
                .getCurrentLoanableInventory()
                .parallelStream()
                .anyMatch(item -> item.equals(libraryItem) && item.getType() == ItemType.BOOK);
    }

    public LibraryItem getItemByTitleAndType(final String title, final ItemType itemType) throws LibraryItemNotFoundException {
        if (title == null || itemType == null) {
            String msg = "One or  more invalid method parameter(s) passed to getItemByTitleAndType. title and itemType cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Optional<LibraryItem> foundItem = dataService.getCurrentInventory()
                .parallelStream()
                .filter(item -> item.getTitle().contains(title) && item.getType().equals(itemType))
                .findFirst();

        if (!foundItem.isPresent()) {
            String msg = "Requested item " + title + " of type" + itemType + "not found";
            logger.info(msg);
            throw new LibraryItemNotFoundException(msg);
        }

        return foundItem.get();
    }

    public LibraryItem getItemByLibraryId(final int libraryId) {
        return dataService.getItemsByLibraryId(libraryId);
    }
}
