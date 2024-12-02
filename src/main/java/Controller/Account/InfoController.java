package Controller.Account;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Entities.Account;
import Entities.Address;
import Entities.Person;

public class InfoController {
    private Stage stage;
    private Account account;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField stateField;

    @FXML
    private TextField zipcodeField;

    @FXML
    private TextField countryField;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    private Label streetErrorLabel;

    @FXML
    private Label cityErrorLabel;

    @FXML
    private Label countryErrorLabel;

    @FXML
    private Label notification1;

    @FXML
    private Label notification2;

    @FXML
    private Button cancelButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setUsernameLabel(Label usernameLabel) {
        this.usernameLabel = usernameLabel;
    }

    public void updateInfo() {
        nameField.setText(account.getPerson().getName());
        emailField.setText(account.getPerson().getEmail());
        phoneField.setText(account.getPerson().getPhoneNumber());
        streetField.setText(account.getPerson().getAddress().getStreetAddress());
        cityField.setText(account.getPerson().getAddress().getCity());
        stateField.setText(account.getPerson().getAddress().getState());
        zipcodeField.setText(account.getPerson().getAddress().getZipcode());
        countryField.setText(account.getPerson().getAddress().getCountry());
    }

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });

        nameField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        emailField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        phoneField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        streetField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        cityField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        stateField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        zipcodeField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        countryField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });
    }

    @FXML
    public void handleUpdate() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String street = streetField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String zipcode = zipcodeField.getText().trim();
        String country = countryField.getText().trim();

        nameErrorLabel.setText("");
        emailErrorLabel.setText("");
        phoneErrorLabel.setText("");
        streetErrorLabel.setText("");
        cityErrorLabel.setText("");
        countryErrorLabel.setText("");
        notification1.setText("");
        notification2.setText("");

        if (name.isBlank()) {
            nameErrorLabel.setText("* Full Name cannot be empty");
            return;
        }

        if (email.isBlank()) {
            emailErrorLabel.setText("* Email cannot be empty");
            return;
        } else {
            int atIndex = email.indexOf('@');
            int dotIndex = email.indexOf('.', atIndex);

            if (atIndex < 1 || dotIndex < atIndex + 2 || dotIndex >= email.length() - 1) {
                emailErrorLabel.setText("* Invalid email format");
                return;
            }
        }

        if (phone.isBlank()) {
            phoneErrorLabel.setText("* Phone Number cannot be empty");
            return;
        }

        if (phone.length() < 10 || phone.length() > 15) {
            phoneErrorLabel.setText("* Invalid phone number format");
            return;
        } else {
            for (char c : phone.toCharArray()) {
                if (!Character.isDigit(c)) {
                    phoneErrorLabel.setText("* Invalid phone number format");
                    return;
                }
            }
        }

        if (street.isBlank()) {
            streetErrorLabel.setText("Street Address cannot be empty");
            return;
        }

        if (city.isBlank()) {
            cityErrorLabel.setText("* City cannot be empty");
            return;
        }

        if (country.isBlank()) {
            countryErrorLabel.setText("* Country cannot be empty");
            return;
        }

        if (zipcode.isBlank()) {
            zipcode = "Not Provide";
        }

        if (state.isBlank()) {
            state = "Not Provide";
        }

        if (account.updateInfo(new Person(name, email, phone, new Address(street, city, state, zipcode, country)))) {
            notification1.setText("Your profile has been updated successfully");
            if (account != null) {
                String username = account.getPerson().getName();
                String[] nameParts = username.split(" ");
                String firstName = nameParts[nameParts.length - 1];
                usernameLabel.setText(firstName);
            }
        } else {
            notification2.setText("Update failed. Please try again");
        }
    }
}
