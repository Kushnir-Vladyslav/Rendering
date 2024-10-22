module Rendering {
    requires javafx.controls;
    requires javafx.fxml;


    requires java.desktop;
    requires jgltf.model;

    requires org.lwjgl;
    requires org.lwjgl.opengl;
    requires org.lwjgl.glfw;

    exports com.my_program.rendering;
}