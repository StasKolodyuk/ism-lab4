package by.bsu.kolodyuk;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.linear.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Aspire on 28.04.2016.
 */
public class SLAEUtil {

    static List<List<Double>> A = new ArrayList<>();
    static List<Double> b = new ArrayList<>();

    static void read(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        boolean readMatrix = true;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                readMatrix = false;
                continue;
            }
            String[] split = line.split(" ");
            List<Double> row = Arrays.stream(split).map(Double::valueOf).collect(Collectors.toList());
            if (readMatrix) A.add(row);
            else b = row;
        }
    }

    static double[] slau() throws FileNotFoundException {

        double[][] aArray = new double[A.size()][A.size()];
        double[] bArray = new double[A.size()];
        for(int i = 0; i < aArray.length; i++) {
            for(int j = 0; j < aArray[i].length; j++) {
                aArray[i][j] = A.get(i).get(j);
            }
            bArray[i] = b.get(i);
        }


        RealMatrix coefficients =
                new Array2DRowRealMatrix(aArray, false);
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();

        RealVector constants = new ArrayRealVector(bArray, false);
        RealVector solution = solver.solve(constants);

        System.out.println(solution);


        PrintWriter writer = new PrintWriter(new File("slau"));
        int n = b.size();
        int N = 1000;
        int m = 10000;
        double[] x = new double[n];  //Ðåøåíèå ñèñòåìû
        double[] h = new double[n];
        double pi = 1.0 / (double) n;
        int[] i = new int[N + 1];    //Öåïü Ìàðêîâà
        double[] Q = new double[N + 1];  //Âåñà ñîñòîÿíèé öåïè Ìàðêîâà
        double[][] ksi = new double[n][m];  //ÑÂ
        double alpha;  //ÁÑÂ

        for (int k = 0; k < n; k++)
            for (int j = 0; j < m; j++)
                ksi[k][j] = 0;

        UniformRealDistribution distribution = new UniformRealDistribution(0, 1);
        for (int j = 0; j < m; j++) {
            for (int k = 0; k <= N; k++) {
                alpha = distribution.sample();
                double temp = 1.0 / (double) n;
                for (int t = 0; t < n; t++) {
                    if (alpha <= temp) {
                        i[k] = t;
                        break;
                    } else
                        temp += 1.0 / n;
                }
            }

            for (int dim = 0; dim < n; dim++) {
                for (int ind = 0; ind < n; ind++) {
                    if (ind == dim) {
                        h[ind] = 1;
                    } else {
                        h[ind] = 0;
                    }
                }
                //Âû÷èñëÿåì âåñà öåïè Ìàðêîâà
                Q[0] = h[i[0]] / pi;

                for (int k = 1; k <= N; k++) {
                    Q[k] = Q[k - 1] * A.get(i[k - 1]).get(i[k]) / pi;
                }
                for (int k = 0; k <= N; k++)
                    ksi[dim][j] = ksi[dim][j] + Q[k] * b.get(i[k]);
            }
        }
        for (int dim = 0; dim < n; dim++) {
            x[dim] = 0;
            for (int k = 0; k < m; k++)
                x[dim] = x[dim] + ksi[dim][k];
            x[dim] = x[dim] / m;
            writer.print(x[dim] + " ");
        }
        writer.flush();
        writer.close();
        return x;
    }


}
