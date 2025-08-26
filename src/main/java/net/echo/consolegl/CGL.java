package net.echo.consolegl;

import net.echo.consolegl.math.Pixel;
import net.echo.consolegl.math.Vertex2D;
import net.echo.consolegl.math.Vertex3D;
import net.echo.consolegl.api.VertexPolicy;

import java.util.stream.IntStream;

public class CGL {

    public static final char[] GLYPHS = {' ', '░', '▒', '▓', '█'};
    public static final int POINTS = 0;
    public static final int LINES = 1;
    public static final int LINE_LOOP = 2;
    public static final int LINE_STRIP = 3;
    public static final int TRIANGLES = 4;
    public static final int QUADS = 5;
    public static final String RESET = "\033[0m";
    private static VertexPolicy vertexPolicy = VertexPolicy.IGNORE;

    public static VertexPolicy getVertexPolicy() {
        return vertexPolicy;
    }

    public static void setVertexPolicy(VertexPolicy vertexPolicy) {
        CGL.vertexPolicy = vertexPolicy;
    }

    public static Vertex3D translate(Vertex3D v, double tx, double ty, double tz) {
        return new Vertex3D(v.x() + tx, v.y() + ty, v.z() + tz);
    }

    public static Vertex3D scale(Vertex3D v, double sx, double sy, double sz) {
        return new Vertex3D(v.x() * sx, v.y() * sy, v.z() * sz);
    }

    public static Vertex3D rotateX(Vertex3D v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.y() * cos - v.z() * sin;
        double z = v.y() * sin + v.z() * cos;
        return new Vertex3D(v.x(), y, z);
    }

    public static Vertex3D rotateY(Vertex3D v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.x() * cos + v.z() * sin;
        double z = -v.x() * sin + v.z() * cos;
        return new Vertex3D(x, v.y(), z);
    }

    public static Vertex3D rotateZ(Vertex3D v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.x() * cos - v.y() * sin;
        double y = v.x() * sin + v.y() * cos;
        return new Vertex3D(x, y, v.z());
    }

    public static void rasterizeLine(Pixel[][] buffer, Vertex2D a, Vertex2D b) {
        int x0 = a.x();
        int y0 = a.y();
        int x1 = b.x();
        int y1 = b.y();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            Pixel pixel = buffer[y0][x0];
            pixel.setGlyph(a.glyph());
            pixel.setColor(a.color());

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public static void rasterizeTriangle(Pixel[][] buffer, int height, int width, Vertex2D a, Vertex2D b, Vertex2D c) {
        IntStream.range(0, width * height)
            .parallel()
            .forEach(i -> {
                int y = i / width;
                int x = i % width;

                Vertex2D P = new Vertex2D(x, y, ' ', null);

                float w0 = edgeFunction(b, c, P);
                float w1 = edgeFunction(c, a, P);
                float w2 = edgeFunction(a, b, P);

                boolean allPositive = w0 >= 0 && w1 >= 0 && w2 >= 0;
                boolean allNegative = w0 <= 0 && w1 <= 0 && w2 <= 0;

                if (allPositive || allNegative) {
                    Pixel pixel = buffer[y][x];
                    pixel.setGlyph(a.glyph());
                    pixel.setColor(a.color());
                }
            });
    }

    public static float edgeFunction(Vertex2D a, Vertex2D b, Vertex2D c) {
        return (c.x() - a.x()) * (b.y() - a.y()) - (c.y() - a.y()) * (b.x() - a.x());
    }
}
