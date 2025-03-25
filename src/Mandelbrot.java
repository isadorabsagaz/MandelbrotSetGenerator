public class Mandelbrot {

    public static int mandelbrotSet(Complex c, int maxIterations) {
        Complex z = c;
        for (int i = 0; i < maxIterations; i++) {
            if (z.abs() > 2) return i;
            z = z.multiply(z).add(c);
        }
        return maxIterations;
    }
}
