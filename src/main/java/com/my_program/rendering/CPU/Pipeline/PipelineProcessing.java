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
        Buffers.numberVertexes = Buffers.vertexNum;

        for (int i = 0; i < Math.ceil((double) Buffers.vertexNum / VertexTransformingThreads.POOL_SIZE); i++) {
            pool.submit(new VertexTransformingThreads(
                    i,
                    Buffers.bufferVertices,
                    Buffers.bufferTransformedVertices,
                    Buffers.transformMatrix,
                    Buffers.numberVertexes
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        Buffers.numberVertexes = Buffers.indexNum / 3;

        for (int i = 0; i < Math.ceil((double) Buffers.indexNum / (3 * PolygonFormationThreads.POOL_SIZE)); i++) {
            pool.submit(new PolygonFormationThreads(
                    i,
                    Buffers.bufferTransformedVertices,
                    Buffers.bufferPolygonsVertices,
                    Buffers.bufferUV,
                    Buffers.bufferPolygonsUV,
                    Buffers.vertexMaterial,
                    Buffers.polygonMaterial,
                    Buffers.bufferIndexes,
                    Buffers.numberVertexes
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        for (int i = 0; i < Math.ceil((double) Buffers.indexNum / (3 * ClippingProjectionThreads.POOL_SIZE)); i++) {
            pool.submit(new ClippingProjectionThreads(
                    i,
                    Buffers.bufferPolygonsVertices,
                    Buffers.bufferPolygonsUV,
                    Buffers.polygonMaterial,
                    Buffers.clippingPolygon,
                    Buffers.clippingPolygonsUV,
                    Buffers.clippingPolygonMaterial,
                    Buffers.clippingCounter,
                    Buffers.numberVertexes
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        int numOfVertex = Buffers.clippingCounter.get(); //like read from GPU

        for (int i = 0; i < Math.ceil((double) numOfVertex * 3 / PerspectiveDivideAndViewportTransformThreads.POOL_SIZE); i++) {
            pool.submit(new PerspectiveDivideAndViewportTransformThreads(
                    i,
                    Buffers.clippingPolygon,
                    Buffers.width,
                    Buffers.height,
                    Buffers.clippingCounter
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        for (int i = 0; i < Math.ceil((double) numOfVertex / RasterizationThreads.POOL_SIZE); i++) {
            pool.submit(new RasterizationThreads(
                    i,
                    Buffers.clippingPolygon,
                    Buffers.clippingPolygonsUV,
                    Buffers.clippingPolygonMaterial,
                    Buffers.textures,
                    Buffers.fragmentsColor,
                    Buffers.fragmentsDepth,
                    Buffers.fragmentsNext,
                    Buffers.heads,
                    Buffers.pixelCounter,
                    Buffers.width,
                    Buffers.height,
                    Buffers.clippingCounter
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        for (int i = 0; i < Math.ceil((double) GS.getScreenWidth() * GS.getScreenHeight() / FragmentProcessing.POOL_SIZE); i++) {
            pool.submit(new FragmentProcessing(
                    i,
                    Buffers.fragmentsColor,
                    Buffers.fragmentsDepth,
                    Buffers.fragmentsNext,
                    Buffers.heads,
                    Buffers.pixels,
                    Buffers.width,
                    Buffers.height
            ));
        }

        pool.awaitQuiescence(5000, TimeUnit.MILLISECONDS);

        GS.pixels = Buffers.pixels; //like read from GPU

    }


}
