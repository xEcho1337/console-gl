package net.echo.consolegl.render;

import net.echo.consolegl.CGL;
import net.echo.consolegl.model.Pixel;

import java.awt.*;
import java.io.PrintStream;

import static net.echo.consolegl.CGL.GLYPHS;

public class ConsoleBuffer {

    private final StringBuilder sb;
    private final Pixel[][] buffer;
    private final int width;
    private final int height;
    private char defaultChar = 0;

    public ConsoleBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new Pixel[height][width];
        this.sb = new StringBuilder();

        fillEmpty(GLYPHS[0]);
    }

    public void fillEmpty(char defaultChar) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Pixel pixel = buffer[i][j];

                if (pixel == null) {
                    pixel = new Pixel(defaultChar, Color.WHITE);
                    buffer[i][j] = pixel;
                    continue;
                }

                if (pixel.getGlyph() == this.defaultChar) {
                    pixel.setGlyph(defaultChar);
                }
            }
        }

        this.defaultChar = defaultChar;
    }

    public Pixel[][] getBuffer() {
        return buffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String compute() {
        sb.setLength(0);

        for (Pixel[] row : buffer) {
            for (Pixel pixel : row) {
                sb.append(pixel.formatColor());
                sb.append(pixel.getGlyph());
                sb.append(CGL.RESET);
            }
            sb.append("\r\n");
        }

        return sb.toString();
    }

    public void render(String header, PrintStream out) {
        String text = header + compute();

        out.print("\033[H");
        out.flush();
        out.println(text);
    }

    public RenderStack newStack(int mode) {
        return new RenderStack(this, mode, width, height);
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = new Pixel(defaultChar, Color.WHITE);
            }
        }
    }
}
