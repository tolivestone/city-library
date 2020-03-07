package com.citylibrary.businessexception;

public class LibraryItemNotLoanableException extends LibraryOperationException {
    public LibraryItemNotLoanableException(String message) {
        super(message);
    }
}
