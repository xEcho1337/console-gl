package net.echo.consolegl.input;

import net.echo.consolegl.render.ConsoleBuffer;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Queue;

public class Display {

    private final int width;
    private final int height;
    private final ConsoleBuffer frameBuffer;
    private final Queue<Key> queue;

    private Key lastPressedKey = Key.UNKNOWN;
    private Key pressedKey = Key.UNKNOWN;

    private Thread inputRegister;
    private String title;
    private boolean active;

    public Display(int width, int height) {
        this.width = width;
        this.height = height;
        this.frameBuffer = new ConsoleBuffer(width, height);
        this.queue = new ArrayDeque<>();
    }

    public void create() throws IOException, InterruptedException {
        String[] cmd = {"/bin/sh", "-c", "stty raw </dev/tty"};
        Runtime.getRuntime().exec(cmd).waitFor();

        this.active = true;
        this.inputRegister = new Thread(() -> {
            while (active) {
                try {
                    byte[] inputBuffer = new byte[1];
                    System.in.read(inputBuffer, 0, inputBuffer.length);

                    char keyCode = (char) inputBuffer[0];
                    queue.add(Key.fromChar(keyCode));
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        });

        inputRegister.start();
    }

    public void pollEvents() {
        if (queue.isEmpty()) return;

        pressedKey = queue.poll();
        lastPressedKey = pressedKey;
    }

    public void update(PrintStream out) {
        frameBuffer.render(out);
        pressedKey = Key.UNKNOWN;
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

    public Thread getInputRegister() {
        return inputRegister;
    }

    public void setInputRegister(Thread inputRegister) {
        this.inputRegister = inputRegister;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        setTitle(title, System.out);
    }
    
    public void setTitle(String title, PrintStream out) {
        this.title = title;
        out.print("\033]0;" + title + "\007");
    }

    public Key getLastPressedKey() {
        return lastPressedKey;
    }

    public Key getPressedKey() {
        return pressedKey;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
