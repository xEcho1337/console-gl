package net.echo.consolegl.input;

public enum Key {

    A, B, C, D, E, F, G, H, I, J, K, L, M,
    N, O, P, Q, R, S, T, U, V, W, X, Y, Z,

    DIGIT_0, DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4,
    DIGIT_5, DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9,

    SPACE, ENTER, ESCAPE, BACKSPACE, TAB,
    ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,

    UNKNOWN;

    public static Key fromChar(char ch) {
        return switch (ch) {
            case 'a', 'A' -> Key.A;
            case 'b', 'B' -> Key.B;
            case 'c', 'C' -> Key.C;
            case 'd', 'D' -> Key.D;
            case 'e', 'E' -> Key.E;
            case 'f', 'F' -> Key.F;
            case 'g', 'G' -> Key.G;
            case 'h', 'H' -> Key.H;
            case 'i', 'I' -> Key.I;
            case 'j', 'J' -> Key.J;
            case 'k', 'K' -> Key.K;
            case 'l', 'L' -> Key.L;
            case 'm', 'M' -> Key.M;
            case 'n', 'N' -> Key.N;
            case 'o', 'O' -> Key.O;
            case 'p', 'P' -> Key.P;
            case 'q', 'Q' -> Key.Q;
            case 'r', 'R' -> Key.R;
            case 's', 'S' -> Key.S;
            case 't', 'T' -> Key.T;
            case 'u', 'U' -> Key.U;
            case 'v', 'V' -> Key.V;
            case 'w', 'W' -> Key.W;
            case 'x', 'X' -> Key.X;
            case 'y', 'Y' -> Key.Y;
            case 'z', 'Z' -> Key.Z;
            case '0' -> Key.DIGIT_0;
            case '1' -> Key.DIGIT_1;
            case '2' -> Key.DIGIT_2;
            case '3' -> Key.DIGIT_3;
            case '4' -> Key.DIGIT_4;
            case '5' -> Key.DIGIT_5;
            case '6' -> Key.DIGIT_6;
            case '7' -> Key.DIGIT_7;
            case '8' -> Key.DIGIT_8;
            case '9' -> Key.DIGIT_9;
            case ' ' -> Key.SPACE;
            case '\n' -> Key.ENTER;
            case '\b' -> Key.BACKSPACE;
            case '\t' -> Key.TAB;
            case 27 -> Key.ESCAPE;
            default -> Key.UNKNOWN;
        };
    }
}
