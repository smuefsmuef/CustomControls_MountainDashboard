package cuie.project.template_simplecontrol.demo;

import cuie.project.template_simplecontrol.MountainSliderControl;
import cuie.project.template_simplecontrol.ThemeControl;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import cuie.project.template_simplecontrol.MountainControl;

public class DemoPane extends BorderPane {

    private final PresentationModel pm;

    // declare the custom control
    private MountainControl cc;
    private MountainSliderControl mountainSliderControl;
    private ThemeControl themeControl;

    // all controls
    private TextField title;
    private Slider peakSlider;
    private Slider schartenSlider;
    private Slider distanceSlider;
    private CheckBox darkThemeOnBox;


    public DemoPane(PresentationModel pm) {
        this.pm = pm;
        initializeControls();
        layoutControls();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        title = new TextField();
        title.setText("Dent Blanche");

        cc = new MountainControl();
        mountainSliderControl = new MountainSliderControl();
        themeControl = new ThemeControl();

        peakSlider = new Slider(0, 5000, 4357);
        peakSlider.setShowTickLabels(true);

        schartenSlider = new Slider(200, 2200, 895);
        schartenSlider.setShowTickLabels(true);

        distanceSlider = new Slider(5, 20, 7.2);
        distanceSlider.setShowTickLabels(true);

        darkThemeOnBox = new CheckBox("Darktheme");

    }

    private void layoutControls() {
        VBox controlPane = new VBox(
            new Label("Name des Berges"), title,
            new Label("Gipfelhöhe"), peakSlider,
            new Label("Schartenhöhe"), schartenSlider,
            new Label("Dominanz"), distanceSlider, darkThemeOnBox
        );
        controlPane.setPadding(new Insets(0, 50, 0, 50));
        controlPane.setSpacing(10);

        VBox dashboard = new VBox(themeControl, mountainSliderControl);
        HBox test = new HBox(dashboard, cc);

        // todo problem resizing several....scheint nur zu funktionieren, wenn border pane in der mitte
        // setTop(dashboard);
        setCenter(test);
        setRight(controlPane);

    }

    private void setupBindings() {
        title.textProperty().bindBidirectional(pm.titleProperty());

        peakSlider.valueProperty().bindBidirectional(pm.peakValueProperty());
        schartenSlider.valueProperty().bindBidirectional(pm.schartenValueProperty());
        distanceSlider.valueProperty().bindBidirectional(pm.distanceValueProperty());
        darkThemeOnBox.selectedProperty().bindBidirectional(pm.onProperty());

        cc.titleValueProperty().bindBidirectional(pm.titleProperty());
        cc.schartenValueProperty().bindBidirectional(pm.schartenValueProperty());
        cc.peakValueProperty().bindBidirectional(pm.peakValueProperty());
        cc.distanceValueProperty().bindBidirectional(pm.distanceValueProperty());
        cc.onProperty().bindBidirectional(pm.onProperty());

        mountainSliderControl.peakValueProperty().bindBidirectional(pm.peakValueProperty());
        mountainSliderControl.schartenValueProperty().bindBidirectional(pm.schartenValueProperty());
        mountainSliderControl.distanceValueProperty().bindBidirectional(pm.distanceValueProperty());
        mountainSliderControl.onProperty().bindBidirectional(pm.onProperty());

        themeControl.onProperty().bindBidirectional(pm.onProperty());
    }

}
