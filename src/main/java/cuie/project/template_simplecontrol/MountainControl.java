package cuie.project.template_simplecontrol;

import java.util.List;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.util.Duration;

import static java.lang.String.valueOf;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.rgb;

/**
 * ToDo: CustomControl kurz beschreiben
 *
 * @author Karin Güdel & Petra Kohler
 */

public class MountainControl extends Region {
    // wird gebraucht fuer StyleableProperties
    private static final StyleablePropertyFactory<MountainControl> FACTORY =
        new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    private static final Locale CH = new Locale("de", "CH");

    private static final double ARTBOARD_WIDTH = 1000;
    private static final double ARTBOARD_HEIGHT = 700;

    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;

    private static final double MINIMUM_WIDTH = 500;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;

    private static final double MAXIMUM_WIDTH = 1500;

    private static final int INSET = 160;


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
    private Text displaySchartenHeight;
    private Text displayDistance;

    private Text displaySchartenLabel;
    private Text displayPeakLabel;
    private Text displayDistanceLabel;


    private final DoubleProperty peakValue = new SimpleDoubleProperty(4357);
    private final DoubleProperty schartenValue = new SimpleDoubleProperty(895);
    private final DoubleProperty distanceValue = new SimpleDoubleProperty(7.2);
    private final BooleanProperty on          = new SimpleBooleanProperty(true);

//    // ToDo: ergänzen mit allen CSS stylable properties
//    private static final CssMetaData<MountainControl, Color> BASE_COLOR_META_DATA =
//        FACTORY.createColorCssMetaData("-base-color", s -> s.baseColor);
//
//    private final StyleableObjectProperty<Color> baseColor =
//        new SimpleStyleableObjectProperty<Color>(BASE_COLOR_META_DATA) {
//            @Override
//            protected void invalidated() {
//                setStyle(String.format("%s: %s;", getCssMetaData().getProperty(), colorToCss(get())));
//                applyCss();
//            }
//        };

    // ToDo: Loeschen falls keine getaktete Animation benoetigt wird
    private final BooleanProperty blinking = new SimpleBooleanProperty(false);
    private final ObjectProperty<Duration> pulse = new SimpleObjectProperty<>(Duration.seconds(1.0));

    private final AnimationTimer timer = new AnimationTimer() {
        private long lastTimerCall;

        @Override
        public void handle(long now) {
            if (now > lastTimerCall + (getPulse().toMillis() * 1_000_000L)) {
                performPeriodicTask();
                lastTimerCall = now;
            }
        }
    };

    // ToDo: alle Animationen und Timelines deklarieren
    private PathTransition schartenCircleTransition;
    private Path schartenPath;

