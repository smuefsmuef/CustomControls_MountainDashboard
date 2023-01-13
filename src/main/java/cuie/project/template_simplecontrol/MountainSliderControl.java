package cuie.project.template_simplecontrol;

import java.util.Locale;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

/**
Dieser Custom Control ist das Steuerungs-Element zum Mountain Control.
Drei Schieberegler erlauben die Einstellung von Höhe, Distanz  und Schartenhöhe.
Der Custom Control ist in zwei Farb-Themes verfügbar.

 * @author Karin Güdel & Petra Kohler
 */

public class MountainSliderControl extends Region {

   private static final Locale CH = new Locale("de", "CH");

    private static final double ARTBOARD_WIDTH = 100;
    private static final double ARTBOARD_HEIGHT = 100;

    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;

    private static final double MINIMUM_WIDTH = 400;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;

    private static final double MAXIMUM_WIDTH = 1500;

    private static final double MAX_PEAKHIGHT = 4800;
    private static final double MAX_DISTANCE = 20;
    private static final double MAX_SCHARTEN = 2200;

    private Line peakBackgroundLine;
    private Line peakValueLine;
    private Circle peakThumb;
    private Text displayPeakHeight;
    private Text displayPeakLabel;
    
    private Line schartenBackgroundLine;
    private Line schartenValueLine;
    private Circle schartenThumb;
    private Text displaySchartenHeight;
    private Text displaySchartenLabel;
    
    private Line distanceBackgroundLine;
    private Line distanceValueLine;
    private Circle distanceThumb;
    private Text displayDistanceHeight;
    private Text displayDistanceLabel;

    private final DoubleProperty peakValue = new SimpleDoubleProperty(4357);
    private final DoubleProperty schartenValue = new SimpleDoubleProperty(895);
    private final DoubleProperty distanceValue = new SimpleDoubleProperty(7.2);
    private final BooleanProperty on          = new SimpleBooleanProperty(true);


    // fuer Resizing benoetigt
    private Pane drawingPane;

    public MountainSliderControl() {
        initializeSelf();
        initializeParts();
        initializeDrawingPane();
        layoutParts();
        setupEventHandlers();
        setupValueChangeListeners();
        setupBindings();
    }

    private void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/Lato/Lato-Reg.ttf");
        addStylesheetFiles("style.css");

