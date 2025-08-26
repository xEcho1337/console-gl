package net.echo.consolegl.input.impl;

import net.echo.consolegl.input.InputRegister;
import net.echo.consolegl.input.Key;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class SingleInputRegister implements InputRegister {

    private final Queue<Key> queue;
    private Key pressedKey;
    private Key lastPressedKey;
    private Thread inputRegister;
    private boolean running;

    public SingleInputRegister() {
        this.queue = new ArrayDeque<>();
    }

    @Override
    public void register() {
        String[] cmd = {"/bin/sh", "-c", "stty raw </dev/tty"};

        try {
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        this.running = true;

        new Thread(() -> {
            while (running) {
                try {
                    byte[] inputBuffer = new byte[1];
                    System.in.read(inputBuffer, 0, inputBuffer.length);

                    char keyCode = (char) inputBuffer[0];
                    queue.add(Key.fromChar(keyCode));
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }).start();
    }

    @Override
    public void unregister() {
        running = false;
    }

    @Override
    public void pollEvents() {
        if (queue.isEmpty()) return;

        pressedKey = queue.poll();
        lastPressedKey = pressedKey;
    }

    public Queue<Key> getQueue() {
        return queue;
    }

    public Key getPressedKey() {
        return pressedKey;
    }

    public Key getLastPressedKey() {
        return lastPressedKey;
    }

    public Thread getInputRegister() {
        return inputRegister;
    }

    public boolean isRunning() {
        return running;
    }
}
