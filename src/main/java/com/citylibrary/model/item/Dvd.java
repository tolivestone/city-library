package com.citylibrary.model.item;

import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;

public final class Dvd extends LibraryItem {

    protected Dvd(final int libraryId, final int itemId,final String title, final String description, final int shelfId) {
        super(libraryId,itemId, ItemType.DVD, title, description,shelfId);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
