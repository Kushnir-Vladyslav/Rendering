package com.my_program.rendering;

// весь функціонал перенесено до нового класу com.my_program.rendering.runRendering
// причиною є те що maven плагіни для збирання "fat jar" не працють коректно
public class Main {

    public static void main(String[] args) {
        runRendering.main(args);
    }
}
