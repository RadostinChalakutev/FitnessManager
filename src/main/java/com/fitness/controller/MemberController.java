package com.fitness.controller;

import com.fitness.model.Subscription;
import com.fitness.database.SubscriptionRepository;
import com.fitness.service.MemberService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MemberController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField egnField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<Subscription> subscriptionComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField endDateField;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    private final SubscriptionRepository subscriptionRepository =
            new SubscriptionRepository();

    private final MemberService memberService =
            new MemberService();

    @FXML
    public void initialize() {

        loadSubscriptions();
        loadPaymentMethods();
        configureStartDatePicker();

        subscriptionComboBox.setOnAction(event -> calculateEndDate());

        startDatePicker.setOnAction(event -> calculateEndDate());
    }

    private void loadSubscriptions() {

        List<Subscription> subscriptions =
                subscriptionRepository.findAll();

        subscriptionComboBox.setItems(
                FXCollections.observableArrayList(subscriptions)
        );

        subscriptionComboBox.setCellFactory(listView ->
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Subscription subscription,
                            boolean empty) {

                        super.updateItem(subscription, empty);

                        if (empty || subscription == null) {
                            setText(null);
                        } else {
                            setText(
                                    subscription.getName()
                                            + " - "
                                            + String.format(
                                            "%.2f",
                                            subscription.getPrice()
                                    )
                                            + " лв."
                            );
                        }
                    }
                }
        );

        subscriptionComboBox.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Subscription subscription,
                            boolean empty) {

                        super.updateItem(subscription, empty);

                        if (empty || subscription == null) {
                            setText(null);
                        } else {
                            setText(subscription.getName());
                        }
                    }
                }
        );
    }

    private void loadPaymentMethods() {

        paymentMethodComboBox.setItems(
                FXCollections.observableArrayList(
                        "Cash",
                        "Card"
                )
        );
    }

    private void configureStartDatePicker() {

        LocalDate today = LocalDate.now();

        startDatePicker.setValue(today);

        startDatePicker.setDayCellFactory(datePicker ->
                new DateCell() {

                    @Override
                    public void updateItem(
                            LocalDate date,
                            boolean empty) {

                        super.updateItem(date, empty);

                        if (date.isBefore(today)) {
                            setDisable(true);
                        }
                    }
                }
        );

        calculateEndDate();
    }

    private void calculateEndDate() {

        LocalDate startDate =
                startDatePicker.getValue();

        Subscription selectedSubscription =
                subscriptionComboBox.getValue();

        if (startDate == null ||
                selectedSubscription == null) {

            endDateField.clear();

            return;
        }

        LocalDate endDate =
                startDate.plusMonths(
                        selectedSubscription.getDurationMonths()
                );

        endDateField.setText(
                endDate.toString()
        );
    }

    @FXML
    private void saveMember(ActionEvent event) throws IOException {

        Subscription selectedSubscription =
                subscriptionComboBox.getValue();

        LocalDate startDate =
                startDatePicker.getValue();

        String paymentMethod =
                paymentMethodComboBox.getValue();

        if (firstNameField.getText().isBlank()
                || lastNameField.getText().isBlank()
                || phoneField.getText().isBlank()
                || egnField.getText().isBlank()
                || emailField.getText().isBlank()
                || selectedSubscription == null
                || startDate == null
                || paymentMethod == null) {

            System.out.println(
                    "Please fill in all fields."
            );

            return;
        }

        memberService.saveMember(
                firstNameField.getText(),
                lastNameField.getText(),
                phoneField.getText(),
                egnField.getText(),
                emailField.getText(),
                selectedSubscription,
                startDate,
                paymentMethod
        );

        System.out.println(
                "Member saved successfully."
        );

        goToDashboard(event);
    }

    @FXML
    private void goBack(ActionEvent event)
            throws IOException {

        goToDashboard(event);
    }

    private void goToDashboard(ActionEvent event)
            throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/fxml/main-view.fxml"
                        )
                );

        Parent root = loader.load();

        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        stage.setScene(
                new Scene(root, 1000, 650)
        );

        stage.show();
    }
}