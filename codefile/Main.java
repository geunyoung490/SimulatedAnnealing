public class Main {
    public static void main(String[] args) {
        SimulatedAnnealing sa = new SimulatedAnnealing(1000);
        Problem p = new Problem() {
            @Override
            public double fit(double x) {
                return -2*x*x*x+8*x+3;
            }

            @Override
            public boolean isNeighborBetter(double f0, double f1) {
                return f0 < f1;
            }
        };
            double x = sa.solve(p, 100 , 0.99, 0, -2, 2);
            System.out.println(x);
            System.out.println(p.fit(x));
    }
}