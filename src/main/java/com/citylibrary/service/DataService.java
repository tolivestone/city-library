package com.citylibrary.service;

import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;

import java.time.LocalDate;
import java.util.List;

public interface DataService {
    void clearDataStore();

    void reloadDataStore();

    void addLibraryItem(final LibraryItem item);

    boolean removeLibraryItem(final LibraryItem item);

    void addLoan(Person customer, LibraryItem item, LocalDate issueDate, LocalDate dueDate);

    boolean returnLoanedItem(LibraryItem item);

    List<LibraryItem> getCurrentInventory();

    List<LibraryItem> getCurrentLoanableInventory();

    List<LibraryItem> getItemsByTitle(final String title);

    LibraryItem getItemsByLibraryId(final int libraryId);

    List<Loan> getLoan();

    Person getCustomerById(final int customerID);
}
