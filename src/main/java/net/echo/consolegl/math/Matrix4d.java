package net.echo.consolegl.math;

import net.echo.consolegl.api.Matrix4;

public class Matrix4d implements Matrix4 {

    private final double[][] values;

    public Matrix4d() {
        this.values = new double[4][4];
    }

    public static Matrix4d identity() {
        Matrix4d mat = new Matrix4d();

        for (int i = 0; i < 4; i++) {
            mat.values[i][i] = 1;
        }

        return mat;
    }

    @Override
    public Matrix4 copy() {
        Matrix4d mat = new Matrix4d();

        for (int i = 0; i < 4; i++) {
            System.arraycopy(this.values[i], 0, mat.values[i], 0, 4);
        }

        return mat;
    }

    @Override
    public Matrix4d multiply(Matrix4d other) {
        Matrix4d result = new Matrix4d();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0;

                for (int k = 0; k < 4; k++) {
                    sum += this.values[row][k] * other.values[k][col];
                }

                result.values[row][col] = sum;
            }
        }

        return result;
    }

    @Override
    public Vertex3D transform(Vertex3D v) {
        double[] vec = {v.x(), v.y(), v.z(), 1};
        double[] res = new double[4];

        for (int row = 0; row < 4; row++) {
            res[row] = 0;

            for (int col = 0; col < 4; col++) {
                res[row] += values[row][col] * vec[col];
            }
        }

        return new Vertex3D(res[0], res[1], res[2]);
    }

    @Override
    public void translate(double tx, double ty, double tz) {
        Matrix4d t = identity();
        t.values[0][3] = tx;
        t.values[1][3] = ty;
        t.values[2][3] = tz;
        set(multiply(t));
    }

    @Override
    public void scale(double sx, double sy, double sz) {
        Matrix4d s = identity();
        s.values[0][0] = sx;
        s.values[1][1] = sy;
        s.values[2][2] = sz;
        set(multiply(s));
    }

    @Override
    public void rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Matrix4d r = identity();

        r.values[1][1] = cos;
        r.values[1][2] = -sin;
        r.values[2][1] = sin;
        r.values[2][2] = cos;

        set(multiply(r));
    }

    @Override
    public void rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Matrix4d r = identity();

        r.values[0][0] = cos;
        r.values[0][2] = sin;
        r.values[2][0] = -sin;
        r.values[2][2] = cos;

        set(multiply(r));
    }

    @Override
    public void rotateZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Matrix4d r = identity();

        r.values[0][0] = cos;
        r.values[0][1] = -sin;
        r.values[1][0] = sin;
        r.values[1][1] = cos;

        set(multiply(r));
    }

    @Override
    public void set(Matrix4d other) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(other.values[i], 0, values[i], 0, 4);
        }
    }
}
