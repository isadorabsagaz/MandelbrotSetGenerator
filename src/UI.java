import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UI extends JPanel {

    private static final int WIDTH = 800, HEIGHT = 800, MAX_ITERATIONS = 1000;
    private static final double ZOOM_FACTOR = 0.2;
    private double minX = -2, maxX = 2, minY = -2, maxY = 2;
    private final BufferedImage image;

    public UI() {
       setPreferredSize(new Dimension(WIDTH, HEIGHT));
       image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

       addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               zoom(e.getX(), e.getY(), 0.9 * ZOOM_FACTOR);
           }
       });
       addMouseWheelListener(new MouseAdapter() {
           @Override
           public void mouseWheelMoved(MouseWheelEvent e) {
               double scale = (e.getWheelRotation() < 0) ?  ZOOM_FACTOR : 1 / ZOOM_FACTOR;
               zoom(e.getX(), e.getY(), scale);
           }
       });
       generateFractal();
   }

   private void generateFractal () {
       int numThreads = Runtime.getRuntime().availableProcessors();
       try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
           CountDownLatch countDownLatch = new CountDownLatch(numThreads);

           for (int threadID = 0; threadID < numThreads; threadID++) {
               int startY = (HEIGHT / numThreads) * threadID;
               int endY = (HEIGHT / numThreads) * (threadID + 1);

               executor.submit(() -> {
                   for (int y = startY; y < endY; y++) {
                       for (int x = 0; x < WIDTH; x++) {
                           double zx = minX + x * (maxX - minX) / WIDTH;
                           double zy = minY + y * (maxY - minY) / HEIGHT;

                           Complex z = new Complex(zx, zy);
                           int iterations = Mandelbrot.mandelbrotSet(z, MAX_ITERATIONS);

                           int color = Color.HSBtoRGB(0.7f + (float) iterations / MAX_ITERATIONS, 0.9f, iterations > 0 ? 1 : 0);
                           image.setRGB(x, y, color);
                       }
                   }
                   countDownLatch.countDown();
               });
           }

           new Thread(() -> {
               try {
                   countDownLatch.await();
                   repaint();
               } catch (InterruptedException ignored) {
               }
           }).start();

           executor.shutdown();
       }
   }

    private void zoom(int x, int y, double zoomFactor) {
        double centerX = minX + x * (maxX - minX) / WIDTH;
        double centerY = minY + y * (maxY - minY) / HEIGHT;
        double rangeX = (maxX - minX) * zoomFactor;
        double rangeY = (maxY - minY) * zoomFactor;
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
