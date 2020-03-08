package com.citylibrary.service;

import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryItemNotLoanedReturnedException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LibraryItemLendingService implements LendingService {

    private final DataService dataService;
    private static final Logger logger = LoggerFactory.getLogger(LibraryItemLendingService.class);

    // constructor injection gives an opportunity to mock and run unit tests outside spring framework
    @Autowired
    public LibraryItemLendingService(final DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public boolean borrowItem(final Person customer, final LibraryItem item, final LocalDate issueDate, final LocalDate dueDate)
            throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        if (customer == null || item == null || issueDate == null || dueDate == null) {
            String msg =
                    "One or more invalid method parameter(s) passed to addLoan. " +
                            "Customer, Item, IssueDate, DueDate cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (dataService.getItemsByLibraryId(item.getLibraryId()) == null) {
            String msg =
                    "Library item [ " + item.getLibraryId() + " " + item.getTitle()
                            + " not in our inventory";
            logger.info(msg);
            throw new LibraryItemNotFoundException(msg);
        }

        if (!item.isLoanable()) {
            String msg =
                    "Library item [ " + item.getLibraryId() + " " + item.getTitle()
                            + " is not available for loan at this time";
            logger.info(msg);
            throw new LibraryItemNotLoanableException(msg);
        }

        dataService.addLoan(customer, item, issueDate, dueDate);
        return true;
    }

    @Override
    public boolean returnItem(final LibraryItem item) throws LibraryOperationException {
        if (item == null) {
            String msg =
                    "One or more invalid method parameter(s) passed to returnItem. " +
                            "Item cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!isItemLoaned(item)) {
            String msg =
                    "Cannot return Item " + item.getLibraryId() + " " + item.getTitle()
                            + ". It has not been loaned";
            logger.info(msg);
            throw new LibraryItemNotLoanedReturnedException(msg);
        }
        return dataService.returnLoanedItem(item);
    }

    private boolean isItemLoaned(LibraryItem item) {
        return dataService.getLoan().parallelStream()
                .anyMatch(loan -> loan.getItem().equals(item));
    }

}
