package com.citylibrary.service;

import com.citylibrary.csvhelper.CSVDataLoader;
import com.citylibrary.db.DataStore;
import com.citylibrary.enums.Status;
import com.citylibrary.model.actor.Person;
import com.citylibrary.model.item.LibraryItem;
import com.citylibrary.model.item.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSVDataService implements DataService {

    private final DataStore dataStore;
    private final CSVDataLoader csvDataLoader;

    private static final Object PRESENT = new Object();
    private static final Logger logger = LoggerFactory.getLogger(CSVDataService.class);

    // constructor injection gives an opportunity to mock and run unit tests outside spring framework
    @Autowired
    public CSVDataService(final DataStore dataStore, final CSVDataLoader csvDataLoader) {
        this.dataStore = dataStore;
        this.csvDataLoader = csvDataLoader;
    }

    @PostConstruct
    public void initilize() {
        this.reloadDataStore();
    }

    @Override
    public void clearDataStore() {
        dataStore.getLibraryItems().clear();
        dataStore.getLoans().clear();
        dataStore.getCustomers().clear();
    }

    @Override
    public void reloadDataStore() {

        this.clearDataStore();

        dataStore.getLibraryItems()
                .putAll(csvDataLoader.getLibraryItemsFromCsv().stream()
                        .collect(Collectors.toMap(LibraryItem::getLibraryId, item -> item)));

        dataStore.getCustomers()
                .putAll(csvDataLoader.getCustomersFromCsv().parallelStream()
                        .collect(Collectors.toMap(Person::getId, customer -> customer)));

    }

    @Override
    public List<LibraryItem> getCurrentInventory() {
        Predicates all = new Predicates();
        return getLibraryItems(all);
    }

    @Override
    public List<LibraryItem> getCurrentLoanableInventory() {
        Predicates loanable = new Predicates(Status.AVAILABLE);
        return getLibraryItems(loanable);
    }


    @Override
    public List<LibraryItem> getItemsByTitle(final String title) {
        if (title == null) {
            String msg = "One or  more invalid method parameter(s) passed to getItemsByTitle. Title cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        Predicates byTitle = new Predicates(title);
        return getLibraryItems(byTitle);
    }

    @Override
    public LibraryItem getItemsByLibraryId(final int libraryId) {
        return dataStore.getLibraryItems().get(libraryId);
    }

    @Override
    public void addLibraryItem(final LibraryItem item) {
        if (item == null) {
            String msg = "One or  more invalid method parameter(s) passed to addLibraryItem. Item cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        dataStore.getLibraryItems().putIfAbsent(item.getLibraryId(), item);
    }

    @Override
    public boolean removeLibraryItem(final LibraryItem item) {
        if (item == null) {
            String msg = "One or  more invalid method parameter(s) passed to removeLibraryItem. Item cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return dataStore.getLibraryItems().remove(item.getLibraryId(), item);
    }

    public boolean isBorrowed(final LibraryItem item) {
        return dataStore.getLoans().keySet().parallelStream()
                .anyMatch(loan -> loan.getItem().equals(item));
    }

    @Override
    public void addLoan(final Person customer, final LibraryItem item, final LocalDate issueDate, final LocalDate dueDate) {
        if (customer == null || item == null || issueDate == null || dueDate == null) {
            String msg = "One or  more invalid method parameter(s) passed to addLoan. Customer, Item, IssueDate, DueDate cannot be null";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        synchronized (item) {
            item.setItemStatus(Status.LOANED);
            Loan newLoan = new Loan(customer, item, issueDate, dueDate);
            dataStore.getLoans().put(newLoan, PRESENT);
        }
    }

    @Override
    public List<Loan> getLoan() {

        List<Loan> loanList;
        loanList = dataStore.getLoans().keySet().parallelStream().collect(Collectors.toList());

        return loanList;
    }

    public boolean returnLoanedItem(final LibraryItem item) {

        boolean success = false;
        Loan loanedItem = dataStore.getLoans().keySet().parallelStream()
                .filter(litem -> litem.getItem().equals(item)).findAny().orElse(null);

        if (loanedItem != null) {
            synchronized (item) {
                item.setItemStatus(Status.AVAILABLE);
                dataStore.getLoans().remove(loanedItem);
                success = true;
            }
        }
        return success;
    }

    @Override
    public Person getCustomerById(final int customerID) {
        return dataStore.getCustomers().get(customerID);
    }

    private List<LibraryItem> getLibraryItems(Predicates p) {
        return dataStore.getLibraryItems().values().parallelStream()
                .filter(p::filter).collect(Collectors.toList());
    }

    private static class Predicates {
        private Status status;
        private String title;
        private int libraryId;

        public boolean filter(final LibraryItem item) {
            if (title != null)
                return item.getTitle().contains(this.title);

            if (libraryId > 0)
                return item.getLibraryId() == this.libraryId;

            if (status != null)
                return item.getItemStatus().equals(status);

            return true;
        }

        public Predicates() {
        }

        public Predicates(final Status status) {
            this.status = status;
        }

        public Predicates(final String title) {
            this.title = title;
        }

        public Predicates(final int libraryId) {
            this.libraryId = libraryId;
        }
    }
}
