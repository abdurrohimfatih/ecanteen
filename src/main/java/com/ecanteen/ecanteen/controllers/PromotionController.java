package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.PromotionDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PromotionController implements Initializable {
    @FXML
    private Button productMenuButton;
    @FXML
    private Button categoryMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private Button promotionMenuButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField percentageTextField;
    @FXML
    private TextArea descriptionTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button resetButton;
    @FXML
    private Label infoLabel;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Promotion> promotionTableView;
    @FXML
    private TableColumn<Promotion, String> idTableColumn;
    @FXML
    private TableColumn<Promotion, String> nameTableColumn;
    @FXML
    private TableColumn<Promotion, String> productBarcodeTableColumn;
    @FXML
    private TableColumn<Promotion, String> productNameTableColumn;
    @FXML
    private TableColumn<Promotion, Integer> percentageTableColumn;
    @FXML
    private TableColumn<Promotion, String> descriptionTableColumn;

    private ObservableList<Promotion> promotions;
    private PromotionDaoImpl promotionDao;
    private Promotion selectedPromotion;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        promotionDao = new PromotionDaoImpl();
        ProductDaoImpl productDao = new ProductDaoImpl();
        promotions = FXCollections.observableArrayList();
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            promotions.addAll(promotionDao.fetchAll());
            products.addAll(productDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        productComboBox.setItems(products);

        promotionTableView.setItems(promotions);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        productBarcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getBarcode()));
        productNameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        percentageTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPercentage()).asObject());
        descriptionTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (idTextField.getText().trim().isEmpty() ||
        nameTextField.getText().trim().isEmpty() ||
        productComboBox.getValue() == null ||
        percentageTextField.getText().trim().isEmpty() ||
        descriptionTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Silakan isi semua field!");
            alert.setHeaderText("Error");
            alert.showAndWait();
        } else {
            Promotion promotion = new Promotion();
            promotion.setId(idTextField.getText().trim());
            promotion.setName(nameTextField.getText().trim());
            promotion.setProduct(productComboBox.getValue());
            promotion.setPercentage(Integer.parseInt(percentageTextField.getText().trim()));
            promotion.setDescription(descriptionTextField.getText().trim());

            try {
                if (promotionDao.addData(promotion) == 1) {
                    promotions.clear();
                    promotions.addAll(promotionDao.fetchAll());
                    resetPromotion();
                    infoLabel.setText("Data berhasil ditambahkan!");
                    infoLabel.setStyle("-fx-text-fill: green");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
    }

    @FXML
    private void categoryTableViewClicked(MouseEvent mouseEvent) {
    }

    @FXML
    private void productMenuButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void categoryMenuButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void supplierMenuButtonAction(ActionEvent actionEvent) {
    }

    private void resetPromotion() {
        idTextField.clear();
        nameTextField.clear();
        productComboBox.setValue(null);
        percentageTextField.clear();
        descriptionTextField.clear();
        selectedPromotion = null;
        promotionTableView.getSelectionModel().clearSelection();
        idTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        idTextField.requestFocus();
        infoLabel.setText("");
    }
}