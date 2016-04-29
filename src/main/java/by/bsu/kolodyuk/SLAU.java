package by.bsu.kolodyuk;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.nCopies;

public class SLAU
{
    List<List<Double>> A;
    List<Double> f;

    void read(String fileName) throws FileNotFoundException
    {
        A = new ArrayList<>();
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
            else f = row;
        }
    }

    int solve()
    {
        try
        {
            read("Kolodyuk.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int mark_length = 50;
        int mark_count = 400000;

        Random random = new Random();


        List<Double> x = new ArrayList<>(nCopies(f.size(), 0.));

        List<Double> start_state_probs = new ArrayList<>(nCopies(f.size(), 1.0 / f.size()));

        List<List<Double>> transition_matrix = new ArrayList<>(nCopies(f.size(), start_state_probs));

        for (int k = 0; k < x.size(); k++)
        {
            List<Double> ksi = new ArrayList<>(nCopies(mark_count, 0.));

            List<Integer> h = new ArrayList<>(nCopies(f.size(), 0));

            h.set(k, 1);
            for (int i = 0; i < mark_count; i++)
            {
                List<Integer> mark_chain = new ArrayList<>(nCopies(mark_length, 0));

                mark_chain.set(0, getState(start_state_probs, random.nextDouble()));

                if (mark_chain.get(0) != k)
                {
                    continue;
                }

                for (int j = 1; j < mark_length; j++)
                {
                    mark_chain.set(j, getState(transition_matrix.get(mark_chain.get(j - 1)), random.nextDouble()));
                }

                List<Double> weights = new ArrayList<>(nCopies(mark_length, 0.));

                if (start_state_probs.get(mark_chain.get(0)) > 0)
                {
                    weights.set(0, h.get(mark_chain.get(0)) / start_state_probs.get(mark_chain.get(0)));
                }
                for (int j = 1; j < mark_length; j++)
                {
                    if (transition_matrix.get(mark_chain.get(j - 1)).get(mark_chain.get(j)) > 0)
                    {
                        weights.set(j, weights.get(j - 1) * A.get(mark_chain.get(j - 1)).get(mark_chain.get(j)) / transition_matrix.get(mark_chain.get(j - 1)).get(mark_chain.get(j)));
                    }
                }

                for (int j = 0; j < mark_length; j++)
                {
                    ksi.set(i, ksi.get(i) + weights.get(j) * f.get(mark_chain.get(j)));
                }
            }

            for (int i = 0; i < mark_count; i++)
            {
                x.set(k, x.get(k) + ksi.get(i));
            }
            x.set(k, x.get(k) / mark_count);
        }

        System.out.println("result: ");

        System.out.println(x);


        System.out.println("Checking delta between f: ");

        List<Double> check_f = new ArrayList<>(nCopies(f.size(), 0.));

        double average_error = 0;

        for (int i = 0; i < A.size(); i++)
        {
            for (int j = 0; j < A.get(i).size(); j++)
            {
                double mul = i == j ? 1 - A.get(i).get(j) : -A.get(i).get(j);
                check_f.set(i, check_f.get(i) + mul * x.get(j));
            }

            average_error += Math.abs(check_f.get(i) - f.get(i));
        }

        average_error /= x.size();
        System.out.println("Average error: " + average_error);

        return 0;
    }

    int getState(List<Double> state_probs, double probability)
    {
        double sum = 0;
        int state = 0;
        for (; state < state_probs.size(); state++)
        {
            sum += state_probs.get(state);
            if (probability <= sum)
            {
                break;
            }
        }

        return state;
    }
}
