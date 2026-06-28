module org.podcast_fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens org.podcast_fx.models to javafx.base, com.fasterxml.jackson.databind;
    opens org.podcast_fx to javafx.fxml;
    exports org.podcast_fx;
    exports org.podcast_fx.controllers;
    opens org.podcast_fx.controllers to javafx.fxml;

}