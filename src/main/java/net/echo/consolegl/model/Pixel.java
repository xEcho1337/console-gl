package net.echo.consolegl.model;

import java.awt.*;

public class Pixel {

    private char glyph;
    private Color color;

    public Pixel(char glyph, Color color) {
        this.glyph = glyph;
        this.color = color;
    }

    public char getGlyph() {
        return glyph;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String formatColor() {
        return "\033[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }
}
