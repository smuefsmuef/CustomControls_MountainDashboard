package cuie.project.mountain_dashboard.demo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class PresentationModel {

    private final StringProperty title = new SimpleStringProperty("Dent Blanche");
    private final DoubleProperty        peakValue =   new SimpleDoubleProperty(4357);
    private final DoubleProperty        schartenValue   =   new SimpleDoubleProperty(895);
    private final DoubleProperty        distanceValue   =   new SimpleDoubleProperty(7.2);
    private final DoubleProperty        pmValueProperty =   new SimpleDoubleProperty();
    private final ObjectProperty<Color> baseColor       =   new SimpleObjectProperty<>();
    private BooleanProperty on          = new SimpleBooleanProperty(false);

    public double getSchartenValue() {
        return schartenValue.get();
    }

    public DoubleProperty schartenValueProperty() {
        return schartenValue;
    }

    public void setSchartenValue(double schartenValue) {
        this.schartenValue.set(schartenValue);
    }

    public double getPeakValue() {
        return peakValue.get();
    }

    public DoubleProperty peakValueProperty() {
        return peakValue;
    }

    public void setPeakValue(double peakValue) {
        this.peakValue.set(peakValue);
    }

    public double getDistanceValue() {
        return distanceValue.get();
    }

    public DoubleProperty distanceValueProperty() {
        return distanceValue;
    }

    public void setDistanceValue(double distanceValue) {
        this.distanceValue.set(distanceValue);
    }

    public double getPmValueProperty() {
        return pmValueProperty.get();
    }

    public DoubleProperty pmValuePropertyProperty() {
        return pmValueProperty;
    }

    public void setPmValueProperty(double pmValueProperty) {
        this.pmValueProperty.set(pmValueProperty);
    }

    public Color getBaseColor() {
        return baseColor.get();
    }

    public ObjectProperty<Color> baseColorProperty() {
        return baseColor;
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor.set(baseColor);
    }

    public boolean isOn() {
        return on.get();
    }

    public BooleanProperty onProperty() {
        return on;
    }

    public void setOn(boolean on) {
        this.on.set(on);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
}
