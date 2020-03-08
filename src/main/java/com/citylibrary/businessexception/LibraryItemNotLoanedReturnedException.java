package com.citylibrary.businessexception;

public class LibraryItemNotLoanedReturnedException extends LibraryOperationException {
    public LibraryItemNotLoanedReturnedException(String message) {
        super(message);
    }
}
