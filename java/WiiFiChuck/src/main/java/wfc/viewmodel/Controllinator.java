package wfc.viewmodel;

/**
 * Created by Ljoha on 7/11/2015.
 */

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wfc.binding.Status;
import wfc.component.mixin.PersistSelection;
import wfc.model.Serial;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class Controllinator implements Initializable {
    private Status statusBinding;
    private StringProperty status;

    @Autowired
    public Serial serial;

    @FXML
    public ComboBox<String> portNamesCmb;
    @FXML
    public Label statusLbl;
    @FXML
    public Canvas showerCnv;

    private SingleSelectionModel<String> portName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //get properties
        this.portName = this.portNamesCmb.getSelectionModel();

        //create bindings
        this.statusBinding = new Status(serial.portNameProperty(), serial.statusProperty());

        //bind
        this.portNamesCmb.itemsProperty().bind(new ReadOnlyListWrapper<String>(serial.getPortNames()));
        this.statusLbl.textProperty().bind(statusBinding);

        //initialize component mixins
        new PersistSelection(this.portNamesCmb);

        //start stuff
        this.portNamesCmb.getSelectionModel().select(0);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> Platform.runLater(() -> serial.updatePortNames()), 1, 1, TimeUnit.SECONDS);
//        GraphicsContext gc1 = showerCnv.getGraphicsContext2D();
//        gc1.setFill(Color.GREEN);
//        gc1.fillOval(50, 50, 20, 20);
//        GraphicsContext gc2 = showerCnv.getGraphicsContext2D();
//        gc2.setFill(Color.BLUE);
//        gc2.fillOval(100, 100, 20, 20);

        GridPane root = (GridPane) showerCnv.getParent();
//        drawNgonPiece(root, 3);
        drawNgonPiece(root, 1);
        drawNgonPiece(root, 3);
    }

    private void drawNgonPiece(GridPane root, int pieceNumber) {
        Polygon polygon = new Polygon();
        Double radius = new Double(75);
        Double startPoint = new Double(50);
        polygon.getPoints().addAll(radius * Math.cos(new Double(pieceNumber * 45 + 22.5) * Math.PI / 180) + startPoint);
        polygon.getPoints().addAll(radius * Math.sin(new Double(pieceNumber * 45 + 22.5) * Math.PI / 180) + startPoint);
        polygon.getPoints().addAll(radius * Math.cos(new Double((pieceNumber + 1) * 45 + 22.5) * Math.PI / 180) + startPoint);
        polygon.getPoints().addAll(radius * Math.sin(new Double((pieceNumber + 1) * 45 + 22.5) * Math.PI / 180) + startPoint);
        polygon.getPoints().addAll(radius / 2 * Math.cos(new Double((pieceNumber + 1) * 45 + 22.5) * Math.PI / 180) + startPoint);
        polygon.getPoints().addAll(radius / 2 * Math.sin(new Double((pieceNumber + 1) * 45 + 22.5) * Math.PI / 180) + startPoint);
        polygon.getPoints().addAll(radius / 2 * Math.cos(new Double(pieceNumber * 45 + 22.5) * Math.PI / 180) + startPoint);
        polygon.getPoints().addAll(radius / 2 * Math.sin(new Double(pieceNumber * 45 + 22.5) * Math.PI / 180) + startPoint);
//        polygon.setTranslateX(100);
        root.add(polygon, 0, 2);
    }

    private void drawNgon(GridPane root) {
        Polygon circle = new Polygon();
        Double radius = new Double(75);
        Double startPoint = new Double(50);
        for (int i = 1; i <= 8; i++) {
            circle.getPoints().addAll(radius * Math.cos(new Double(i * 45 + 22.5) * Math.PI / 180) + startPoint);
            circle.getPoints().addAll(radius * Math.sin(new Double(i * 45 + 22.5) * Math.PI / 180) + startPoint);
        }
//        circle.setTranslateX(100);
        root.add(circle, 0, 2);
    }

    public void connectDisconnect(ActionEvent evt) {
        if (serial.getStatus() == Serial.STATUS.CONNECTED.toString()) {
            serial.disconnect();
        } else {
            serial.connect(this.portName.getSelectedItem());
        }
    }

    public void refresh(ActionEvent evt) {
        serial.updatePortNames();
    }
}
