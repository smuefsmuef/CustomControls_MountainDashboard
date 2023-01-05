package cuie.project.template_simplecontrol.demo;

import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import cuie.project.template_simplecontrol.MountainControl;

public class DemoPane extends BorderPane {

    private final PresentationModel pm;

    // declare the custom control
    private MountainControl cc;

    // all controls
    private Slider      peakSlider;
    private Slider      schartenSlider;
    private Slider      distanceSlider;
    private ColorPicker colorPicker;

    public DemoPane(PresentationModel pm) {
        this.pm = pm;
        initializeControls();
        layoutControls();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        cc = new MountainControl();

        peakSlider = new Slider(0, 5000, 4357);
        peakSlider.setShowTickLabels(true);

        schartenSlider = new Slider(200, 2200, 895);
        schartenSlider.setShowTickLabels(true);

        distanceSlider = new Slider(5, 20, 7.2);
        distanceSlider.setShowTickLabels(true);


        //colorPicker = new ColorPicker();
    }

    private void layoutControls() {
        VBox controlPane = new VBox(
            new Label("Gipfelhöhe"), peakSlider,
            new Label("Schartenhöhe"), schartenSlider,
            new Label("Kilometer bis zum nächsten grösseren Berg"), distanceSlider
            //new Label("Farbpicker"), colorPicker
            );
        controlPane.setPadding(new Insets(0, 50, 0, 50));
        controlPane.setSpacing(10);

        setCenter(cc);
        setRight(controlPane);
    }

    private void setupBindings() {
        //slider.valueProperty().bindBidirectional(pm.pmValueProperty());
       // colorPicker.valueProperty().bindBidirectional(pm.baseColorProperty());
       // cc.baseColorProperty().bindBidirectional(pm.baseColorProperty());

        peakSlider.valueProperty().bindBidirectional(pm.peakValueProperty());
        schartenSlider.valueProperty().bindBidirectional(pm.schartenValueProperty());
        distanceSlider.valueProperty().bindBidirectional(pm.distanceValueProperty());


        cc.schartenValueProperty().bindBidirectional(pm.schartenValueProperty());
        cc.peakValueProperty().bindBidirectional(pm.peakValueProperty());
        cc.distanceValueProperty().bindBidirectional(pm.distanceValueProperty());

    }

}
