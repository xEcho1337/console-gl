package net.echo.consolegl.input;

import net.echo.consolegl.render.ConsoleBuffer;

import java.io.PrintStream;

public class Display {

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