        //init
        if(!isOn()) {
            getStyleClass().add("mountain-slider-dark-control");
        } else {
            getStyleClass().add("mountain-slider-control");
        }
    }

    private void initializeParts() {
        // Gipfelhöhe
        peakBackgroundLine = new Line();
        peakBackgroundLine.setStartX(7);
        peakBackgroundLine.setStartY(11);
        peakBackgroundLine.setEndX(94);
        peakBackgroundLine.setEndY(11);
        peakBackgroundLine.setStrokeLineCap(StrokeLineCap.ROUND);
        peakBackgroundLine.getStyleClass().add("background-line");

        peakValueLine = new Line();
        peakValueLine.setStartX(7);
        peakValueLine.setStartY(11);
        peakValueLine.setEndX(64);
        peakValueLine.setEndY(11);
        peakValueLine.setStrokeLineCap(StrokeLineCap.ROUND);
        peakValueLine.getStyleClass().add("peak-value-line");

        peakThumb = new Circle(7, 11, 1);
        peakThumb.getStyleClass().add("peak-thumb");

        displayPeakHeight = new Text(94-10, 8,"");
        displayPeakHeight.getStyleClass().add("text");

        displayPeakLabel = new Text(7, 8,"Gipfelhöhe [m.ü.M.]");
        displayPeakLabel.getStyleClass().add("text");

        // Schartenhöhe
        schartenBackgroundLine = new Line();
        schartenBackgroundLine.setStartX(7);
        schartenBackgroundLine.setStartY(24);
        schartenBackgroundLine.setEndX(94);
        schartenBackgroundLine.setEndY(24);
        schartenBackgroundLine.setStrokeLineCap(StrokeLineCap.ROUND);
        schartenBackgroundLine.getStyleClass().add("background-line");

        schartenValueLine = new Line();
        schartenValueLine.setStartX(7);
        schartenValueLine.setStartY(24);
        schartenValueLine.setEndX(35);
        schartenValueLine.setEndY(24);
        schartenValueLine.setStrokeLineCap(StrokeLineCap.ROUND);
        schartenValueLine.getStyleClass().add("scharten-value-line");

        schartenThumb = new Circle(7, 24, 1);
        schartenThumb.getStyleClass().add("scharten-thumb");

        displaySchartenHeight = new Text(94-10, 8+13,"");
        displaySchartenHeight.getStyleClass().add("text");

        displaySchartenLabel = new Text(7, 8+13,"Schartenhöhe [m]");
        displaySchartenLabel.getStyleClass().add("text");

        // Distanz
        distanceBackgroundLine = new Line();
        distanceBackgroundLine.setStartX(7);
        distanceBackgroundLine.setStartY(37);
        distanceBackgroundLine.setEndX(94);
        distanceBackgroundLine.setEndY(37);
        distanceBackgroundLine.setStrokeLineCap(StrokeLineCap.ROUND);
        distanceBackgroundLine.getStyleClass().add("background-line");

        distanceValueLine = new Line();
        distanceValueLine.setStartX(7);
        distanceValueLine.setStartY(37);
        distanceValueLine.setEndX(35);
        distanceValueLine.setEndY(37);
        distanceValueLine.setStrokeLineCap(StrokeLineCap.ROUND);
        distanceValueLine.getStyleClass().add("distance-value-line");

        distanceThumb = new Circle(7, 37, 1);
        distanceThumb.getStyleClass().add("distance-thumb");

        displayDistanceHeight = new Text(94-10, 8+13+13,"");
        displayDistanceHeight.getStyleClass().add("text");

        displayDistanceLabel = new Text(7, 8+13+13,"Dominanz [km]");
        displayDistanceLabel.getStyleClass().add("text");
    }

    private void initializeDrawingPane() {
        drawingPane = new Pane();
        drawingPane.getStyleClass().add("drawing-pane");
        drawingPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
    }


    private void layoutParts() {
        drawingPane.getChildren().addAll(
            peakBackgroundLine, peakValueLine,peakThumb, displayPeakHeight, displayPeakLabel,
            schartenBackgroundLine, schartenValueLine, schartenThumb,  displaySchartenHeight, displaySchartenLabel,
            distanceBackgroundLine, distanceValueLine, distanceThumb,  displayDistanceHeight, displayDistanceLabel);

        getChildren().add(drawingPane);
    }

    private void setupEventHandlers() {
        peakThumb.setOnMouseDragged( event -> {
            if(event.getX()*MAX_PEAKHIGHT/101 < 5200 )
            setPeakValue(event.getX()*MAX_PEAKHIGHT/101);
        });

        schartenThumb.setOnMouseDragged( event -> {
            if (event.getX()*MAX_SCHARTEN/101 < 2376) {
                setSchartenValue(event.getX() * MAX_SCHARTEN / 101);
            }
        });

        distanceThumb.setOnMouseDragged( event -> {
            if(event.getX()*MAX_DISTANCE/101 < 22){
                setDistanceValue(event.getX()*MAX_DISTANCE/101);
            }
        });

    }

    private void setupValueChangeListeners() {
        peakValueProperty().addListener((observable, oldValue, newValue) ->
        {
                peakValueLine.setEndX((Double) newValue);
        });

        schartenValueProperty().addListener((observable, oldValue, newValue) ->
        {
            schartenValueLine.setEndX((Double) newValue);
        });

        distanceValueProperty().addListener((observable, oldValue, newValue) ->
        {
            distanceValueLine.setEndX((Double) newValue);
        });

        onProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(true)) {
                getStyleClass().remove("mountain-slider-control");
                getStyleClass().add("mountain-slider-dark-control");

            } else {
                getStyleClass().remove("mountain-slider-dark-control");
                getStyleClass().add("mountain-slider-control");
            }
        });

    }

    private void setupBindings() {
        displayPeakHeight.textProperty().bind(peakValueProperty().asString(CH, "%.0f"));
        peakValueLine.endXProperty().bind(peakValueProperty().multiply((getPeakBackgroundLine().getEndX()-getPeakBackgroundLine().getStartX())).divide(MAX_PEAKHIGHT) );
        peakThumb.centerXProperty().bind(peakValueProperty().multiply((getPeakBackgroundLine().getEndX()-getPeakBackgroundLine().getStartX())).divide(MAX_PEAKHIGHT));

        displaySchartenHeight.textProperty().bind(schartenValueProperty().asString(CH, "%.0f"));
        schartenValueLine.endXProperty().bind(schartenValueProperty().multiply((getSchartenBackgroundLine().getEndX()-getSchartenBackgroundLine().getStartX())).divide(MAX_SCHARTEN) );
        schartenThumb.centerXProperty().bind(schartenValueProperty().multiply((getSchartenBackgroundLine().getEndX()-getSchartenBackgroundLine().getStartX())).divide(MAX_SCHARTEN));

        displayDistanceHeight.textProperty().bind(distanceValueProperty().asString(CH, "%.1f"));
        distanceValueLine.endXProperty().bind(distanceValueProperty().multiply((getDistanceBackgroundLine().getEndX()-getDistanceBackgroundLine().getStartX())).divide(MAX_DISTANCE) );
        distanceThumb.centerXProperty().bind(distanceValueProperty().multiply((getDistanceBackgroundLine().getEndX()-getDistanceBackgroundLine().getStartX())).divide(MAX_DISTANCE));
    }
    


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    private void resize() {
        Insets padding = getPadding();
        double availableWidth = getWidth() - padding.getLeft() - padding.getRight();
        double availableHeight = getHeight() - padding.getTop() - padding.getBottom();

        double width =
            Math.max(Math.min(Math.min(availableWidth, availableHeight * ASPECT_RATIO), MAXIMUM_WIDTH), MINIMUM_WIDTH);

        double scalingFactor = width / ARTBOARD_WIDTH;

        if (availableWidth > 0 && availableHeight > 0) {
            relocateDrawingPaneCentered();
            drawingPane.setScaleX(scalingFactor);
            drawingPane.setScaleY(scalingFactor);
        }
    }

    private void relocateDrawingPaneCentered() {
        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, (getHeight() - ARTBOARD_HEIGHT) * 0.5);
    }

    private void relocateDrawingPaneCenterBottom(double scaleY, double paddingBottom) {
        double visualHeight = ARTBOARD_HEIGHT * scaleY;
        double visualSpace = getHeight() - visualHeight;
        double y = visualSpace + (visualHeight - ARTBOARD_HEIGHT) * 0.5 - paddingBottom;

        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, y);
    }

    private void relocateDrawingPaneCenterTop(double scaleY, double paddingTop) {
        double visualHeight = ARTBOARD_HEIGHT * scaleY;
        double y = (visualHeight - ARTBOARD_HEIGHT) * 0.5 + paddingTop;

        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, y);
    }

    // Sammlung nuetzlicher Funktionen


    private void loadFonts(String... font) {
        for (String f : font) {
            Font.loadFont(getClass().getResourceAsStream(f), 0);
        }
    }

    private void addStylesheetFiles(String... stylesheetFile) {
        for (String file : stylesheetFile) {
            String stylesheet = getClass().getResource(file).toExternalForm();
            getStylesheets().add(stylesheet);
        }
    }

    /**
     * Umrechnen einer Prozentangabe, zwischen 0 und 100, in den tatsaechlichen Wert innerhalb des angegebenen Wertebereichs.
     *
     * @param percentage Wert in Prozent
     * @param minValue   untere Grenze des Wertebereichs
     * @param maxValue   obere Grenze des Wertebereichs
     * @return value der akuelle Wert
     */
    private double percentageToValue(double percentage, double minValue, double maxValue) {
        return ((maxValue - minValue) * percentage) + minValue;
    }

    /**
     * Umrechnen des angegebenen Werts in eine Prozentangabe zwischen 0 und 100.
     *
     * @param value    der aktuelle Wert
     * @param minValue untere Grenze des Wertebereichs
     * @param maxValue obere Grenze des Wertebereichs
     * @return Prozentangabe des aktuellen Werts
     */
    private double valueToPercentage(double value, double minValue, double maxValue) {
        return (value - minValue) / (maxValue - minValue);
    }

    /**
     * Berechnet den Winkel zwischen 0 und 360 Grad, 0 Grad entspricht "Nord", der dem value
     * innerhalb des Wertebereichs zwischen minValue und maxValue entspricht.
     *
     * @param value    der aktuelle Wert
     * @param minValue untere Grenze des Wertebereichs
     * @param maxValue obere Grenze des Wertebereichs
     * @return angle Winkel zwischen 0 und 360 Grad
     */
    private double valueToAngle(double value, double minValue, double maxValue) {
        return percentageToAngle(valueToPercentage(value, minValue, maxValue));
    }

    /**
     * Umrechnung der Maus-Position auf den aktuellen Wert.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     * <p>
     * Lineare Controls wie Slider müssen auf andere Art die Mausposition auf den value umrechnen.
     *
     * @param mouseX   x-Position der Maus
     * @param mouseY   y-Position der Maus
     * @param cx       x-Position des Zentrums des radialen Controls
     * @param cy       y-Position des Zentrums des radialen Controls
     * @param minValue untere Grenze des Wertebereichs
     * @param maxValue obere Grenze des Wertebereichs
     * @return value der dem Winkel entspricht, in dem die Maus zum Mittelpunkt des radialen Controls steht
     */
    private double radialMousePositionToValue(double mouseX, double mouseY, double cx, double cy, double minValue,
                                              double maxValue) {
        double percentage = angleToPercentage(angle(cx, cy, mouseX, mouseY));

        return percentageToValue(percentage, minValue, maxValue);
    }

    /**
     * Umrechnung eines Winkels, zwischen 0 und 360 Grad, in eine Prozentangabe.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     *
     * @param angle der Winkel
     * @return die entsprechende Prozentangabe
     */
    private double angleToPercentage(double angle) {
        return angle / 360.0;
    }

    /**
     * Umrechnung einer Prozentangabe, zwischen 0 und 100, in den entsprechenden Winkel.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     *
     * @param percentage die Prozentangabe
     * @return der entsprechende Winkel
     */
    private double percentageToAngle(double percentage) {
        return 360.0 * percentage;
    }

    /**
     * Berechnet den Winkel zwischen einem Zentrums-Punkt und einem Referenz-Punkt.
     *
     * @param cx x-Position des Zentrums
     * @param cy y-Position des Zentrums
     * @param x  x-Position des Referenzpunkts
     * @param y  y-Position des Referenzpunkts
     * @return winkel zwischen 0 und 360 Grad
     */
    private double angle(double cx, double cy, double x, double y) {
        double deltaX = x - cx;
        double deltaY = y - cy;
        double radius = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx = deltaX / radius;
        double ny = deltaY / radius;
        double theta = Math.toRadians(90) + Math.atan2(ny, nx);

        return Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
    }

    /**
     * Berechnet den Punkt auf einem Kreis mit gegebenen Radius im angegebenen Winkel
     *
     * @param cX     x-Position des Zentrums
     * @param cY     y-Position des Zentrums
     * @param radius Kreisradius
     * @param angle  Winkel zwischen 0 und 360 Grad
     * @return Punkt auf dem Kreis
     */
    private Point2D pointOnCircle(double cX, double cY, double radius, double angle) {
        return new Point2D(cX - (radius * Math.sin(Math.toRadians(angle - 180))),
            cY + (radius * Math.cos(Math.toRadians(angle - 180))));
    }

    /**
     * Erzeugt eine Text-Instanz in der Mitte des CustomControls.
     * Der Text bleibt zentriert auch wenn der angezeigte Text sich aendert.
     *
     * @param styleClass mit dieser StyleClass kann der erzeugte Text via css gestyled werden
     * @return Text
     */
    private Text createCenteredText(String styleClass) {
        return createCenteredText(ARTBOARD_WIDTH * 0.5, ARTBOARD_HEIGHT * 0.5, styleClass);
    }

    /**
     * Erzeugt eine Text-Instanz mit dem angegebenen Zentrum.
     * Der Text bleibt zentriert auch wenn der angezeigte Text sich aendert.
     *
     * @param cx         x-Position des Zentrumspunkt des Textes
     * @param cy         y-Position des Zentrumspunkt des Textes
     * @param styleClass mit dieser StyleClass kann der erzeugte Text via css gestyled werden
     * @return Text
     */
    private Text createCenteredText(double cx, double cy, String styleClass) {
        Text text = new Text();
        text.getStyleClass().add(styleClass);
        text.setTextOrigin(VPos.CENTER);
        text.setTextAlignment(TextAlignment.CENTER);
        double width = cx > ARTBOARD_WIDTH * 0.5 ? ((ARTBOARD_WIDTH - cx) * 2.0) : cx * 2.0;
        text.setWrappingWidth(width);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setY(cy);
        text.setX(cx - (width / 2.0));

        return text;
    }

    /**
     * Erzeugt eine Group von Lines, die zum Beispiel fuer Skalen oder Zifferblaetter verwendet werden koennen.
     * <p>
     * Diese Funktion ist sinnvoll nur fuer radiale Controls einsetzbar.
     *
     * @param cx            x-Position des Zentrumspunkts
     * @param cy            y-Position des Zentrumspunkts
     * @param radius        radius auf dem die Anfangspunkte der Ticks liegen
     * @param numberOfTicks gewuenschte Anzahl von Ticks
     * @param startingAngle Wickel in dem der erste Tick liegt, zwischen 0 und 360 Grad
     * @param overallAngle  gewuenschter Winkel zwischen den erzeugten Ticks, zwischen 0 und 360 Grad
     * @param tickLength    Laenge eines Ticks
     * @param styleClass    Name der StyleClass mit der ein einzelner Tick via css gestyled werden kann
     * @return Group mit allen Ticks
     */
    private Group createTicks(double cx, double cy, double radius, int numberOfTicks, double startingAngle,
                              double overallAngle, double tickLength, String styleClass) {
        Group group = new Group();

        double degreesBetweenTicks = overallAngle == 360 ?
            overallAngle / numberOfTicks :
            overallAngle / (numberOfTicks - 1);
        double innerRadius = radius - tickLength;

        for (int i = 0; i < numberOfTicks; i++) {
            double angle = startingAngle + i * degreesBetweenTicks;

            Point2D startPoint = pointOnCircle(cx, cy, radius, angle);
            Point2D endPoint = pointOnCircle(cx, cy, innerRadius, angle);

            Line tick = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
            tick.getStyleClass().add(styleClass);
            group.getChildren().add(tick);
        }

        return group;
    }

    private String colorToCss(final Color color) {
        return color.toString().replace("0x", "#");
    }


    // compute sizes

    @Override
    protected double computeMinWidth(double height) {
        Insets padding = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return MINIMUM_WIDTH + horizontalPadding;
    }

    @Override
    protected double computeMinHeight(double width) {
        Insets padding = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return MINIMUM_HEIGHT + verticalPadding;
    }

    @Override
    protected double computePrefWidth(double height) {
        Insets padding = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return ARTBOARD_WIDTH + horizontalPadding;
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets padding = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return ARTBOARD_HEIGHT + verticalPadding;
    }

    // GETTER & SETTER

    // ToDo: ersetzen durch die Getter und Setter Ihres CustomControls


    public double getPeakValue() {
        return peakValue.get();
    }

    public DoubleProperty peakValueProperty() {
        return peakValue;
    }

    public void setPeakValue(double peakValue) {
        this.peakValue.set(peakValue);
    }

    public Line getPeakBackgroundLine() {
        return peakBackgroundLine;
    }

    public void setPeakBackgroundLine(Line peakBackgroundLine) {
        this.peakBackgroundLine = peakBackgroundLine;
    }

    public Line getPeakValueLine() {
        return peakValueLine;
    }

    public void setPeakValueLine(Line peakValueLine) {
        this.peakValueLine = peakValueLine;
    }

    public Circle getPeakThumb() {
        return peakThumb;
    }

    public void setPeakThumb(Circle peakThumb) {
        this.peakThumb = peakThumb;
    }

    public Text getDisplayPeakHeight() {
        return displayPeakHeight;
    }

    public void setDisplayPeakHeight(Text displayPeakHeight) {
        this.displayPeakHeight = displayPeakHeight;
    }

    public Pane getDrawingPane() {
        return drawingPane;
    }

    public void setDrawingPane(Pane drawingPane) {
        this.drawingPane = drawingPane;
    }

    public Text getDisplayPeakLabel() {
        return displayPeakLabel;
    }

    public void setDisplayPeakLabel(Text displayPeakLabel) {
        this.displayPeakLabel = displayPeakLabel;
    }

    public Line getSchartenBackgroundLine() {
        return schartenBackgroundLine;
    }

    public void setSchartenBackgroundLine(Line schartenBackgroundLine) {
        this.schartenBackgroundLine = schartenBackgroundLine;
    }

    public Line getSchartenValueLine() {
        return schartenValueLine;
    }

    public void setSchartenValueLine(Line schartenValueLine) {
        this.schartenValueLine = schartenValueLine;
    }

    public Circle getSchartenThumb() {
        return schartenThumb;
    }

    public void setSchartenThumb(Circle schartenThumb) {
        this.schartenThumb = schartenThumb;
    }

    public Text getDisplaySchartenHeight() {
        return displaySchartenHeight;
    }

    public void setDisplaySchartenHeight(Text displaySchartenHeight) {
        this.displaySchartenHeight = displaySchartenHeight;
    }

    public Text getDisplaySchartenLabel() {
        return displaySchartenLabel;
    }

    public void setDisplaySchartenLabel(Text displaySchartenLabel) {
        this.displaySchartenLabel = displaySchartenLabel;
    }

    public double getSchartenValue() {
        return schartenValue.get();
    }

    public DoubleProperty schartenValueProperty() {
        return schartenValue;
    }

    public void setSchartenValue(double schartenValue) {
        this.schartenValue.set(schartenValue);
    }

    public Line getDistanceBackgroundLine() {
        return distanceBackgroundLine;
    }

    public void setDistanceBackgroundLine(Line distanceBackgroundLine) {
        this.distanceBackgroundLine = distanceBackgroundLine;
    }

    public Line getDistanceValueLine() {
        return distanceValueLine;
    }

    public void setDistanceValueLine(Line distanceValueLine) {
        this.distanceValueLine = distanceValueLine;
    }

    public Circle getDistanceThumb() {
        return distanceThumb;
    }

    public void setDistanceThumb(Circle distanceThumb) {
        this.distanceThumb = distanceThumb;
    }

    public Text getDisplayDistanceHeight() {
        return displayDistanceHeight;
    }

    public void setDisplayDistanceHeight(Text displayDistanceHeight) {
        this.displayDistanceHeight = displayDistanceHeight;
    }

    public Text getDisplayDistanceLabel() {
        return displayDistanceLabel;
    }

    public void setDisplayDistanceLabel(Text displayDistanceLabel) {
        this.displayDistanceLabel = displayDistanceLabel;
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

    public boolean isOn() {
        return on.get();
    }

    public BooleanProperty onProperty() {
        return on;
    }

    public void setOn(boolean on) {
        this.on.set(on);
    }
}