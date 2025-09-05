package com.my_program.rendering.CPU.Pipeline;

import com.my_program.rendering.*;
import com.my_program.rendering.CPU.Buffers.Buffers;
import com.my_program.rendering.CPU.Pipeline.Thread.*;

import java.util.Vector;
import java.util.concurrent.*;

public class PipelineProcessing {
    Vector<DrawableObject> objects;
    ForkJoinPool pool = new ForkJoinPool();

    public PipelineProcessing(Vector<DrawableObject> objects) {
        this.objects = objects;

    }

    public void createBuffers() {
        for (DrawableObject object : objects) {
            BuffersPreparation.addObject(object);
        }

        BuffersPreparation.preparation();
    }

    public void screenResize() {
        BuffersPreparation.resize();
    }

    public void processing(Matrix4D transformer) {
        BuffersPreparation.clearHeadBuffer();
        BuffersPreparation.clearPixels();
        BuffersPreparation.clearCounters();

        Buffers.transformMatrix = transformer;
        Buffers.numberVertexForTransform = Buffers.vertexNum;

        for (int i = 0; i < Buffers.vertexNum; i++) {
            pool.submit(new VertexTransformingThreads(
                    i,
                    Buffers.bufferVertices,
                    Buffers.bufferTransformedVertices,
                    Buffers.transformMatrix,
                    Buffers.numberVertexForTransform
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);


        for (int i = 0; i < Buffers.indexNum / 3; i++) {
            pool.submit(new PolygonFormationThreads(
                    i,
                    Buffers.bufferTransformedVertices,
                    Buffers.bufferPolygonsVertices,
                    Buffers.bufferUV,
                    Buffers.bufferPolygonsUV,
                    Buffers.vertexMaterial,
                    Buffers.polygonMaterial,
                    Buffers.bufferIndexes
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        for (int i = 0; i < Buffers.indexNum / 3; i++) {
            pool.submit(new ClippingProjectionThreads(
                    i,
                    Buffers.bufferPolygonsVertices,
                    Buffers.bufferPolygonsUV,
                    Buffers.polygonMaterial,
                    Buffers.clippingPolygon,
                    Buffers.clippingPolygonsUV,
                    Buffers.clippingPolygonMaterial,
                    Buffers.clippingCounter
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        int numOfVertex = Buffers.clippingCounter.get(); //like read from GPU

        for (int i = 0; i < numOfVertex * 3; i++) {
            pool.submit(new PerspectiveDivideAndViewportTransformThreads(
                    i,
                    Buffers.clippingPolygon,
                    Buffers.width,
                    Buffers.height
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        for (int i = 0; i < numOfVertex; i++) {
            pool.submit(new RasterizationThreads(
                    i,
                    Buffers.clippingPolygon,
                    Buffers.clippingPolygonsUV,
                    Buffers.clippingPolygonMaterial,
                    Buffers.textures,
                    Buffers.fragments,
                    Buffers.heads,
                    Buffers.pixelCounter,
                    Buffers.width,
                    Buffers.height
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        for (int i = 0; i < GS.getScreenWidth() * GS.getScreenHeight(); i++) {
            pool.submit(new FragmentProcessing(
                    i,
                    Buffers.fragments,
                    Buffers.heads,
                    Buffers.pixels
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        GS.pixels = Buffers.pixels; //like read from GPU

    }


}
