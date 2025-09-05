package com.my_program.rendering.CPU.Pipeline.Thread;

import java.util.concurrent.RecursiveAction;

public abstract class KernelCPU extends RecursiveAction  {
    protected int idWorkGroup;

    protected int threadNum = 0;
    public static final int POOL_SIZE = 256;

    @Override
    protected void compute() {
        for(threadNum = 0; threadNum < POOL_SIZE; threadNum++){
            thread();
        }
    }

    protected int get_global_id() {
        return idWorkGroup * POOL_SIZE + threadNum;
    }

    protected abstract void thread();
}
