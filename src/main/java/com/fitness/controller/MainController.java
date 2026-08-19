package com.fitness.controller;

import com.fitness.database.MemberRepository;
import com.fitness.model.Member;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, String> idColumn;

    @FXML
    private TableColumn<Member, String> firstNameColumn;

    @FXML
    private TableColumn<Member, String> lastNameColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, String> subscriptionColumn;

    @FXML
    private TableColumn<Member, String> daysRemainingColumn;

    @FXML
    private TableColumn<Member, String> endDateColumn;

    @FXML
    private TableColumn<Member, String> editColumn;

    private final MemberRepository memberRepository =
            new MemberRepository();

    private final ObservableList<Member> members =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        configureColumns();

        loadMembers();

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filterMembers(newValue)
        );
    }

    private void configureColumns() {

        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.valueOf(
                                cellData.getValue().getId()
                        )
                )
        );

        firstNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getFirstName()
                )
        );

        lastNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getLastName()
                )
        );

        phoneColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getPhone()
                )
        );

        subscriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getSubscription()
                )
        );

        daysRemainingColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        calculateDaysRemaining(
                                cellData.getValue()
                        )
                )
        );

        endDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getEndDate()
                                .toString()
                )
        );

        configureSubscriptionColors();
        configureDaysRemainingColors();

        membersTable.setItems(members);
    }

    private void loadMembers() {

        List<Member> loadedMembers =
                memberRepository.findAll();

        members.setAll(loadedMembers);
    }

    private String calculateDaysRemaining(Member member) {

        LocalDate today = LocalDate.now();

        long days =
                ChronoUnit.DAYS.between(
                        today,
                        member.getEndDate()
                );

        if (days < 0) {
            return "Expired";
        }

        if (days == 0) {
            return "Today";
        }

        return days + " days";
    }

    private void filterMembers(String searchText) {

        if (searchText == null ||
                searchText.isBlank()) {

            members.setAll(
                    memberRepository.findAll()
            );

            return;
        }

        String search =
                searchText.toLowerCase().trim();

        List<Member> allMembers =
                memberRepository.findAll();

        List<Member> filtered =
                allMembers.stream()
                        .filter(member ->
                                String.valueOf(
                                        member.getId()
                                ).contains(search)

                                        || member.getFirstName()
                                        .toLowerCase()
                                        .contains(search)

                                        || member.getLastName()
                                        .toLowerCase()
                                        .contains(search)

                                        || member.getPhone()
                                        .toLowerCase()
                                        .contains(search)

                                        || member.getEgn()
                                        .toLowerCase()
                                        .contains(search)
                        )
                        .toList();

        members.setAll(filtered);
    }

    private void configureSubscriptionColors() {

        subscriptionColumn.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String subscription,
                            boolean empty) {

                        super.updateItem(
                                subscription,
                                empty
                        );

                        if (empty || subscription == null) {

                            setText(null);
                            setStyle("");

                            return;
                        }

                        setText(subscription);

                        if (subscription.equals("1 Month")) {

                            setStyle(
                                    "-fx-text-fill: #16a34a;"
                                            + "-fx-font-weight: bold;"
                            );

                        } else if (
                                subscription.equals("3 Months")) {

                            setStyle(
                                    "-fx-text-fill: #2563eb;"
                                            + "-fx-font-weight: bold;"
                            );

                        } else if (
                                subscription.equals("6 Months")) {

                            setStyle(
                                    "-fx-text-fill: #9333ea;"
                                            + "-fx-font-weight: bold;"
                            );

                        } else if (
                                subscription.equals("12 Months")) {

                            setStyle(
                                    "-fx-text-fill: #ea580c;"
                                            + "-fx-font-weight: bold;"
                            );
                        }
                    }
                }
        );
    }

    private void configureDaysRemainingColors() {

        daysRemainingColumn.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String days,
                            boolean empty) {

                        super.updateItem(
                                days,
                                empty
                        );

                        if (empty || days == null) {

                            setText(null);
                            setStyle("");

                            return;
                        }

                        setText(days);

                        if (days.equals("Expired")) {

                            setStyle(
                                    "-fx-text-fill: #dc2626;"
                                            + "-fx-font-weight: bold;"
                            );

                        } else if (days.equals("Today")) {

                            setStyle(
                                    "-fx-text-fill: #dc2626;"
                                            + "-fx-font-weight: bold;"
                            );

                        } else {

                            try {

                                int remaining =
                                        Integer.parseInt(
                                                days.replace(
                                                        " days",
                                                        ""
                                                )
                                        );

                                if (remaining <= 7) {

                                    setStyle(
                                            "-fx-text-fill: #dc2626;"
                                                    + "-fx-font-weight: bold;"
                                    );

                                } else if (remaining <= 30) {

                                    setStyle(
                                            "-fx-text-fill: #ca8a04;"
                                                    + "-fx-font-weight: bold;"
                                    );

                                } else {

                                    setStyle(
                                            "-fx-text-fill: #16a34a;"
                                                    + "-fx-font-weight: bold;"
                                    );
                                }

                            } catch (NumberFormatException ignored) {

                                setStyle("");
                            }
                        }
                    }
                }
        );
    }

    @FXML
    private void openMembers(ActionEvent event)
            throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/fxml/members-view.fxml"
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