package cuie.project.mountain_dashboard;

import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Mit dem Mountain Control können Name des Berges, Dominanz, Gipfel- und Schartenhöhe eines
 * Berges dargestellt werden. Diese Grössen werden mit zwei Dreiecken stark vereinfacht grafisch simuliert:
 * mit einem Hauptberg links und dem nächst höheren Berg rechts. Eine absolute Skala der Gipfelhöhen mit
 * exemplarischen Bergen helfen die Grössen einzuordnen. Die Schartenhöhe und die Dominanz
 * können mit einem Klick auf die farbigen Punkte animiert werden.
 *
 * @author Karin Güdel & Petra Kohler
 */

public class MountainControl extends Region {

    private static final Locale CH = new Locale("de", "CH");

    private static final double ARTBOARD_WIDTH = 1000;
    private static final double ARTBOARD_HEIGHT = 700;

    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;

    private static final double MINIMUM_WIDTH = 500;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;

    private static final double MAXIMUM_WIDTH = 1500;

    private static final int INSET = 160;

    private Label titleDisplay;
    private Polygon mainMountain;
    private Polygon neighbourMountain;
    private Rectangle underground;

    private Line heightScale;
    private Label kilimandscharo;
    private Label matterhorn;
    private Label rigi;
    private Label zuerich;

    private Circle schartenCircle;
    private Circle distanceCircle;

    private Text displayPeakHeight;
    private Line peakHeightLine;
    private Text displaySchartenHeight;
    private Text displayDistance;

    private Text displaySchartenLabel;
    private Text displayPeakLabel;
    private Text displayDistanceLabel;

    private final StringProperty titleValue = new SimpleStringProperty("Dent Blanche");
    private final DoubleProperty peakValue = new SimpleDoubleProperty(4357);
    private final DoubleProperty schartenValue = new SimpleDoubleProperty(895);
    private final DoubleProperty distanceValue = new SimpleDoubleProperty(7.2);
    private final BooleanProperty on = new SimpleBooleanProperty(true);

    private PathTransition schartenCircleTransition;
    private Path schartenPath;

    private PathTransition distanceCircleTransition;
    private Path distancePath;

    private Pane drawingPane;

    public MountainControl() {
        initializeSelf();
        initializeParts();
        initializeDrawingPane();
        initializeAnimations();
        layoutParts();
        setupEventHandlers();
        setupValueChangeListeners();
        setupBindings();
    }

    private void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/Lato/Lato-Reg.ttf");
        addStylesheetFiles("style.css");

