package net.echo.consolegl;

import net.echo.consolegl.render.ConsoleBuffer;
import net.echo.consolegl.render.RenderStack;

public class TestWindow {

    public static void main(String[] args) throws InterruptedException {
        new TestWindow().start();
    }

    private void start() throws InterruptedException {
        ConsoleBuffer buffer = new ConsoleBuffer(200, 50);
        loop(buffer);
    }

    private void loop(ConsoleBuffer buffer) throws InterruptedException {
        int i = 0;

        while (true) {
            Thread.sleep(1000 / 120);
            i++;

            buffer.clear();

            try (RenderStack stack = buffer.newStack()) {
                stack.begin(CGL.TRIANGLES);
                stack.glyph(CGL.GLYPHS[4]);
                stack.color(28, 213, 255);

                stack.pushMatrix();
                stack.translate(0, 0, 1.3);
                stack.rotateY(Math.toRadians(i));

                stack.vertex(0, 0.5, -0.5); // Vertex C
                stack.vertex(0.5, -0.5, -0.5); // Vertex B
                stack.vertex(-0.5, -0.5, -0.5); // Vertex A

                stack.popMatrix();
            }

            buffer.render(System.out);
        }
    }
}
