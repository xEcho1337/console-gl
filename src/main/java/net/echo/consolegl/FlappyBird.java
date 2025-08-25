package net.echo.consolegl;

import net.echo.consolegl.input.Display;
import net.echo.consolegl.model.VertexPolicy;
import net.echo.consolegl.render.ConsoleBuffer;
import net.echo.consolegl.render.RenderStack;
import net.echo.consolegl.input.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBird {
    
    private static class Tube {
        int x;
        int holeY;
        int holeHeight;
        
        Tube(int x, int holeY, int holeHeight) {
            this.x = x;
            this.holeY = holeY;
            this.holeHeight = holeHeight;
        }
    }
    
    public static void main(String[] args) throws Exception {
        new FlappyBird().start();
    }
    
    private void start() throws Exception {
        CGL.setVertexPolicy(VertexPolicy.IGNORE);
        Display display = new Display(80, 25);
        
        display.setTitle("Flappy Bird ASCII");
        display.create();
        
        try {
            runGame(display);
        } catch (Exception ex) {
            display.setActive(false);
            ex.printStackTrace(System.err);
        }
    }
    
    private void runGame(Display display) throws InterruptedException {
        int frameRate = 20;
        double gravity = 0.07;
        double jump = -0.8;
        
        double birdY = 0.5;
        double birdVelocity = 0;
        
        int width = display.getWidth();
        int height = display.getHeight();
        
        List<Tube> tubes = new ArrayList<>();
        Random rand = new Random();
        
        int score = 0;
        int tubeSpacing = 25;
        int tubeWidth = 3;
        int holeHeight = 6;
        
        int frame = 0;
        boolean gameOver = false;
        
        while (display.isActive()) {
            Thread.sleep(1000 / frameRate);
            frame++;
            
            ConsoleBuffer buffer = display.getFrameBuffer();
            buffer.clear();
            display.pollEvents();
            
            Key key = display.getPressedKey();
            
            if (key == Key.ESCAPE) {
                display.setActive(false);
                return;
            }
            
            double ground = 0.9;
            
            try (RenderStack stack = buffer.newStack(CGL.QUADS)) {
                stack.color(84, 195, 255);
                stack.glyph(CGL.GLYPHS[4]);
                
                stack.vertex(0, 0);
                stack.vertex(1, 0);
                stack.vertex(1, ground);
                stack.vertex(0, ground);
                
                stack.color(252, 249, 149);
                
                stack.vertex(0, ground);
                stack.vertex(1, ground);
                stack.vertex(1, 1);
                stack.vertex(0, 1);
            }
            
            if (!gameOver) {
                if (key == Key.SPACE) birdVelocity = jump;
                birdVelocity += gravity;
                
                birdY += birdVelocity / height;
                birdY = Math.max(0, Math.min(1, birdY));
                
                if (frame % tubeSpacing == 0) {
                    int offset = (int) (height * (1 - ground));
                    int holeY = rand.nextInt(height - offset - holeHeight - 5) + 1;
                    tubes.add(new Tube(width - 1, holeY, holeHeight));
                }
                
                tubes.forEach(t -> t.x--);
                List<Tube> toRemove = new ArrayList<>();
                for (Tube t : tubes) {
                    if (t.x < 0) {
                        toRemove.add(t);
                        score++;
                    }
                }
                tubes.removeAll(toRemove);
                
                if (birdY > ground) {
                    gameOver = true;
                }
                
                for (Tube t : tubes) {
                    if (t.x <= width / 4 && t.x + tubeWidth >= width / 4) {
                        int birdRow = (int) (birdY * height);
                        if (birdRow < t.holeY || birdRow > t.holeY + t.holeHeight) {
                            gameOver = true;
                        }
                    }
                }
            }
            
            try (RenderStack stack = buffer.newStack(CGL.POINTS)) {
                stack.color(255, 255, 0);
                stack.glyph('O');
                stack.vertex(0.25, birdY);
                
                for (Tube t : tubes) {
                    stack.color(0, 255, 0);
                    for (int yPos = 0; yPos < height; yPos++) {
                        if (yPos < t.holeY || yPos > t.holeY + t.holeHeight) {
                            double xNorm = (double) t.x / width;
                            double yNorm = (double) yPos / height;
                            stack.glyph(CGL.GLYPHS[4]);
                            stack.vertex(xNorm, yNorm);
                        }
                    }
                }
            }
            
            display.setTitle("Score: " + score);
            display.update(System.out);
        }
    }
}
