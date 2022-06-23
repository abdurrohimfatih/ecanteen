package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.IncomeDaoImpl;
import com.ecanteen.ecanteen.entities.Income;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import com.ecanteen.ecanteen.utils.ReportGenerator;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class IncomeReportController implements Initializable {
    @FXML
    private MenuButton masterMenuButton;
    @FXML
    private MenuItem productMenuItem;
    @FXML
    private MenuItem categoryMenuItem;
    @FXML
    private MenuButton stockMenuButton;
    @FXML
    private MenuItem addStockMenuItem;
    @FXML
    private MenuItem returnStockMenuItem;
    @FXML
    private Button userMenuButton;
    @FXML
    private Button customerMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private MenuButton reportMenuButton;
    @FXML
    private MenuItem stockReportMenuItem;
    @FXML
    private MenuItem incomeReportMenuItem;
    @FXML
    private MenuItem supplierReportMenuItem;
    @FXML
    private MenuButton recapMenuButton;
    @FXML
    private MenuItem incomeRecapMenuItem;
    @FXML
    private MenuItem stockRecapMenuItem;
    @FXML
    private MenuItem supplierRecapMenuItem;
    @FXML
    private Button topUpMenuButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private TableView<Income> incomeTableView;
    @FXML
    private TableColumn<Income, Integer> noTableColumn;
    @FXML
    private TableColumn<Income, String> cashierTableColumn;
    @FXML
    private TableColumn<Income, String> incomeTableColumn;
    @FXML
    private TableColumn<Income, String> profitTableColumn;
    @FXML
    private Button printButton;

    private IncomeDaoImpl incomeDao;
    private ObservableList<Income> incomes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        incomeDao = new IncomeDaoImpl();
        incomes = FXCollections.observableArrayList();

        Helper.formatDatePicker(dateDatePicker);
        dateDatePicker.getEditor().setDisable(true);
        dateDatePicker.getEditor().setOpacity(1);
        dateDatePicker.setValue(LocalDate.now());

        String date = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            incomes.addAll(incomeDao.fetchIncomeAdmin(date));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        incomeTableView.setPlaceholder(new Label("Tidak ada data."));
        incomeTableView.setItems(incomes);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(incomeTableView.getItems().indexOf(data.getValue()) + 1));
        cashierTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCashier().getName()));
        incomeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIncome()));
        profitTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProfit()));

        printButton.setDisable(incomeTableView.getItems().isEmpty());
    }

    @FXML
    private void dateDatePickerAction(ActionEvent actionEvent) {
        String date = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            incomes.clear();
            incomes.addAll(incomeDao.fetchIncomeAdmin(date));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        incomeTableView.setItems(incomes);

        printButton.setDisable(incomeTableView.getItems().isEmpty());
    }

    @FXML
    private void printButtonAction(ActionEvent actionEvent) {
        incomes = incomeTableView.getItems();

        String date = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id")));
        String employee = Common.user.getName();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        int incomeInt;
        int profitInt;
        int totalIncomeInt = 0;
        int totalProfitInt = 0;

        for (Income i : incomes) {
            String[] incomeArray = i.getIncome().split("\\.");
            String[] profitArray = i.getProfit().split("\\.");
            StringBuilder inc = new StringBuilder();
            StringBuilder pro = new StringBuilder();
            for (String s : incomeArray) {
                inc.append(s);
            }
            for (String s : profitArray) {
                pro.append(s);
            }
            incomeInt = Integer.parseInt(String.valueOf(inc));
            profitInt = Integer.parseInt(String.valueOf(pro));
            totalIncomeInt += incomeInt;
            totalProfitInt += profitInt;
        }

        String totalIncomeString = formatter.format(totalIncomeInt);
        String totalProfitString = formatter.format(totalProfitInt);

        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeNow = Helper.formattedTimeNow();

        new ReportGenerator().printIncomeReport(incomes, totalIncomeString, totalProfitString, date, employee, dateNow, timeNow);
    }

    @FXML
    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(masterMenuButton, "Admin - Produk", "product-view.fxml");
    }

    @FXML
    private void categoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(masterMenuButton, "Admin - Kategori", "category-view.fxml");
    }

    @FXML
    private void addStockMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Tambah Stok", "add-stock-view.fxml");
    }

    @FXML
    private void returnStockMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Return Stok", "return-stock-view.fxml");
    }

    @FXML
    private void userButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(userMenuButton, "Admin - User", "user-view.fxml");
    }

    @FXML
    private void supplierButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
    }

    @FXML
    private void stockReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Stok", "stock-report-view.fxml");
    }

    @FXML
    private void supplierReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Supplier", "supplier-report-view.fxml");
    }

    @FXML
    private void stockRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Stok", "stock-recap-view.fxml");
    }

    @FXML
    private void incomeRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Pendapatan", "income-recap-view.fxml");
    }

    @FXML
    private void supplierRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Supplier", "supplier-recap-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
