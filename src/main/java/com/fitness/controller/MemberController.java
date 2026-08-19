package com.fitness.controller;

import com.fitness.database.SubscriptionRepository;
import com.fitness.model.Member;
import com.fitness.model.Subscription;
import com.fitness.service.MemberService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

    @FXML
    private Label formTitle;

    @FXML
    private Button saveButton;

    private final SubscriptionRepository subscriptionRepository =
            new SubscriptionRepository();

    private final MemberService memberService =
            new MemberService();

    private Member memberBeingEdited;

    @FXML
    public void initialize() {

        loadSubscriptions();
        loadPaymentMethods();
        configureStartDatePicker();

        subscriptionComboBox.setOnAction(
                event -> calculateEndDate()
        );

        startDatePicker.setOnAction(
                event -> calculateEndDate()
        );
    }

    private void loadSubscriptions() {

        List<Subscription> subscriptions =
                subscriptionRepository.findAll();

        subscriptionComboBox.setItems(
                FXCollections.observableArrayList(
                        subscriptions
                )
        );

        subscriptionComboBox.setCellFactory(listView ->
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Subscription subscription,
                            boolean empty) {

                        super.updateItem(
                                subscription,
                                empty
                        );

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

                        super.updateItem(
                                subscription,
                                empty
                        );

                        if (empty || subscription == null) {
                            setText(null);
                        } else {
                            setText(
                                    subscription.getName()
                            );
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

        startDatePicker.setDayCellFactory(
                datePicker ->
                        new DateCell() {

                            @Override
                            public void updateItem(
                                    LocalDate date,
                                    boolean empty) {

                                super.updateItem(
                                        date,
                                        empty
                                );

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

        Subscription subscription =
                subscriptionComboBox.getValue();

        if (startDate == null ||
                subscription == null) {

            endDateField.clear();

            return;
        }

        LocalDate endDate =
                startDate.plusMonths(
                        subscription.getDurationMonths()
                );

        endDateField.setText(
                endDate.toString()
        );
    }

    public void setMemberForEdit(Member member) {

        this.memberBeingEdited = member;

        firstNameField.setText(
                member.getFirstName()
        );

        lastNameField.setText(
                member.getLastName()
        );

        phoneField.setText(
                member.getPhone()
        );

        egnField.setText(
                member.getEgn()
        );

        emailField.setText(
                member.getEmail()
        );

        startDatePicker.setValue(
                member.getStartDate()
        );

        paymentMethodComboBox.setValue(
                member.getPaymentMethod()
        );
        subscriptionComboBox.setDisable(true);
        paymentMethodComboBox.setDisable(true);

        for (Subscription subscription :
                subscriptionComboBox.getItems()) {

            if (subscription.getName().equals(
                    member.getSubscription()
            )) {

                subscriptionComboBox.setValue(
                        subscription
                );

                break;
            }
        }

        calculateEndDate();

        if (formTitle != null) {
            formTitle.setText("Edit Member");
        }

        if (saveButton != null) {
            saveButton.setText("Update Member");
        }
    }

    @FXML
    private void saveMember(ActionEvent event)
            throws IOException {

        Subscription subscription =
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
                || subscription == null
                || startDate == null
                || paymentMethod == null) {

            System.out.println(
                    "Please fill in all fields."
            );

            return;
        }

        if (memberBeingEdited == null) {

            memberService.saveMember(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    phoneField.getText(),
                    egnField.getText(),
                    emailField.getText(),
                    subscription,
                    startDate,
                    paymentMethod
            );

            System.out.println(
                    "Member saved successfully."
            );

        } else {

            memberBeingEdited.setFirstName(
                    firstNameField.getText()
            );

            memberBeingEdited.setLastName(
                    lastNameField.getText()
            );

            memberBeingEdited.setPhone(
                    phoneField.getText()
            );

            memberBeingEdited.setEgn(
                    egnField.getText()
            );

            memberBeingEdited.setEmail(
                    emailField.getText()
            );

            memberService.updateMember(
                    memberBeingEdited
            );

            System.out.println(
                    "Member updated successfully."
            );
        }

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