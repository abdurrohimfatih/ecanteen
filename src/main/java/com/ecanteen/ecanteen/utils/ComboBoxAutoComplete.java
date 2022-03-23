package com.ecanteen.ecanteen.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.stream.Stream;

public class ComboBoxAutoComplete<T> {

    private final ComboBox<T> cmb;
    String filter = "";
    private final ObservableList<T> originalItems;

    public ComboBoxAutoComplete(ComboBox<T> cmb) {
        this.cmb = cmb;
        originalItems = FXCollections.observableArrayList(cmb.getItems());
        cmb.setTooltip(new Tooltip("Bisa diketik"));
        cmb.setOnKeyPressed(this::handleOnKeyPressed);
        cmb.setOnHidden(this::handleOnHiding);
    }

    public void handleOnKeyPressed(KeyEvent e) {
        ObservableList<T> filteredList = FXCollections.observableArrayList();
        KeyCode code = e.getCode();

        if (code.isLetterKey()) {
            filter += e.getText();
        }
        if (code == KeyCode.BACK_SPACE && filter.length() > 0) {
            filter = filter.substring(0, filter.length() - 1);
            cmb.getItems().setAll(originalItems);
        }
        if (code == KeyCode.ESCAPE) {
            filter = "";
        }
        if (filter.length() == 0) {
            filteredList = originalItems;
            cmb.getTooltip().hide();
        } else {
            Stream<T> items = cmb.getItems().stream();
            String txtUsr = filter.toLowerCase();
            items.filter(el -> el.toString().toLowerCase().contains(txtUsr)).forEach(filteredList::add);
            cmb.getTooltip().setText(txtUsr);
            Stage stage = (Stage) cmb.getScene().getWindow();
            double posX = cmb.getBoundsInParent().getMinX() + cmb.getParent().getBoundsInParent().getMinX();
            double posY = cmb.getBoundsInParent().getMinY() + cmb.getParent().getBoundsInParent().getMinY() + cmb.getParent().getParent().getBoundsInParent().getMinY();
            cmb.getTooltip().show(stage, posX, posY);
            cmb.show();
        }
        cmb.getItems().setAll(filteredList);
    }

    public void handleOnHiding(Event e) {
        filter = "";
        cmb.getTooltip().hide();
        T s = cmb.getSelectionModel().getSelectedItem();
        cmb.getItems().setAll(originalItems);
        cmb.getSelectionModel().select(s);
    }
}