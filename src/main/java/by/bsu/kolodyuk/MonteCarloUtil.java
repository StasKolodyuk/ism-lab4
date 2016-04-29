package by.bsu.kolodyuk;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MonteCarloUtil {

    public static double integrate(Function<Double, Double> function, double a, double b, int n) {
        return (b - a) / n * new Random().doubles(n, a, b).map(x -> function.apply(x)).sum();
    }

    public static double integrate(BiFunction<Double, Double, Double> function, double a, double b, double c, double d, int n) {
        return (b - a) * (d - c) / n * IntStream.range(0, n).mapToDouble(i -> function.apply(random(a, b), random(c, d))).sum();
    }

    public static double integrate(TriFunction<Double, Double, Double, Double> function, double a, double b, double c, double d, double e, double f, int n) {
        return (b - a) * (d - c) * (f - e) / n * IntStream.range(0, n).mapToDouble(i -> function.apply(random(a, b), random(c, d), random(e, f))).sum();
    }

    public static double random(double a, double b) {
        return ThreadLocalRandom.current().nextDouble(a, b);
    }
}
