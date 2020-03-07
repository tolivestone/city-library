package com.citylibrary.businessexception;

public class LibraryItemNotFoundException extends LibraryOperationException {
    public LibraryItemNotFoundException(String message) {
        super(message);
    }
}