        //init
        if (!isOn()) {
            getStyleClass().add("mountain-dark-control");
        } else {
            getStyleClass().add("mountain-control");
        }
    }

    private void initializeParts() {
        double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
        double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
        double neighbourPeakHeight = peakHeight - 30;
        double distance = getDistanceValue() * 10 * 2;

        titleDisplay = new Label(getTitleValue());
        titleDisplay.setLayoutX(170);
        titleDisplay.setLayoutY(0);
        titleDisplay.getStyleClass().add("title");


        mainMountain = new Polygon();
        mainMountain.getPoints().setAll(
            400.0, schartenHeight,
            600.0, schartenHeight,
            500.0, peakHeight
        );
        mainMountain.getStyleClass().add("main-mountain");

        neighbourMountain = new Polygon();
        neighbourMountain.getPoints().setAll(
            600.0, schartenHeight,
            1000.0, schartenHeight,
            500.0 + distance+0.5*getDistanceValue(), (neighbourPeakHeight)
        );
        neighbourMountain.getStyleClass().add("neighbour-mountain");

        underground = new Rectangle(INSET, schartenHeight, 1000 - 160, 700 - schartenHeight);
        underground.getStyleClass().add("neighbour-mountain");

        heightScale = new Line(160, 0, 160, 700);
        heightScale.getStyleClass().add("height-scale");

        kilimandscharo = new Label("Kilimandscharo  \n---\n5895  ");
        kilimandscharo.setLayoutX(43);
        kilimandscharo.setLayoutY(700 - 589 - 45);
        kilimandscharo.getStyleClass().add("scale-label");

        matterhorn = new Label("Matterhorn  \n---\n4478  ");
        matterhorn.setLayoutX(72);
        matterhorn.setLayoutY(700 - 448 - 30);
        matterhorn.getStyleClass().add("scale-label");

        zuerich = new Label("Zürich  \n---\n403  ");
        zuerich.setLayoutX(107);
        zuerich.setLayoutY(700 - 40 - 28);
        zuerich.getStyleClass().add("scale-label");

        rigi = new Label("Rigi  \n---\n1798  ");
        rigi.setLayoutX(117);
        rigi.setLayoutY(700 - 179 - 30);
        rigi.getStyleClass().add("scale-label");

        schartenCircle = new Circle(500, schartenHeight, 15);
        schartenCircle.getStyleClass().add("scharten-circle");
        schartenCircle.setOpacity(0.7);

        distanceCircle =
            new Circle(500, peakHeight, 15);
        distanceCircle.getStyleClass().add("distance-circle");
        distanceCircle.setOpacity(0.7);

        displayPeakLabel = new Text(175, peakHeight - 10, "Gipfel [m.ü.M.]");
        displayPeakLabel.getStyleClass().add("label-peak-height");

        displayPeakHeight = new Text(175, peakHeight + 20, "" + (getPeakValue()));
        displayPeakHeight.getStyleClass().add("display-peak-height");

        peakHeightLine = new Line(240, peakHeight, 500, peakHeight);
        peakHeightLine.getStyleClass().add("peak-height-line");

        displaySchartenLabel =
            new Text(310, ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10) / 2) - 10,
                "Schartenhöhe [m]");
        displaySchartenLabel.getStyleClass().add("scharten-height-label");
        displaySchartenLabel.setOpacity(0.0);

        displaySchartenHeight =
            new Text(310, ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10) / 2) + 10,
                ("" + getSchartenValue()));
        displaySchartenHeight.getStyleClass().add("display-scharten-height");
        displaySchartenHeight.setOpacity(0.0);

        displayDistanceLabel = new Text(500.0 + distance / 2 - 40, neighbourPeakHeight - 20, "Dominanz [km]");
        displayDistanceLabel.getStyleClass().add("distance-label");
        displayDistanceLabel.setOpacity(0.0);

        displayDistance = new Text("");
        displayDistance.setX(500.0 + distance / 2 - 40);
        displayDistance.setY(neighbourPeakHeight);
        displayDistance.getStyleClass().add("display-distance");
        displayDistance.setOpacity(0.0);
    }

    private void initializeDrawingPane() {
        drawingPane = new Pane();
        drawingPane.getStyleClass().add("drawing-pane");
        drawingPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
    }

    private void initializeAnimations() {
        double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
        double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
        double distance = getDistanceValue() * 10 * 2;

        schartenPath = new Path();
        schartenPath.getElements().add(new MoveTo(500, schartenHeight));
        schartenPath.getElements().add(new LineTo(500.0, peakHeight));
        schartenPath.getStyleClass().add("scharten-path");

        distancePath = new Path(new MoveTo(500, peakHeight),
            new LineTo(500.0 + distance, peakHeight));
        distancePath.getStyleClass().add("distance-path");
    }

    private void layoutParts() {
        drawingPane.getChildren().addAll(
            distancePath,
            titleDisplay, mainMountain, neighbourMountain, underground,
            heightScale, kilimandscharo, matterhorn, rigi, zuerich,
            peakHeightLine, schartenPath, schartenCircle, distanceCircle,
            displaySchartenHeight, displayPeakHeight, displayDistance,
            displaySchartenLabel, displayPeakLabel, displayDistanceLabel);

        getChildren().add(drawingPane);
    }

    private void setupEventHandlers() {
        schartenCircle.setOnMouseClicked(event -> {
            if (schartenCircleTransition == null) {
                schartenCircleTransition = new PathTransition(Duration.seconds(1), schartenPath, schartenCircle);
                schartenCircleTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                schartenCircleTransition.setCycleCount(4);
                schartenCircleTransition.setAutoReverse(true);
            }

            if (!schartenCircleTransition.getStatus().equals(Animation.Status.RUNNING)) {
                schartenCircleTransition.play();
                displaySchartenHeight.setOpacity(1);
                displaySchartenLabel.setOpacity(1.0);
            }
        });

        distanceCircle.setOnMouseClicked(event -> {
            if (distanceCircleTransition == null) {
                distanceCircleTransition = new PathTransition(Duration.seconds(1), distancePath, distanceCircle);
                distanceCircleTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                distanceCircleTransition.setCycleCount(4);
                distanceCircleTransition.setAutoReverse(true);
            }

            if (!distanceCircleTransition.getStatus().equals(Animation.Status.RUNNING)) {
                distanceCircleTransition.play();

            }
            displayDistance.setOpacity(1.0);
            displayDistanceLabel.setOpacity(1.0);
        });

        onProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(true)) {
                getStyleClass().remove("mountain-control");
                getStyleClass().add("mountain-dark-control");

            } else {
                getStyleClass().remove("mountain-dark-control");
                getStyleClass().add("mountain-control");
            }
        });
    }

    private void setupValueChangeListeners() {

        peakValueProperty().addListener((observable, oldValue, newValue) ->
        {
            double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
            double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
            double neighbourPeakHeight = peakHeight - 30;
            double distance = getDistanceValue() * 10 * 2;

            mainMountain.getPoints().clear();
            mainMountain.getPoints().addAll(
                400.0, schartenHeight,
                600.0, schartenHeight,
                500.0, peakHeight
            );

            neighbourMountain.getPoints().clear();
            neighbourMountain.getPoints().addAll(
                600.0, schartenHeight,
                1000.0, schartenHeight,
                500.0 + distance+2*getDistanceValue(), neighbourPeakHeight
            );

            underground.setY(schartenHeight);
            underground.setHeight(700 - schartenHeight);

            displayPeakHeight.setY(peakHeight + 20);
            displaySchartenHeight.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10) / 2) + 10);

            displayDistance.setX(500.0 + distance / 2 - 40);
            displayDistance.setY(neighbourPeakHeight);

            displayPeakLabel.setY(peakHeight - 10);
            displaySchartenLabel.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10) / 2) - 10);
            displayDistanceLabel.setY(neighbourPeakHeight - 20);

            peakHeightLine.setStartY(peakHeight);
            peakHeightLine.setEndY(peakHeight);

            schartenCircle.setCenterX(500);
            schartenCircle.setCenterY(schartenHeight);

            schartenPath.getElements().clear();
            schartenPath.getElements().add(new MoveTo(500, schartenHeight));
            schartenPath.getElements().add(new LineTo(500.0, peakHeight));

            displaySchartenHeight.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10) / 2) + 10);

            distanceCircle.setCenterX(500);
            distanceCircle.setCenterY(peakHeight);

            distancePath.getElements().clear();
            distancePath.getElements().add(new MoveTo(500, peakHeight));
            distancePath.getElements().add(new LineTo(500.0 + distance, peakHeight));
        });

        schartenValueProperty().addListener((observable, oldValue, newValue) ->
        {
            double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
            double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
            double distance = getDistanceValue() * 10 * 2 ;
            double neighbourPeakHeight = peakHeight - 30;

            mainMountain.getPoints().clear();
            mainMountain.getPoints().addAll(
                400.0, schartenHeight,
                600.0, schartenHeight,
                500.0, ARTBOARD_HEIGHT - getPeakValue() / 10
            );

            neighbourMountain.getPoints().clear();
            neighbourMountain.getPoints().addAll(
                600.0, schartenHeight,
                1000.0, schartenHeight,
                500.0 + distance+2*getDistanceValue(), neighbourPeakHeight
            );

            underground.setY(schartenHeight);
            underground.setHeight(700 - schartenHeight);

            displaySchartenHeight.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10) / 2) + 10);
            displaySchartenLabel.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10) / 2) - 10);

            schartenCircle.setCenterX(500);
            schartenCircle.setCenterY(schartenHeight);

            schartenPath.getElements().clear();
            schartenPath.getElements().add(new MoveTo(500, schartenHeight));
            schartenPath.getElements().add(new LineTo(500.0, peakHeight));
        });


        distanceValueProperty().addListener((observable, oldValue, newValue) ->
        {
            double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
            double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
            double neighbourPeakHeight = peakHeight - 30;
            double distance = getDistanceValue() * 10 * 2;

            neighbourMountain.getPoints().clear();
            neighbourMountain.getPoints().addAll(
                600.0, schartenHeight,
                1000.0, schartenHeight,
                500.0 + distance+2*getDistanceValue(), neighbourPeakHeight
            );
            underground.setY(schartenHeight);
            underground.setHeight(700 - schartenHeight);

            displayDistance.setX(500.0 + distance / 2 - 40);
            displayDistance.setY(neighbourPeakHeight);

            displayDistanceLabel.setX(500.0 + distance / 2 - 40);
            displayDistanceLabel.setY(neighbourPeakHeight - 20);

            distanceCircle.setCenterX(500);
            distanceCircle.setCenterY(ARTBOARD_HEIGHT - (getPeakValue() / 10));

            distancePath.getElements().clear();
            distancePath.getElements().add(new MoveTo(500, peakHeight));
            distancePath.getElements().add(new LineTo(500.0 + distance, peakHeight));
        });
    }

    private void setupBindings() {
        titleDisplay.textProperty().bindBidirectional(titleValueProperty());
        displaySchartenHeight.textProperty().bind(schartenValueProperty().asString(CH, "%.0f"));
        displayPeakHeight.textProperty().bind(peakValueProperty().asString(CH, "%.0f"));
        displayDistance.textProperty().bind(distanceValueProperty().asString(CH, "%.1f"));
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

    // alle getter und setter  (generiert via "Code -> Generate... -> Getter and Setter)

    public Polygon getMainMountain() {
        return mainMountain;
    }

    public void setMainMountain(Polygon mainMountain) {
        this.mainMountain = mainMountain;
    }

    public Polygon getNeighbourMountain() {
        return neighbourMountain;
    }

    public void setNeighbourMountain(Polygon neighbourMountain) {
        this.neighbourMountain = neighbourMountain;
    }

    public Circle getSchartenCircle() {
        return schartenCircle;
    }

    public void setSchartenCircle(Circle schartenCircle) {
        this.schartenCircle = schartenCircle;
    }

    public Circle getDistanceCircle() {
        return distanceCircle;
    }

    public void setDistanceCircle(Circle distanceCircle) {
        this.distanceCircle = distanceCircle;
    }

    public Text getDisplaySchartenHeight() {
        return displaySchartenHeight;
    }

    public void setDisplaySchartenHeight(Text displaySchartenHeight) {
        this.displaySchartenHeight = displaySchartenHeight;
    }

    public Text getDisplayPeakHeight() {
        return displayPeakHeight;
    }

    public void setDisplayPeakHeight(Text displayPeakHeight) {
        this.displayPeakHeight = displayPeakHeight;
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


    public Text getDisplayDistance() {
        return displayDistance;
    }

    public void setDisplayDistance(Text displayDistance) {
        this.displayDistance = displayDistance;
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

    public String getTitleValue() {
        return titleValue.get();
    }

    public StringProperty titleValueProperty() {
        return titleValue;
    }

    public void setTitleValue(String titleValue) {
        this.titleValue.set(titleValue);
    }
}