package net.echo.consolegl.api;

import net.echo.consolegl.model.Matrix4;
import net.echo.consolegl.model.Vertex3D;

/**
 * Represents a 4x4 transformation matrix for 3D operations.
 * <p>
 * This interface defines methods for creating and manipulating 3D transformation matrices,
 * including translation, scaling, rotation, and vertex transformation.
 * Implementations should provide efficient and consistent matrix operations suitable for 3D rendering.
 */
public interface IMatrix4 {

    /**
     * Creates a copy of this matrix.
     *
     * @return a new IMatrix4 instance with the same values as this matrix.
     */
    IMatrix4 copy();

    /**
     * Multiplies this matrix by another matrix.
     *
     * @param other the matrix to multiply with.
     * @return the result of the multiplication as a new Matrix4 object.
     */
    Matrix4 multiply(Matrix4 other);

    /**
     * Transforms a 3D vertex using this matrix.
     *
     * @param v the vertex to transform.
     * @return a new Vertex3D representing the transformed coordinates.
     */
    Vertex3D transform(Vertex3D v);

    /**
     * Applies a translation to this matrix.
     *
     * @param tx translation along the X-axis.
     * @param ty translation along the Y-axis.
     * @param tz translation along the Z-axis.
     */
    void translate(double tx, double ty, double tz);

    /**
     * Applies a scaling transformation to this matrix.
     *
     * @param sx scale factor along the X-axis.
     * @param sy scale factor along the Y-axis.
     * @param sz scale factor along the Z-axis.
     */
    void scale(double sx, double sy, double sz);

    /**
     * Applies a rotation around the X-axis.
     *
     * @param angle rotation angle in radians.
     */
    void rotateX(double angle);

    /**
     * Applies a rotation around the Y-axis.
     *
     * @param angle rotation angle in radians.
     */
    void rotateY(double angle);

    /**
     * Applies a rotation around the Z-axis.
     *
     * @param angle rotation angle in radians.
     */
    void rotateZ(double angle);

    /**
     * Sets this matrix to have the same values as another matrix.
     *
     * @param other the matrix whose values will be copied.
     */
    void set(Matrix4 other);
}