    private Path distancePath;
    private PathTransition distanceCircleTransition;



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
        if(!isOn()) {
            getStyleClass().add("mountain-dark-control");
        } else {
            getStyleClass().add("mountain-control");
        }
    }

    private void initializeParts() {
        double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
        double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
        double neighbourPeakHeight = peakHeight - 30;
        double distance = getDistanceValue() * 10 * 2 +20;

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
            (500.0 + distance), (neighbourPeakHeight)
        );
        neighbourMountain.getStyleClass().add("neighbour-mountain");

        underground = new Rectangle(INSET, schartenHeight, 1000-160, 700-schartenHeight);
        underground.getStyleClass().add("neighbour-mountain");

        heightScale = new Line(160, 0, 160, 700);
        heightScale.getStyleClass().add("height-scale");

        kilimandscharo = new Label( "Kilimandscharo  \n---\n5895  ");
        kilimandscharo.setLayoutX(43);
        kilimandscharo.setLayoutY(700-589-45);
        kilimandscharo.getStyleClass().add("scale-label");

        matterhorn = new Label( "Matterhorn  \n---\n4478  ");
        matterhorn.setLayoutX(72);
        matterhorn.setLayoutY(700-448-38);
        matterhorn.getStyleClass().add("scale-label");

        zuerich = new Label( "Zürich  \n---\n403  ");
        zuerich.setLayoutX(107);
        zuerich.setLayoutY(700-40-45);
        zuerich.getStyleClass().add("scale-label");

        rigi = new Label( "Rigi  \n---\n1798  ");
        rigi.setLayoutX(117);
        rigi.setLayoutY(700-179-45);
        rigi.getStyleClass().add("scale-label");

        schartenCircle = new Circle(500, peakHeight, 10);
        schartenCircle.getStyleClass().add("scharten-circle");

        distanceCircle =
            new Circle(500, peakHeight, 10);
        distanceCircle.getStyleClass().add("distance-circle");

        displayPeakLabel = new Text(170, peakHeight-20, "Gipfelhöhe");
        displayPeakLabel.getStyleClass().add("scale-label");

        displayPeakHeight = new Text(170, peakHeight, "" +(getPeakValue()));
        displayPeakHeight.getStyleClass().add("display-peak-height");

        displaySchartenLabel = new Text(350, ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10)/2)-20, "Schartenhöhe");
        displaySchartenLabel.getStyleClass().add("scale-label");

        displaySchartenHeight = new Text(350, ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10)/2),
            ( "" + getSchartenValue()));
        displaySchartenHeight.getStyleClass().add("display-scharten-height");
        displaySchartenHeight.setOpacity(0.0);

        displayDistanceLabel =  new Text(500.0 + distance/2, neighbourPeakHeight+20-20, "Dominanz");
        displayDistanceLabel.getStyleClass().add("scale-label");

        displayDistance = new Text("");
        displayDistance.setX(500.0 + distance/2);
        displayDistance.setY(neighbourPeakHeight+20);
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
        //ToDo: alle deklarierten Animationen initialisieren
        double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
        double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
        double distance = getDistanceValue() * 10 * 2 +20;

        schartenPath = new Path();
        schartenPath.getElements().add(new MoveTo(500, peakHeight));
        schartenPath.getElements().add(new LineTo(500.0, schartenHeight));
        schartenPath.setStroke(Color.DODGERBLUE);
        schartenPath.getStrokeDashArray().setAll(5d, 5d);

        distancePath = new Path(new MoveTo(500, peakHeight),
            new LineTo(500.0 + distance, peakHeight));
        distancePath.setStroke(Color.DODGERBLUE);
        distancePath.getStrokeDashArray().setAll(5d, 5d);

    }

    private void layoutParts() {
        //ToDo: alle Parts zur drawingPane hinzufügen
        drawingPane.getChildren().addAll(
            mainMountain, neighbourMountain, underground,
            heightScale, kilimandscharo, matterhorn, rigi, zuerich,
            schartenCircle, distanceCircle, schartenPath, distancePath,
            displaySchartenHeight, displayPeakHeight, displayDistance, displaySchartenLabel, displayPeakLabel, displayDistanceLabel);

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
                displaySchartenHeight.setOpacity(0.5);
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
        //ToDo: durch die Listener auf die Properties des Custom Controls ersetzen


        peakValueProperty().addListener((observable, oldValue, newValue) ->
        {
            double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
            double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
            double neighbourPeakHeight = peakHeight - 30;
            double distance = getDistanceValue() * 10 * 2 + 20;

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
                500.0 + distance, neighbourPeakHeight
            );

            underground.setY(schartenHeight);
            underground.setHeight(700-schartenHeight);

            displayPeakHeight.setY(peakHeight);
            displaySchartenHeight.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10)/2)-20);

            displayDistance.setX(500.0 + distance/2);
            displayDistance.setY(neighbourPeakHeight+20);

            displayPeakLabel.setY(peakHeight-20);
            displaySchartenLabel.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10)/2)-20);
            displayDistanceLabel.setY(neighbourPeakHeight);

            schartenCircle.setCenterX(500);
            schartenCircle.setCenterY(peakHeight);

            schartenPath.getElements().clear();
            schartenPath.getElements().add(new MoveTo(500, peakHeight));
            schartenPath.getElements().add(new LineTo(500.0, schartenHeight));

            displaySchartenHeight.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10)/2));

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
            double distance = getDistanceValue() * 10 * 2 + 20;
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
                500.0 + distance, neighbourPeakHeight
            );

            underground.setY(schartenHeight);
            underground.setHeight(700-schartenHeight);

            displaySchartenHeight.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10)/2));

            displaySchartenLabel.setY(ARTBOARD_HEIGHT - (getPeakValue() / 10 - (getSchartenValue() / 10)/2)-20);

            schartenCircle.setCenterX(500);
            schartenCircle.setCenterY(peakHeight);

            schartenPath.getElements().clear();
            schartenPath.getElements().add(new MoveTo(500, peakHeight));
            schartenPath.getElements().add(new LineTo(500.0, schartenHeight));
        });


        distanceValueProperty().addListener((observable, oldValue, newValue) ->
        {
            double schartenHeight = ARTBOARD_HEIGHT - (getPeakValue() / 10 - getSchartenValue() / 10);
            double peakHeight = ARTBOARD_HEIGHT - getPeakValue() / 10;
            double neighbourPeakHeight = peakHeight - 30;
            double distance = getDistanceValue() * 10 * 2 + 20;

            neighbourMountain.getPoints().clear();
            neighbourMountain.getPoints().addAll(
                600.0, schartenHeight,
                1000.0, schartenHeight,
                500.0 + distance, neighbourPeakHeight
            );
            underground.setY(schartenHeight);
            underground.setHeight(700-schartenHeight);

            displayDistance.setX(500.0 + distance/2);
            displayDistance.setY(neighbourPeakHeight+20);

            displayDistanceLabel.setX(500.0 + distance/2);
            displayDistanceLabel.setY(neighbourPeakHeight);

            distanceCircle.setCenterX(500);
            distanceCircle.setCenterY(ARTBOARD_HEIGHT - (getPeakValue() / 10));

            distancePath.getElements().clear();
            distancePath.getElements().add(new MoveTo(500, peakHeight));
            distancePath.getElements().add(new LineTo(500.0 + distance, peakHeight));
        });


        // fuer die getaktete Animation
        //blinking.addListener((observable, oldValue, newValue) -> startClockedAnimation(newValue));
    }

    private void setupBindings() {
        //ToDo: dieses Binding ersetzen
        displaySchartenHeight.textProperty().bind(schartenValueProperty().asString(CH, "%.1f"));
        displayPeakHeight.textProperty().bind(peakValueProperty().asString(CH, "%.1f"));
        displayDistance.textProperty().bind(distanceValueProperty().asString(CH, "%.1f"));
    }

    private void updateUI() {
        //ToDo : ergaenzen mit dem was bei einer Wertaenderung einer Status-Property im UI upgedated werden muss
    }

    private void performPeriodicTask() {
        //ToDo: ergaenzen mit dem was bei der getakteten Animation gemacht werden muss
        //normalerweise: den Wert einer der Status-Properties aendern
    }

    private void startClockedAnimation(boolean start) {
        if (start) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    //ToDo: ueberpruefen ob dieser Resizing-Ansatz anwendbar ist.
    private void resize() {
        Insets padding = getPadding();
        double availableWidth = getWidth() - padding.getLeft() - padding.getRight();
        double availableHeight = getHeight() - padding.getTop() - padding.getBottom();

        double width =
            Math.max(Math.min(Math.min(availableWidth, availableHeight * ASPECT_RATIO), MAXIMUM_WIDTH), MINIMUM_WIDTH);

        double scalingFactor = width / ARTBOARD_WIDTH;

        if (availableWidth > 0 && availableHeight > 0) {
            //ToDo: ueberpruefen ob die drawingPane immer zentriert werden soll (eventuell ist zum Beispiel linksbuendig angemessener)
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

    //ToDo: diese Funktionen anschauen und für die Umsetzung des CustomControls benutzen

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

    // alle getter und setter  (generiert via "Code -> Generate... -> Getter and Setter)

    // ToDo: ersetzen durch die Getter und Setter Ihres CustomControls


//    public Color getBaseColor() {
//        return baseColor.get();
//    }

//    public StyleableObjectProperty<Color> baseColorProperty() {
//        return baseColor;
//    }
//
//    public void setBaseColor(Color baseColor) {
//        this.baseColor.set(baseColor);
//    }

    public boolean isBlinking() {
        return blinking.get();
    }

    public BooleanProperty blinkingProperty() {
        return blinking;
    }

    public void setBlinking(boolean blinking) {
        this.blinking.set(blinking);
    }

    public Duration getPulse() {
        return pulse.get();
    }

    public ObjectProperty<Duration> pulseProperty() {
        return pulse;
    }

    public void setPulse(Duration pulse) {
        this.pulse.set(pulse);
    }


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

    public AnimationTimer getTimer() {
        return timer;
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
}