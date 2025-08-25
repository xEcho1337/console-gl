package net.echo.consolegl;

import net.echo.consolegl.input.Display;
import net.echo.consolegl.render.ConsoleBuffer;
import net.echo.consolegl.render.RenderStack;
import net.echo.consolegl.input.Key;

public class TestWindow {

    public static final char[] GLYPHS = {'.', 'o', 'O', '@', '#'};

    public static void main(String[] args) throws Exception {
        new TestWindow().start();
    }

    private void start() throws Exception {
        Display display = new Display(320, 100);

        display.setTitle("Test Window");
        display.create();

        spinningDonut(display);
    }

    private void spinningDonut(Display display) {
        double A = 0, B = 0;
        int R1 = 10, R2 = 20, K2 = 30;
        double K1 = display.getWidth() * K2 * 3.0 / (8.0 * (R1 + R2));
        int distanceFromCamera = 50;

        int width = display.getWidth();
        int height = display.getHeight();

        try {
            while (display.isActive()) {
                ConsoleBuffer buffer = display.getFrameBuffer();
                buffer.clear();

                display.pollEvents();

                Key key = display.getPressedKey();

                if (key == Key.ESCAPE) {
                    display.setActive(false);
                    return;
                }

                double[][] zBuffer = new double[height][width];

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        zBuffer[y][x] = 0;
                    }
                }

                try (RenderStack stack = buffer.newStack(CGL.POINTS)) {
                    for (double theta = 0; theta < 2 * Math.PI; theta += 0.07) {
                        for (double phi = 0; phi < 2 * Math.PI; phi += 0.02) {
                            double cosTheta = Math.cos(theta), sinTheta = Math.sin(theta);
                            double cosPhi = Math.cos(phi), sinPhi = Math.sin(phi);

                            double circleX = R2 + R1 * cosTheta;
                            double circleY = R1 * sinTheta;

                            double x = circleX * (Math.cos(B) * cosPhi + Math.sin(A) * Math.sin(B) * sinPhi) - circleY * Math.cos(A) * Math.sin(B);
                            double y = circleX * (Math.sin(B) * cosPhi - Math.sin(A) * Math.cos(B) * sinPhi) + circleY * Math.cos(A) * Math.cos(B);
                            double z = K2 + Math.cos(A) * circleX * sinPhi + circleY * Math.sin(A);
                            double ooz = 1 / (z + distanceFromCamera);

                            int xp = (int) (width / 2.0 + K1 * ooz * x);
                            int yp = (int) (height / 2.0 - K1 * ooz * y);

                            double L = cosPhi * cosTheta * Math.sin(B) - Math.cos(A) * cosTheta * sinPhi - Math.sin(A) * sinTheta + Math.cos(B) * (Math.cos(A) * sinTheta - cosTheta * Math.sin(A) * sinPhi);
                            int luminanceIndex = (int) (8 * Math.max(L, 0));

                            xp = Math.clamp(xp, 0, width - 1);
                            yp = Math.clamp(yp, 0, height - 1);

                            if (ooz > zBuffer[yp][xp]) {
                                zBuffer[yp][xp] = ooz;

                                char c = GLYPHS[Math.min(luminanceIndex, GLYPHS.length - 1)];

                                stack.glyph(c);
                                stack.vertex((double) xp / width, (double) yp / height);
                            }
                        }
                    }
                }

                A += 0.07;
                B += 0.03;

                display.update(System.out);
            }
        } catch (Exception e) {
            display.setActive(false);
            e.printStackTrace(System.err);
        }
    }

    private void movingRect(Display display) throws InterruptedException {
        int frameRate = 120;
        int frameDelay = 1000 / frameRate;

        double size = 0.5;
        double half = size / 2;
        double posX = 0.5 - half;
        double posY = 0.5 - half;

        double step = 0.05;
        ConsoleBuffer frameBuffer = display.getFrameBuffer();

        while (display.isActive()) {
            Thread.sleep(frameDelay);
            display.pollEvents();

            Key key = display.getPressedKey();
            Key lastKey = display.getLastPressedKey();

            if (key == Key.ESCAPE) {
                display.setActive(false);
                return;
            }

            if (key == Key.W) posY -= step;
            if (key == Key.S) posY += step;
            if (key == Key.A) posX -= step;
            if (key == Key.D) posX += step;

            posX = Math.clamp(posX, 0, 1 - size);
            posY = Math.clamp(posY, 0, 1 - size);

            frameBuffer.clear();

            try (RenderStack stack = frameBuffer.newStack(CGL.QUADS)) {
                stack.color(255, 10, 10);
                stack.vertex(posX, posY);
                stack.vertex(posX + size, posY);
                stack.vertex(posX + size, posY + size);
                stack.vertex(posX, posY + size);
            }

            display.setTitle("Test Window - Pressed Key: " + lastKey.name());
            display.update(System.out);
        }
    }

    private void spinningCube(Display display) {
        int i = 0;

        double[][] verts = {
            {-0.5, -0.5, -0.5}, // V0
            { 0.5, -0.5, -0.5}, // V1
            { 0.5,  0.5, -0.5}, // V2
            {-0.5,  0.5, -0.5}, // V3
            {-0.5, -0.5,  0.5}, // V4
            { 0.5, -0.5,  0.5}, // V5
            { 0.5,  0.5,  0.5}, // V6
            {-0.5,  0.5,  0.5}  // V7
        };

        int[][] faces = {
            {0,1,2},{0,2,3}, {4,5,6},{4,6,7}, {0,3,7},{0,7,4},
            {1,2,6},{1,6,5}, {3,2,6},{3,6,7}, {0,1,5},{0,5,4}
        };

        int[][] colors = {
            {255,0,0},{0,255,0},{0,0,255},{255,255,0},{0,255,255},{255,0,255}
        };

        int frames = 0;
        long timePerFrame = 0;
        long timestamp = 0;
        double lastFps = 0;

        try {
            while (display.isActive()) {
                i++;

                long start = System.currentTimeMillis();
                double angle = Math.toRadians(i * 2);

                ConsoleBuffer frameBuffer = display.getFrameBuffer();

                frameBuffer.clear();
                display.pollEvents();

                Key key = display.getPressedKey();

                if (key == Key.ESCAPE) {
                    display.setActive(false);
                    return;
                }

                try (RenderStack stack = frameBuffer.newStack(CGL.TRIANGLES)) {
                    stack.glyph('#');
                    stack.pushMatrix();
                    stack.translate(0, 0, 1.5);
                    stack.rotateY(angle);
                    stack.rotateX(angle * 0.5);

                    for (int f = 0; f < faces.length; f++) {
                        int faceIndex = f / 2;
                        stack.color(colors[faceIndex][0], colors[faceIndex][1], colors[faceIndex][2]);

                        int[] tri = faces[f];
                        stack.vertex(verts[tri[0]][0], verts[tri[0]][1], verts[tri[0]][2]);
                        stack.vertex(verts[tri[1]][0], verts[tri[1]][1], verts[tri[1]][2]);
                        stack.vertex(verts[tri[2]][0], verts[tri[2]][1], verts[tri[2]][2]);
                    }

                    stack.popMatrix();
                }

                if (System.currentTimeMillis() - timestamp > 1000) {
                    lastFps = (double) timePerFrame / frames;
                    timePerFrame = 0;
                    frames = 0;
                    timestamp = System.currentTimeMillis();
                }

                display.setTitle("Test Window - FPS: " + String.format("%.2f", lastFps));
                display.update(System.out);

                long end = System.currentTimeMillis();

                timePerFrame += (end - start);
                frames++;
            }
        } catch (Exception e) {
            display.setActive(false);
            e.printStackTrace(System.err);
        }
    }
}
