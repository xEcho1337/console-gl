package net.echo.consolegl.input;

public interface InputRegister {
    void register();

    void unregister();

    void pollEvents();
}
