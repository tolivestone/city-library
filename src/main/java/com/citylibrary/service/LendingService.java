package com.citylibrary.service;

import com.citylibrary.businessexception.LibraryItemNotFoundException;
import com.citylibrary.businessexception.LibraryItemNotLoanableException;
import com.citylibrary.businessexception.LibraryOperationException;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;

import java.time.LocalDate;

public interface LendingService {
    boolean borrowItem(Person customer, LibraryItem item, LocalDate issueDate, LocalDate dueDate) throws LibraryItemNotLoanableException, LibraryItemNotFoundException;
    boolean returnItem(LibraryItem item) throws LibraryOperationException;
}
