package net.echo.consolegl.input.impl;

import net.echo.consolegl.input.InputRegister;

import java.util.Scanner;

public class LineInputRegister implements InputRegister {

    private Scanner scanner;
    private String value;

    @Override
    public void register() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void unregister() {
    }

    @Override
    public void pollEvents() {
        this.value = scanner.nextLine();
    }

    public Scanner getScanner() {
        return scanner;
    }

    public String getValue() {
        return value;
    }
}
