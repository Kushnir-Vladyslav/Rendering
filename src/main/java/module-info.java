module Rendering {
    requires javafx.controls;
    requires javafx.fxml;


    requires java.desktop;
    requires jgltf.model;

//    opens com.my_program.rendering to javafx.fxml;
    exports com.my_program.rendering;
}