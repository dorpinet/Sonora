module com.dorpine {
    requires javafx.controls;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens com.dorpine.model to com.fasterxml.jackson.databind;

    exports com.dorpine;
    exports com.dorpine.api;
    exports com.dorpine.util;
    exports com.dorpine.ui.components;
    exports com.dorpine.ui.screens;
}
