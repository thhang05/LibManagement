package Entity;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BookLending {
    private String itemId;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String memberId;
    private int lendingId;

    public BookLending(String itemId, LocalDate creationDate, LocalDate dueDate, LocalDate returnDate, String memberId) {
        this.itemId = itemId;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.memberId = memberId;
    }

    public BookLending(String itemId, LocalDate creationDate, LocalDate dueDate, LocalDate returnDate, String memberId, int lendingID) {
        this.itemId = itemId;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.memberId = memberId;
        this.lendingId = lendingID;
    }

    public String getItemId() {
        return itemId;
    }

    public SimpleStringProperty ItemIdProperty() {
        return new SimpleStringProperty(itemId);
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public ObjectProperty<LocalDate> creationDateProperty() {
        return new SimpleObjectProperty<>(creationDate);
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public ObjectProperty<LocalDate> dueDateProperty() {
        return new SimpleObjectProperty<>(dueDate);
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public ObjectProperty<LocalDate> returnDateProperty() {
        return new SimpleObjectProperty<>(returnDate);
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getMemberId() {
        return memberId;
    }

    public StringProperty MemberIdProperty() {
        return new SimpleStringProperty(memberId);
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void lendBook(BookItem bookItem, String memberId) {
    }

    public int getLendingId() {
        return lendingId;
    }

    public void setLendingId(int lendingId) {
        this.lendingId = lendingId;
    }
}