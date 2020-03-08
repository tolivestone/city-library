package com.citylibrary.db;

import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;

import java.util.concurrent.ConcurrentMap;


public interface DataStore {
    ConcurrentMap<Integer, LibraryItem> getLibraryItems();

    ConcurrentMap<Integer, Person> getCustomers();

    ConcurrentMap<Loan, Object> getLoans();
}
