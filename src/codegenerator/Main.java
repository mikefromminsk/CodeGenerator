package codegenerator;

public class Main {

    public static double calculatePI() {
        double piResult = 0.0; // Не входит в codeLines нужно для инициализации в Java
        // begin codeLines
        double res = 0.0 + 0.0;
        double i = 1.0 + 0.0;
        double dx = 1.0 + 0.0;
        do {
            i = i + 1.0;
            res = res + dx;
            double sqrI = i * i;
            dx = 1.0 / sqrI;
            double resMul6 = res * 6.0;
            piResult = Math.pow(resMul6, 0.5);
    } while (i < 10000.0 / 7.0); // Ограничитель вечного цикла. В codeLines стоит while (1.0 == 1.0)
        // end codeLines
        return piResult;
    }

    public static void main(String[] args) {
        //System.out.println(calculatePI());
        //new Thread(new Sync(2, new File(""))).start();
        //new Thread(new CodeGenerator(5)).start();
    }
}
