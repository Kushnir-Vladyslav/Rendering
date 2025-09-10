package com.my_program.rendering.CPU.Pipeline;

public class PixelLinkedList {
    public int color;
    public float depth;
    public int nextIndex;

    public PixelLinkedList() {
        color = -1;
        depth = -1f;
        nextIndex = -1;
    }

    public PixelLinkedList(int color, float depth, int nextIndex) {
        this.color = color;
        this.depth = depth;
        this.nextIndex = nextIndex;
    }

}
