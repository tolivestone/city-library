package com.citylibrary.db;

import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class CSVLibraryDataStore implements DataStore {

    private final ConcurrentMap<Integer, LibraryItem> libraryItems = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Person> customers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Loan, Object> loans = new ConcurrentHashMap<>();

    public ConcurrentMap<Integer, LibraryItem> getLibraryItems() {
        return libraryItems;
    }

    public ConcurrentMap<Integer, Person> getCustomers() {
        return customers;
    }

    public ConcurrentMap<Loan, Object> getLoans() {
        return loans;
    }

}
