package cuie.project.mountain_dashboard;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
Der ThemeControl steuert über einen einfachen an/aus-Mechanismus den Dark & Light Mode des gesamten Dashboards. *

 * @author Karin Güdel & Petra Kohler
 */

public class ThemeControl extends Region {

    private static final double ARTBOARD_WIDTH = 50;
    private static final double ARTBOARD_HEIGHT = 50;

    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;

    private static final double MINIMUM_WIDTH = 50;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;

    private static final double MAXIMUM_WIDTH = 1500; // check with karin to have the same value

    private Circle   circle;

    private final BooleanProperty on  = new SimpleBooleanProperty(true);

    private static final int ICON_SIZE  = 22;
    private static final int IMG_OFFSET = 4;

    private static ImageView lightIcon;

    private static ImageView darkIcon;


    // fuer Resizing benoetigt
    private Pane drawingPane;

    public ThemeControl() {
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
        circle = new Circle(ARTBOARD_WIDTH/2, ARTBOARD_HEIGHT/2, 39.5/2);
        circle.getStyleClass().add("button");
        circle.setStrokeWidth(0);

        lightIcon = new ImageView(new Image(ThemeControl.class.getResource("icons/sun.png").toExternalForm(),
            ICON_SIZE, ICON_SIZE,
            true, false));
        lightIcon.getStyleClass().add("test");

        darkIcon = new ImageView(new Image(ThemeControl.class.getResource("icons/moon.png").toExternalForm(),
            ICON_SIZE, ICON_SIZE,
            true, false));

        darkIcon.setOpacity(0.0);
        lightIcon.setOpacity(1);

        lightIcon.setX((ARTBOARD_WIDTH-ICON_SIZE)/2);
        lightIcon.setY((ARTBOARD_HEIGHT-ICON_SIZE)/2);
        darkIcon.setX((ARTBOARD_WIDTH-ICON_SIZE)/2);
        darkIcon.setY((ARTBOARD_HEIGHT-ICON_SIZE)/2);

        DropShadow bottomShadow = new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.3), 4/2, 0, 3/2, 2/2);
        DropShadow topShadow = new DropShadow(BlurType.GAUSSIAN, Color.rgb(255, 255, 255, 0.3), 4/2, 0, -3/2, -2/2);

        Blend blend = new Blend();
        blend.setMode(BlendMode.ADD);
        blend.setBottomInput(bottomShadow);
        blend.setTopInput(topShadow);

        circle.setEffect(blend);

    }

    private void initializeDrawingPane() {
        drawingPane = new Pane();
        drawingPane.getStyleClass().add("drawing-pane");
        drawingPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
    }


    private void layoutParts() {


        drawingPane.getChildren().addAll(circle, lightIcon, darkIcon);

        getChildren().add(drawingPane);
    }

    private void setupEventHandlers() {
        drawingPane.setOnMouseClicked(event -> setOn(!isOn()));
    }

    private void setupValueChangeListeners() {


        onProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.equals(true)) {
                lightIcon.setOpacity(0.0);
                darkIcon.setOpacity(1);

                getStyleClass().remove("mountain-slider-control");
                getStyleClass().add("mountain-slider-dark-control");

            } else {
                lightIcon.setOpacity(1.0);
                darkIcon.setOpacity(0.0);
                getStyleClass().remove("mountain-slider-dark-control");
                getStyleClass().add("mountain-slider-control");
            }
        });

    }

    private void setupBindings() {
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

    // GETTER & SETTER

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