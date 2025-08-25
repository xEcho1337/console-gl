package net.echo.consolegl.render;

import net.echo.consolegl.CGL;
import net.echo.consolegl.api.IMatrix4;
import net.echo.consolegl.api.IRenderStack;
import net.echo.consolegl.model.*;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class RenderStack implements AutoCloseable, IRenderStack {

    private final Deque<IMatrix4> transformStack = new ArrayDeque<>();
    private final List<Vertex2D> vertices;
    private final ConsoleBuffer buffer;
    private final int drawMode;
    private final int width;
    private final int height;

    private char glyph = CGL.GLYPHS[4];
    private Color color = Color.WHITE;

    public RenderStack(ConsoleBuffer buffer, int mode, int width, int height) {
        this.buffer = buffer;
        this.drawMode = mode;
        this.width = width;
        this.height = height;
        this.vertices = new ArrayList<>();
        transformStack.push(Matrix4.identity());
    }

    @Override
    public void pushMatrix() {
        transformStack.push(transformStack.peek().copy());
    }

    @Override
    public void popMatrix() {
        if (transformStack.isEmpty()) {
            throw new IllegalStateException("Cannot pop identity matrix");
        }

        transformStack.pop();
    }

    @Override
    public void glyph(char value) {
        this.glyph = value;
    }

    @Override
    public void color(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
    }

    @Override
    public void color(Color color) {
        this.color = color;
    }

    @Override
    public void vertex(double x, double y) {
        int scaledWidth = (int) (x * width);
        int scaledHeight = (int) (y * height);
        
        if (scaledWidth >= width || x < 0 || scaledHeight >= height || y < 0) {
            switch (CGL.getVertexPolicy()) {
                case ERROR -> throw new IllegalArgumentException("X and Y must be between 0 and 1! Got " + x + ", " + y);
                case IGNORE -> { return; }
            }
        }
        
        vertices.add(new Vertex2D(scaledWidth, scaledHeight, glyph, color));
    }

    @Override
    public void vertex(double x, double y, double z) {
        if (drawMode == -1) {
            throw new IllegalStateException("Draw mode has not been set, make sure to call 'begin' first");
        }

        Vertex3D v = new Vertex3D(x, y, z);
        v = transformStack.peek().transform(v);

        double zClamped = Math.max(v.z(), 0.01);
        double px = (v.x() / zClamped);
        double py = (v.y() / zClamped);

        double nx = (px + 1) / 2.0;
        double ny = (py + 1) / 2.0;

        vertex(nx, ny);
    }

    @Override
    public void translate(double tx, double ty, double tz) {
        transformStack.peek().translate(tx, ty, tz);
    }

    @Override
    public void scale(double sx, double sy, double sz) {
        transformStack.peek().scale(sx, sy, sz);
    }

    @Override
    public void rotateX(double angle) {
        transformStack.peek().rotateX(angle);
    }

    @Override
    public void rotateY(double angle) {
        transformStack.peek().rotateY(angle);
    }

    @Override
    public void rotateZ(double angle) {
        transformStack.peek().rotateZ(angle);
    }

    @Override
    public void close() {
        if (drawMode == -1) {
            throw new IllegalStateException("Unable to end draw, mode has not been set!");
        }

        Pixel[][] arrayBuffer = buffer.getBuffer();

        switch (drawMode) {
            case CGL.POINTS -> {
                for (Vertex2D vertex2D : vertices) {
                    Pixel pixel = arrayBuffer[vertex2D.y()][vertex2D.x()];
                    pixel.setGlyph(vertex2D.glyph());
                    pixel.setColor(vertex2D.color());
                }
            }
            case CGL.LINES -> { // LINES
                for (int i = 0; i < vertices.size(); i += 2) {
                    Vertex2D a = vertices.get(i);
                    Vertex2D b = vertices.get(i + 1);

                    CGL.rasterizeLine(arrayBuffer, a, b);
                }
            }
            case CGL.LINE_LOOP -> {
                Vertex2D lastVertex2D = vertices.getFirst();

                for (int i = 1; i < vertices.size(); i += 1) {
                    Vertex2D a = vertices.get(i);

                    CGL.rasterizeLine(arrayBuffer, a, lastVertex2D);
                    lastVertex2D = a;
                }

                CGL.rasterizeLine(arrayBuffer, lastVertex2D, vertices.getFirst());
            }
            case CGL.LINE_STRIP -> {
                Vertex2D lastVertex2D = vertices.getFirst();

                for (int i = 1; i < vertices.size(); i += 1) {
                    Vertex2D a = vertices.get(i);

                    CGL.rasterizeLine(arrayBuffer, a, lastVertex2D);
                    lastVertex2D = a;
                }
            }
            case CGL.TRIANGLES -> {
                for (int i = 0; i + 2 < vertices.size(); i += 3) {
                    Vertex2D a = vertices.get(i);
                    Vertex2D b = vertices.get(i+1);
                    Vertex2D c = vertices.get(i+2);

                    CGL.rasterizeTriangle(arrayBuffer, height, width, a, b, c);
                }
            }
            case CGL.QUADS -> {
                for (int i = 0; i + 3 < vertices.size(); i += 4) {
                    Vertex2D a = vertices.get(i);
                    Vertex2D b = vertices.get(i + 1);
                    Vertex2D c = vertices.get(i + 2);
                    Vertex2D d = vertices.get(i + 3);

                    CGL.rasterizeTriangle(arrayBuffer, height, width, a, b, c);
                    CGL.rasterizeTriangle(arrayBuffer, height, width, a, c, d);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + drawMode);
        }

        vertices.clear();
    }
}
