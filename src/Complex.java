public class Complex {

    private final double real;
    private final double imag;

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double abs (){
        return Math.sqrt(real * real + imag * imag);
    }

    public Complex add (Complex z) {
       Complex a = this;
       double real = a.real + z.real;
       double imag = a.imag + z.imag;
       return new Complex(real, imag);
    }

    public Complex multiply (Complex z) {
        Complex a = this;
        double real = a.real * z.real - a.imag * z.imag;
        double imag = a.real * z.imag + a.imag * z.real;
        return new Complex(real, imag);
    }

    public String print () {
        if (real == 0 ) return imag + "i";
        if (imag == 0) return real + "";
        if (imag < 0) return real + " " + imag + "i";
        return real + " + " + imag + "i";
    }
}
