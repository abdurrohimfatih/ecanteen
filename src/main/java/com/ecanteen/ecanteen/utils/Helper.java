package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.Main;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class Helper {
    public static void changePage(Control control, String title, String fxmlFile) throws IOException {
        Stage stage = (Stage) control.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        stage.getScene().setRoot(fxmlLoader.load());
        stage.setTitle(title + " | IDC");
        stage.show();
    }

    public static String currencyToString(int currency) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(currency);
    }

    public static Integer currencyToInt(String currency) {
        String[] currencyArray = currency.split("\\.");
        StringBuilder curr = new StringBuilder();
        for (String c : currencyArray) {
            curr.append(c);
        }

        return Integer.parseInt(String.valueOf(curr));
    }

    public static void addTextLimiter(final TextInputControl tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }

    public static void toNumberField(final TextField tf) {
        tf.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.matches("\\d*")) {
                tf.setText(t1.replaceAll("\\D", ""));
            }
        });
    }

    public static boolean validateNumberPhone(final TextField textField) {
        return !textField.getText().matches("\\d{10}") &&
                !textField.getText().matches("\\d{11}") &&
                !textField.getText().matches("\\d{12}") &&
                !textField.getText().matches("\\d{13}");
    }

    public static void addThousandSeparator(final TextInputControl textField) {
        final char separator = '.';
        final Pattern p = Pattern.compile("[0-9" + separator + "]*");
        textField.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.isContentChange()) {
                return c;
            }
            String newText = c.getControlNewText();
            if (newText.isEmpty()) {
                return c;
            }
            if (!p.matcher(newText).matches()) {
                return null;
            }

            int suffixCount = c.getControlText().length() - c.getRangeEnd();
            int digits = suffixCount - suffixCount / 4;
            StringBuilder sb = new StringBuilder();

            if (digits % 3 == 0 && digits > 0 && suffixCount % 4 != 0) {
                sb.append(separator);
            }

            for (int i = c.getRangeStart() + c.getText().length() - 1; i >= 0; i--) {
                char letter = newText.charAt(i);
                if (Character.isDigit(letter)) {
                    sb.append(letter);
                    digits++;
                    if (digits % 3 == 0) {
                        sb.append(separator);
                    }
                }
            }

            if (digits % 3 == 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.reverse();
            int length = sb.length();

            c.setRange(0, c.getRangeEnd());
            c.setText(sb.toString());
            c.setCaretPosition(length);
            c.setAnchor(length);

            return c;
        }));
    }

    public static void formatDatePicker(DatePicker datePicker) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) {
                    return "";
                }
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String s) {
                if (s == null || s.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(s, dateTimeFormatter);
            }
        });
    }

    public static String formattedDateNow() {
        LocalDate now = LocalDate.now();
        return now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static String formattedTimeNow() {
        LocalTime now = LocalTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static LocalDate formatter(String text) {
        return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static String hashPassword(String password)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));

            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }

            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static ButtonType alert(Alert.AlertType alertType, String content) {
        Image image;
        String title;
        Alert alert = new Alert(alertType);
        DialogPane pane = alert.getDialogPane();

        HBox hBox = new HBox();
        Label label = new Label(content + "  ");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TextField textField = new TextField(Common.change);
        textField.setEditable(false);
        textField.setStyle("-fx-font-size: 20px; -fx-pref-width: 200px; -fx-font-weight: bold;");
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(label);
        hBox.getChildren().add(textField);

        if (alertType == Alert.AlertType.CONFIRMATION) {
            image = new Image(String.valueOf(Main.class.getResource("image/confirm.png")));
            title = "Konfirmasi";
            ((Button) pane.lookupButton(ButtonType.OK)).setText("Ya");
            ((Button) pane.lookupButton(ButtonType.CANCEL)).setText("Tidak");
        } else if (alertType == Alert.AlertType.ERROR) {
            image = new Image(String.valueOf(Main.class.getResource("image/warning.png")));
            title = "Warning";
        } else if (alertType == Alert.AlertType.NONE) {
            image = new Image(String.valueOf(Main.class.getResource("image/confirm.png")));
            title = "Konfirmasi";
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            ((Button) pane.lookupButton(ButtonType.YES)).setText("Ya");
            ((Button) pane.lookupButton(ButtonType.NO)).setText("Tidak");
            ((Button) pane.lookupButton(ButtonType.CANCEL)).setText("Batal");
            pane.lookupButton(ButtonType.CANCEL).getStyleClass().add("delete");
            pane.lookupButton(ButtonType.CANCEL).getStyleClass().add("cancel-button");
        } else {
            image = new Image(String.valueOf(Main.class.getResource("image/success.png")));
            title = "Sukses";
        }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(content);
        alert.initOwner(Main.getStage());
        alert.initModality(Modality.APPLICATION_MODAL);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(image);

        pane.getStylesheets().add(String.valueOf(Main.class.getResource("css/style.css")));
        pane.getStyleClass().add("myDialog");
        pane.setPrefWidth(400);

        switch (content) {
            case "Kembalian (Rp)" -> {
                pane.setContent(hBox);
                pane.getContent().setStyle("-fx-alignment: center;");
            }
            case "Sisa saldo (Rp)" -> {
                textField.setText(Common.change);
                pane.setContent(hBox);
                pane.getContent().setStyle("-fx-alignment: center;");
            }
            case "Tidak jadi membeli ini?" -> {
                Label label1 = new Label(content);
                Label label2 = new Label(Common.productName);
                label2.setStyle("-fx-font-weight: bold");
                VBox vBox = new VBox();
                vBox.getChildren().add(label1);
                vBox.getChildren().add(label2);
                pane.setContent(vBox);
            }
        }

        alert.showAndWait();

        return alert.getResult();
    }
}
