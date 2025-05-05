package scanlin.model;

public class CalcFSTEC {
    public CalcFSTEC() {

    }

    public double calc(double cvss, int type, int count, int network) {
        double arg1 = 0.4;
        double arg2 = 0.2;
        double arg3 = 0.4;
        if (type == 2 || type == 3) {
            arg1 *=  0.8;
        } else if (type == 4 || type == 5) {
            arg1 *= 0.5;
        }
        if (count == 2) {
            arg2 *= 0.8;
        } else if (count == 3) {
            arg2 *= 0.6;
        } else if (count == 4) {
            arg2 *= 0.5;
        }
        if (network == 2) {
            arg3 *= 0.5;
        }
        return cvss * (arg1 + arg2 + arg3);
    }
}
