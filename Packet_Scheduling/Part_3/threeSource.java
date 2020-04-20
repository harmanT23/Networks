public class threeSource {

    public static void main(String[] args) {

        String[] f1 = { "8", "1" };
        String[] f2 = { "6", "2" };
        String[] f3 = { "2", "3" };

        new Thread() {
            @Override
            public void run() {
                TrafficGenerator_Poisson.main(f1);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                TrafficGenerator_Poisson.main(f2);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                TrafficGenerator_Poisson.main(f3);
            }
        }.start();
    }

}