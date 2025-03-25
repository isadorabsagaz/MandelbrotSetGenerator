import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class UI extends JPanel {

    private static final int WIDTH = 900, HEIGHT = 900, MAX_ITERATIONS = 1000;
    private double minX = -2, maxX = 2, minY = -2, maxY = 2;
    private BufferedImage image;

   public UI() {
       setPreferredSize(new Dimension(WIDTH, HEIGHT));
       addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               zoom(e.getX(), e.getY(), 0.5);
           }
       });
       addMouseWheelListener(e -> {
           double scale = e.getPreciseWheelRotation() < 0 ? 0.9 : 1.1;
           zoom(getWidth() / 2, getHeight() / 2, scale);
       });
       generateFractal();
   }

   private void generateFractal () {
       image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
       for (int x = 0; x < WIDTH; x++) {
           for (int y = 0; y < HEIGHT; y++) {
               double zx = minX + x * (maxX - minX) / WIDTH;
               double zy = minY + y * (maxY - minY) / HEIGHT;

               Complex z = new Complex(zx, zy);
               int iterations = Mandelbrot.mandelbrotSet(z, MAX_ITERATIONS);

               int color = Color.HSBtoRGB(0.7f + (float) iterations / MAX_ITERATIONS, 1, iterations > 0 ? 1 : 0);
               image.setRGB(x, y, color);
           }
       }
       repaint();
   }

    private void zoom(int x, int y, double scale) {
        double centerX =  minX + x * (maxX - minX) / WIDTH;
        double centerY = minY + y * (maxY - minY) / HEIGHT;
        double rangeX = (maxX - minX) * scale;
        double rangeY = (maxY - minY) * scale;
        minX = centerX - rangeX / 2;
        maxX = centerX + rangeX / 2;
        minY = centerY - rangeY / 2;
        maxY = centerY + rangeY / 2;
        generateFractal();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
