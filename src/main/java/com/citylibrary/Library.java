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
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.citylibrary.constant.Constant.LOAN_PERIOD;

@Component
public final class Library {

    @Autowired
    private DataService dataService;

    @Autowired
    private LendingService lendingService;

    public boolean borrowItem(Person customer, LibraryItem item) throws
            LibraryItemNotLoanableException, LibraryItemNotFoundException {
        if(customer == null || item == null)
            throw new IllegalArgumentException("Customer or item is ");

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD);

        return lendingService.borrowItem(customer,item,issueDate,dueDate);
    }

    public boolean returnItem(LibraryItem item) throws LibraryOperationException {
        Objects.requireNonNull(item, "Item cannot be null");
        return lendingService.returnItem(item);
    }

    public List<Loan> getOverDueItems() {
        return dataService
                .getLoan()
                .parallelStream()
                .filter(item->item.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Loan> getItemBorrowedByUser(Person customer) {
        Objects.requireNonNull(customer,"Customer cannot be null");
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

    public boolean isBookAvailable(LibraryItem libraryItem) {
        Objects.requireNonNull(libraryItem, "Library Item cannot be null");

        return dataService
                .getCurrentLoanableInventory()
                .parallelStream()
                .anyMatch(item->item.equals(libraryItem) && item.getType() == ItemType.BOOK);
    }

    public LibraryItem findItemByTitleAndType(String title, ItemType itemType) {
        return
                dataService.getCurrentInventory()
                .parallelStream()
                .filter(item-> item.getTitle().contains(title) && item.getType().equals(itemType))
                .findFirst().get();
    }
}
