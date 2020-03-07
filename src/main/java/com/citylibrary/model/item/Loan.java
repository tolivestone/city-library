package com.citylibrary.model.item;

import com.citylibrary.model.actor.Person;
import java.time.LocalDate;
import java.util.Objects;

public final class Loan {
    private final Person customer;
    private final LibraryItem item;
    private final LocalDate issueDate;
    private final LocalDate dueDate;


    public Loan(final Person customer, final  LibraryItem item, final LocalDate issueDate, final LocalDate dueDate) {
        this.customer = customer;
        this.item = item;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Person getCustomer() {
        return customer;
    }

    public LibraryItem getItem() {
        return item;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loan loan = (Loan) o;

        if (!Objects.equals(customer, loan.customer)) return false;
        if (!Objects.equals(item, loan.item)) return false;
        if (!Objects.equals(issueDate, loan.issueDate)) return false;
        return Objects.equals(dueDate, loan.dueDate);
    }

    @Override
    public int hashCode() {
        int result = customer != null ? customer.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        result = 31 * result + (dueDate != null ? dueDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Loan[" +
                "\ncustomer=" + customer +
                ",\n item=" + item +
                ",\n issueDate=" + issueDate +
                "\n  dueDate=" + dueDate +
                "\n]";
    }
}
