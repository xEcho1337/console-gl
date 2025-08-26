package net.echo.consolegl.api;

import java.awt.*;

/**
 * Interface defining the API for a rendering stack, similar to OpenGL's
 * matrix and drawing stack. Allows pushing/popping matrices, setting
 * colors and glyphs, and issuing 2D/3D vertex commands.
 */
public interface RenderStack extends AutoCloseable {

    /**
     * Pushes the current transformation matrix onto the stack.
     * All subsequent transformations will be applied on top of this matrix.
     */
    void pushMatrix();

    /**
     * Pops the top transformation matrix from the stack,
     * restoring the previous matrix state.
     */
    void popMatrix();

    /**
     * Sets the glyph (character) used to render points or primitives.
     *
     * @param value The character to use for rendering.
     */
    void glyph(char value);

    /**
     * Sets the current color using RGB components.
     *
     * @param red   Red component (0-255)
     * @param green Green component (0-255)
     * @param blue  Blue component (0-255)
     */
    void color(int red, int green, int blue);

    /**
     * Sets the current color using a java.awt.Color object.
     *
     * @param color The color to use for rendering.
     */
    void color(Color color);

    /**
     * Adds a 2D vertex to the current primitive.
     * Coordinates are expected to be normalized between 0 and 1.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    void vertex(double x, double y);

    /**
     * Adds a 3D vertex to the current primitive.
     * Coordinates are normalized; depth (z) is used for perspective projection.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     */
    void vertex(double x, double y, double z);

    /**
     * Translates (moves) all subsequent vertices by the given offset.
     *
     * @param tx Offset along X-axis
     * @param ty Offset along Y-axis
     * @param tz Offset along Z-axis
     */
    void translate(double tx, double ty, double tz);

    /**
     * Scales all subsequent vertices by the given factors.
     *
     * @param sx Scale factor along X-axis
     * @param sy Scale factor along Y-axis
     * @param sz Scale factor along Z-axis
     */
    void scale(double sx, double sy, double sz);

    /**
     * Rotates all subsequent vertices around the X-axis.
     *
     * @param angle Rotation angle in radians
     */
    void rotateX(double angle);

    /**
     * Rotates all subsequent vertices around the Y-axis.
     *
     * @param angle Rotation angle in radians
     */
    void rotateY(double angle);

    /**
     * Rotates all subsequent vertices around the Z-axis.
     *
     * @param angle Rotation angle in radians
     */
    void rotateZ(double angle);

    @Override
    void close();
}
