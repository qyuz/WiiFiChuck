package wfc.component.mixin;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;

/**
 * Created by Ljoha on 7/16/2015.
 */
public class PersistSelection {
    private String persistSelection;
    private ComboBox comboBox;

    public PersistSelection(ComboBox comboBox) {
        this.comboBox = comboBox;

        listenSelectedItem(this.comboBox.getSelectionModel());
        listenItemsChange(this.comboBox.getItems());
    }

    private void listenItemsChange(ObservableList items) {
        items.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                //fix by deferring
                // 1) port name(combobox) will deselect every second time we refresh port names list
                // 2) refresh till port deselects, disconnect device, connect to another port, refresh -> new port name will be selected
                Platform.runLater(() -> comboBox.getSelectionModel().select(persistSelection));
            }
        });
    }

    private void listenSelectedItem(SingleSelectionModel selectedItem) {
        selectedItem.selectedItemProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                String invalidatedSelection = ((ReadOnlyObjectProperty<String>) observable).get();
                if (invalidatedSelection != null) {
                    persistSelection = invalidatedSelection;
                }
            }
        });
    }
}
