package by.bsu.kolodyuk;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import org.apache.commons.math3.linear.*;

import javax.naming.SizeLimitExceededException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.*;

public class Controller implements Initializable
{
    @FXML
    XYChart<Integer, Double> errorChart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int n = 100000;
        Function<Double, Double> function = x -> exp(x) * log(x);
        System.out.println(MonteCarloUtil.integrate(function, 1, 3, n));

        BiFunction<Double, Double, Double> biFunction = (x, y) -> exp(-(x*x + y*y) / 2) * log(1 + (2*x - 3*y)*(2*x - 3*y));
        System.out.println(MonteCarloUtil.integrate(biFunction, -10, 10, -10, 10, n)); // 12.0694

        TriFunction<Double, Double, Double, Double> triFunction = (x, y, z) -> x*x + y*y + z*z;
        System.out.println(MonteCarloUtil.integrate(triFunction, 0, 1, 0, 1, 0, 1, n));

        XYChart.Series<Integer, Double> series = new XYChart.Series<>();
        series.setName("Integration Error");
        IntStream.iterate(100, i -> i += 100).limit(1000).forEach(i -> series.getData().add(new XYChart.Data<>(i, abs(1 - MonteCarloUtil.integrate(triFunction, 0, 1, 0, 1, 0, 1, i)))));
        errorChart.getData().add(series);

        try {
            SLAEUtil.read("Kolodyuk.txt");
            System.out.println(Arrays.stream(SLAEUtil.slau()).mapToObj(i -> i + "").collect(Collectors.joining(" ")));
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

}
