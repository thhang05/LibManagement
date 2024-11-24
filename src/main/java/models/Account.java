package models;

import database.Database;
import enums.AccountStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Account {
    protected String username;
    protected String password;
    protected AccountStatus status;
    protected Person person;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account(String username, String password, AccountStatus status, Person person) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.person = person;
    }

    public Person getPerson() {
        return Database.getInstance().getPerson(username);
    }

    public String getUsername() {
        return username;
    }

    public StringProperty usernameProperty() {
        return new SimpleStringProperty(username);
    }

    public ObjectProperty<AccountStatus> accountStatusProperty() {
        return new SimpleObjectProperty<>(status);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public abstract boolean updateInfo(Person newPerson);
    public abstract boolean resetPassword(String newPassword);
}