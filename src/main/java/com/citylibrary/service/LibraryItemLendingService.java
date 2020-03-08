package com.citylibrary.service;

import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class LibraryItemLendingService implements LendingService{

    @Autowired
    private DataService dataService;

    @Override
    public boolean borrowItem(Person customer, LibraryItem item, LocalDate issueDate, LocalDate dueDate)
            throws LibraryItemNotLoanableException, LibraryItemNotFoundException {
        if(customer == null || item == null || issueDate == null || dueDate == null)
            throw new IllegalArgumentException("One or more arguments are null");

        if(dataService.getItemsByLibraryId(item.getLibraryId()) == null)
            throw new LibraryItemNotFoundException("Library item [ " + item.getLibraryId() + " " + item.getTitle() +  " not in our inventory");

        if(!item.isLoanable())
            throw new LibraryItemNotLoanableException
                    ("Library item [ " + item.getLibraryId() + " " + item.getTitle() +  " is not available for loan at this time");

        dataService.addLoan(customer, item, issueDate, dueDate);
        return true;
    }

    @Override
    public boolean returnItem(LibraryItem item) throws LibraryOperationException {
        if(item == null) throw new IllegalArgumentException("Item cannot be null");

        if(!isItemLoaned(item))
            throw new LibraryOperationException("Cannot return Item "+ item.getLibraryId() + " " + item.getTitle() + ". It has not been loaned");

        return dataService.returnLoanedItem(item);
    }

    private boolean isItemLoaned(LibraryItem item) {
        return dataService.getLoan().parallelStream()
                .anyMatch(loan -> loan.getItem().equals(item));
    }

}
