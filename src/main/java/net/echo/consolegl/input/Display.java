package net.echo.consolegl.input;

import net.echo.consolegl.render.ConsoleBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Display {

    public record Size(int width, int height) {
        @Override
        public String toString() {
            return "(" + width + ", " + height + ")";
        }
    }

    private final int width;
    private final int height;
    private final ConsoleBuffer frameBuffer;

    private InputRegister inputRegister;
    private String title;
    private boolean active;

    public Display(int width, int height) {
        this.width = width;
        this.height = height;
        this.frameBuffer = new ConsoleBuffer(width, height);
    }

    public void create(InputRegister inputRegister) {
        this.active = true;
        this.inputRegister = inputRegister;

        inputRegister.register();
    }

    public Size getTerminalSize() throws IOException {
        Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "stty size </dev/tty"});

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = br.readLine();

            if (line != null) {
                String[] parts = line.split(" ");
                int rows = Integer.parseInt(parts[0]);
                int cols = Integer.parseInt(parts[1]);

                return new Size(rows, cols);
            }
        }

        throw new IllegalStateException("Cannot get terminal size");
    }

    public void pollEvents() {
        inputRegister.pollEvents();
    }

    public void update(PrintStream out) {
        frameBuffer.render(out);
    }

    public void setTitle(String title) {
        setTitle(title, System.out);
    }

    public void setTitle(String title, PrintStream out) {
        this.title = title;
        out.print("\033]0;" + title + "\007");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ConsoleBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public InputRegister getInputRegister() {
        return inputRegister;
    }

    public void setInputRegister(InputRegister inputRegister) {
        this.inputRegister = inputRegister;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
