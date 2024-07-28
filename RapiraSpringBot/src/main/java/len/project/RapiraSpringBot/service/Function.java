package len.project.RapiraSpringBot.service;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import java.util.Map;
import java.util.TreeMap;

public class Function {
    private UnivariateFunction splineFunction;

    public Function(Map<String, Integer> data) {
        fitSplineFunction(data);
    }

    private void fitSplineFunction(Map<String, Integer> data) {
        // Сортируем данные по ключам (ID)
        TreeMap<String, Integer> sortedData = new TreeMap<>(data);
        int size = sortedData.size();
        double[] x = new double[size];
        double[] y = new double[size];
        int index = 0;

        for (Map.Entry<String, Integer> entry : sortedData.entrySet()) {
            x[index] = Double.parseDouble(entry.getKey());
            y[index] = entry.getValue();
            index++;
        }

        UnivariateInterpolator interpolator = new SplineInterpolator();
        splineFunction = interpolator.interpolate(x, y);
    }

    public long estimateRegistrationDate(long id) {
        return Math.round(splineFunction.value(id));
    }
}
