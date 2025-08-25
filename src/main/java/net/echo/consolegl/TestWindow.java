package net.echo.consolegl;

import net.echo.consolegl.input.Display;
import net.echo.consolegl.model.VertexPolicy;
import net.echo.consolegl.render.ConsoleBuffer;
import net.echo.consolegl.render.RenderStack;
import net.echo.consolegl.input.Key;

public class TestWindow {

    public static void main(String[] args) throws Exception {
        new TestWindow().start();
    }

    private void start() throws Exception {
        CGL.setVertexPolicy(VertexPolicy.IGNORE);
        Display display = new Display(320, 100);

        display.setTitle("Test Window");
        display.create();

        spinningCube(display);
    }

    private void movingRect(Display display) throws InterruptedException {
        int frameRate = 120;
        int frameDelay = 1000 / frameRate;

        double size = 0.5;
        double half = size / 2;
        double posX = 0.5 - half;
        double posY = 0.5 - half;

        double step = 0.05;

        while (display.isActive()) {
            Thread.sleep(frameDelay);

            ConsoleBuffer frameBuffer = display.getFrameBuffer();
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
