package com.citylibrary.model.item;

import com.citylibrary.enums.ItemType;
import com.citylibrary.enums.Status;

public abstract class LibraryItem implements Loanable {

    private final int libraryId;            //required unique library id
    private final int itemId;               //required
    private final ItemType type;            //required
    private final String title;             //required
    private volatile Status itemStatus;     //required

    private final String description;       //optional
    private final int shelfId;               //optional

    protected LibraryItem(final int libraryId, final int itemId,final  ItemType type,final String title,final String description,final int shelfId) {
        this.libraryId = libraryId;
        this.itemId = itemId;
        this.type = type;
        this.title = title;
        this.itemStatus = Status.AVAILABLE;
        this.description = description;
        this.shelfId = shelfId;
    }

    public int getLibraryId() {
        return libraryId;
    }

    public int getItemId() {
        return itemId;
    }

    public ItemType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Status getItemStatus() {
        return itemStatus;
    }

    public String getDescription() {
        return description;
    }

    public int getShelfId() {
        return shelfId;
    }

    public final void setItemStatus(final Status itemStatus) {
            this.itemStatus = itemStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryItem that = (LibraryItem) o;
        return libraryId == that.libraryId;
    }

    @Override
    public int hashCode() {
        return 31 * libraryId;
    }

    @Override
    public String toString() {
        return "[" +
                "libraryId=" + libraryId +
                ", itemId=" + itemId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", itemStatus=" + itemStatus +
                ", description='" + description + '\'' +
                ", shelfId=" + shelfId +
                ']';
    }

    //Builder for library items, This will abstract item creation and make sure no item is created with invalid data
    public static class LibraryItemBuilder {

        private final int libraryId;
        private final int itemId;
        private final ItemType type;
        private final String title;
        private String description = "";
        private int ShelfId= 0;

        public LibraryItemBuilder(final int libraryId, final int itemId, final ItemType type, final String title) {
            if(libraryId <=0 || itemId <=0 || type == null || title == null || title.isEmpty())
                        throw new IllegalArgumentException("One or more argurment are not set or valid");

            this.libraryId = libraryId;
            this.itemId = itemId;
            this.type = type;
            this.title = title;
        }

        public LibraryItemBuilder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public LibraryItemBuilder withShelftId(final int ShelfId) {
            this.ShelfId = ShelfId;
            return this;
        }

        public LibraryItem build() {
            LibraryItem item = null;
            switch (this.type) {
                case BOOK:
                    item = new Book(libraryId, itemId, title,description, ShelfId);
                    break;
                case DVD:
                    item =  new Dvd(libraryId, itemId, title,description, ShelfId);
                    break;
                case VHS:
                    item =  new Vhs(libraryId, itemId, title,description, ShelfId);
                    break;
                case CD:
                    item =  new CompactDisc(libraryId, itemId, title,description, ShelfId);
                    break;
            }
            return item;
        }
    }
}
