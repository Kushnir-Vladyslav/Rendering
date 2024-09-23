module Rendering {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.my_program.rendering to javafx.fxml;
    exports com.my_program.rendering;
}