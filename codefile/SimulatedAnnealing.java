import java.util.*;

public class SimulatedAnnealing {
    private int niter;
    public ArrayList<Double> hist;

    public SimulatedAnnealing(int niter) {//생성자
        this.niter = niter;
        hist = new ArrayList<>();
    }


    public double solve(Problem p, double t, double a, double x0, double lower, double upper) {
        Random r = new Random();
        double f0 = p.fit(x0);
        hist.add(f0);

        for (int i=0; i<niter; i++) {//REPEAT
            int kt = (int) t; //온도가 낮아지면 횟수가 줄어듬
            for(int j=0; j<kt; j++) {
                double x1 = r.nextDouble() * (upper - lower) + lower;
                double f1 = p.fit(x1);

                if(p.isNeighborBetter(f0, f1)) {
                    x0 = x1;
                    f0 = f1;
                    hist.add(f0);
                }
                else {
                    double d = Math.sqrt(Math.abs(f1 - f0));
                    double p0 = Math.exp(-d/t);
                    if(r.nextDouble() < p0) {
                        x0 = x1;
                        f0 = f1;
                        hist.add(f0);
                    }
                }
            }
            t *= a;//0<a<1
        }
        return x0;
    }
}