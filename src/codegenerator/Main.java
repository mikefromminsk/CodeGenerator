package codegenerator;

public class Main {

    static double piResult = 0; // Можно убрать
    public static void calculatePI() {
        double res = 0.0 + 0.0;
        double i = 1.0 + 0.0;
        double dx = 1.0 + 0.0;
        do {
            i = i + 1.0;
            res = res + dx;
            double sqrI = i * i;
            dx = 1.0 / sqrI;
            double resMul6 = res * 6.0;
            piResult = Math.sqrt(resMul6);
    } while (i < 1000000.0 / 6.0);
    }

    //TODO Разбить CodeGenerator по функциям
    //TODO Убрать избыточность



    public static void main(String[] args) {
        //System.out.println(calculatePI());
        //new Thread(new Sync(2, new File(""))).start();
        new Thread(new CodeGenerator(5)).start();
    }
}
