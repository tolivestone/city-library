package com.citylibrary.model.item;

import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;

public final class Book extends LibraryItem {

    protected Book(final int libraryId, final int itemId, final String title, final String description, final int shelfId) {
        super(libraryId, itemId, ItemType.BOOK, title, description, shelfId);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
