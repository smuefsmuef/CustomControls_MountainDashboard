package cuie.project.template_businesscontrol;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

//todo: umbenennen
public class BusinessControl extends Control {
    private static final Locale CH = new Locale("de", "CH");

    private static final PseudoClass MANDATORY_CLASS = PseudoClass.getPseudoClass("mandatory");
    private static final PseudoClass INVALID_CLASS   = PseudoClass.getPseudoClass("invalid");


    //todo: durch die eigenen regulaeren Ausdruecke ersetzen
    static final String FORMATTED_INTEGER_PATTERN = "%,d";

    private static final String INTEGER_REGEX    = "[+-]?[\\d'’]{1,14}";
    private static final Pattern INTEGER_PATTERN = Pattern.compile(INTEGER_REGEX);

    //todo: Integer bei Bedarf ersetzen
    private final IntegerProperty value       = new SimpleIntegerProperty();
    private final StringProperty  valueAsText = new SimpleStringProperty();

    private final BooleanProperty mandatory = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(MANDATORY_CLASS, get());
        }
    };

    private final BooleanProperty invalid = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(INVALID_CLASS, get());
        }
    };

    //todo: ergaenzen um convertible

    private final BooleanProperty readOnly     = new SimpleBooleanProperty();
    private final StringProperty  label        = new SimpleStringProperty();
    private final StringProperty  errorMessage = new SimpleStringProperty();


    public BusinessControl() {
        initializeSelf();
        addValueChangeListener();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BusinessSkin(this);
    }

    public void reset() {
        setValueAsText(convertToString(getValue()));
    }

    public void increase() {
        setValue(getValue() + 1);
    }

    public void decrease() {
        setValue(getValue() - 1);
    }

    private void initializeSelf() {
         getStyleClass().add("business-control");

         setValueAsText(convertToString(getValue()));
    }

    //todo: durch geeignete Konvertierungslogik ersetzen
    private void addValueChangeListener() {
        valueAsText.addListener((observable, oldValue, userInput) -> {
            if (isMandatory() && (userInput == null || userInput.isEmpty())) {
                setInvalid(true);
                setErrorMessage("Mandatory Field");
                return;
            }

            if (isInteger(userInput)) {
                setInvalid(false);
                setErrorMessage(null);
                setValue(convertToInt(userInput));
            } else {
                setInvalid(true);
                setErrorMessage("Not an Integer");
            }
        });

        valueProperty().addListener((observable, oldValue, newValue) -> {
            setInvalid(false);
            setErrorMessage(null);
            Platform.runLater(() -> setValueAsText(convertToString(newValue.intValue())));
        });
    }

    //todo: Forgiving Format implementieren

    public void loadFonts(String... font){
        for(String f : font){
            Font.loadFont(getClass().getResourceAsStream(f), 0);
        }
    }

    public void addStylesheetFiles(String... stylesheetFile){
        for(String file : stylesheetFile){
            String stylesheet = getClass().getResource(file).toExternalForm();
            getStylesheets().add(stylesheet);
        }
    }

    private boolean isInteger(String userInput) {
        return INTEGER_PATTERN.matcher(userInput).matches();
    }

    private int convertToInt(String userInput) {
        try {
            return NumberFormat.getInstance(CH).parse(userInput).intValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String convertToString(int newValue) {
        return String.format(CH, FORMATTED_INTEGER_PATTERN, newValue);
    }


    // alle  Getter und Setter
    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    public boolean isReadOnly() {
        return readOnly.get();
    }

    public BooleanProperty readOnlyProperty() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly.set(readOnly);
    }

    public boolean isMandatory() {
        return mandatory.get();
    }

    public BooleanProperty mandatoryProperty() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory.set(mandatory);
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public boolean getInvalid() {
        return invalid.get();
    }

    public BooleanProperty invalidProperty() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid.set(invalid);
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    public String getValueAsText() {
        return valueAsText.get();
    }

    public StringProperty valueAsTextProperty() {
        return valueAsText;
    }

    public void setValueAsText(String valueAsText) {
        this.valueAsText.set(valueAsText);
    }

    public boolean isInvalid() {
        return invalid.get();
    }


}
