package com.example.harwithwekafiveactivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeatureExtractor {

	public List<Float> calculateMagnitudeArray(List<Float> x, List<Float> y, List<Float> z) {
		List<Float> magnitude = new ArrayList<>();

		for (int i = 0; i < x.size(); i++) {
			float val = (float) Math.sqrt(x.get(i) * x.get(i) + y.get(i) * y.get(i) + z.get(i) * z.get(i));
			magnitude.add(val);
		}
		return magnitude;
	}

	public List<Float> mean(List<Float> x, List<Float> y, List<Float> z, List<Float> magnitude) {
		double sumX = 0;
		double sumY = 0;
		double sumZ = 0;
		double sumMagnitude = 0;
		for (int i = 0; i < x.size(); i++) {
			sumX += x.get(i);
			sumY += y.get(i);
			sumZ += z.get(i);
			sumMagnitude += magnitude.get(i);
		}
		int length = x.size();
		return new ArrayList<>(Arrays.asList((float) (sumX / length), (float) (sumY / length), (float) (sumZ / length),
				(float) (sumMagnitude / length)));
	}

	public List<Float> calculateSTD(List<Float> x, List<Float> y, List<Float> z, List<Float> magnitude) {
		int length = x.size();
		List<Float> mean = mean(x, y, z, magnitude);
		double stdX = 0.0, stdY = 0.0, stdZ = 0.0, stdMagnitude = 0.0;
		for (int i = 0; i < x.size(); i++) {
			stdX += Math.pow(x.get(i) - mean.get(0), 2);
			stdY += Math.pow(y.get(i) - mean.get(1), 2);
			stdZ += Math.pow(z.get(i) - mean.get(2), 2);
			stdMagnitude += Math.pow(magnitude.get(i) - mean.get(3), 2);
		}

		return new ArrayList<>(Arrays.asList((float) Math.sqrt(stdX / length), (float) Math.sqrt(stdY / length),
				(float) Math.sqrt(stdZ / length), (float) Math.sqrt(stdMagnitude / length)));
	}

	public List<Float> calculateRmsValue(List<Float> x, List<Float> y, List<Float> z, List<Float> magnitude) {
		double squareX = 0.0, squareY = 0.0, squareZ = 0.0, squareMagnitude = 0.0;
		double meanX = 0.0, meanY = 0.0, meanZ = 0.0, meanMagnitude = 0.0;
		for (int i = 0; i < x.size(); i++) {
			squareX += Math.pow(x.get(i), 2);
			squareY += Math.pow(y.get(i), 2);
			squareZ += Math.pow(z.get(i), 2);
			squareMagnitude += Math.pow(magnitude.get(i), 2);
		}

		meanX = (squareX / x.size());
		meanY = (squareY / y.size());
		meanZ = (squareZ / z.size());
		meanMagnitude = (squareMagnitude / magnitude.size());

		return new ArrayList<>(Arrays.asList((float) Math.sqrt(meanX), (float) Math.sqrt(meanY),
				(float) Math.sqrt(meanZ), (float) Math.sqrt(meanMagnitude)));
	}

	public List<Float> calculateMinValue(List<Float> x, List<Float> y, List<Float> z, List<Float> magnitude) {
		List<Float> xx = new ArrayList<>();
		List<Float> yy = new ArrayList<>();
		List<Float> zz = new ArrayList<>();
		List<Float> mag = new ArrayList<>();
		xx.addAll(x);
		yy.addAll(y);
		zz.addAll(z);
		mag.addAll(magnitude);

		Collections.sort(xx);
		Collections.sort(yy);
		Collections.sort(zz);
		Collections.sort(mag);
		return new ArrayList<>(Arrays.asList(xx.get(0), yy.get(0), zz.get(0), mag.get(0)));
	}

	public List<Float> calculateMaxValue(List<Float> x, List<Float> y, List<Float> z, List<Float> magnitude) {
		List<Float> xx = new ArrayList<>();
		List<Float> yy = new ArrayList<>();
		List<Float> zz = new ArrayList<>();
		List<Float> mag = new ArrayList<>();
		xx.addAll(x);
		yy.addAll(y);
		zz.addAll(z);
		mag.addAll(magnitude);

		Collections.sort(xx);
		Collections.sort(yy);
		Collections.sort(zz);
		Collections.sort(mag);
		return new ArrayList<>(Arrays.asList(xx.get(xx.size() - 1), yy.get(yy.size() - 1), zz.get(zz.size() - 1),
				mag.get(mag.size() - 1)));
	}

	public List<Float> median(List<Float> x, List<Float> y, List<Float> z, List<Float> magnitude) {
		if (x.size() == 0 || y.size() == 0 || z.size() == 0 || magnitude.size() == 0) {
			throw new IllegalArgumentException("Need at least 1 element to calculate median");
		}
		List<Float> xx = new ArrayList<>();
		List<Float> yy = new ArrayList<>();
		List<Float> zz = new ArrayList<>();
		List<Float> mag = new ArrayList<>();
		xx.addAll(x);
		yy.addAll(y);
		zz.addAll(z);
		mag.addAll(magnitude);

		Collections.sort(xx);
		Collections.sort(yy);
		Collections.sort(zz);
		Collections.sort(mag);
		double medianX = 0.0, medianY = 0.0, medianZ = 0.0, medianMagnitude = 0.0;

		int middleX = xx.size() / 2;
		int middleY = yy.size() / 2;
		int middleZ = zz.size() / 2;
		int middleMagnitude = mag.size() / 2;
		if (xx.size() % 2 == 1) {
			medianX = xx.get(middleX);
		} else {
			medianX = ((xx.get(middleX - 1) + xx.get(middleX)) / 2.0);
		}

		if (yy.size() % 2 == 1) {
			medianY = yy.get(middleY);
		} else {
			medianY = ((yy.get(middleY - 1) + yy.get(middleY)) / 2.0);
		}

		if (zz.size() % 2 == 1) {
			medianZ = zz.get(middleZ);
		} else {
			medianZ = ((zz.get(middleZ - 1) + zz.get(middleZ)) / 2.0);
		}

		if (mag.size() % 2 == 1) {
			medianMagnitude = mag.get(middleMagnitude);
		} else {
			medianMagnitude = ((mag.get(middleMagnitude - 1) + mag.get(middleMagnitude)) / 2.0);
		}

		return new ArrayList<>(
				Arrays.asList((float) medianX, (float) medianY, (float) medianZ, (float) medianMagnitude));
	}

	public List<Float> mad(List<Float> x, List<Float> y, List<Float> z, List<Float> magnitude) {

		List<Float> mean = mean(x, y, z, magnitude);
		List<Float> meanArrayDeviationX = new ArrayList<>();
		List<Float> meanArrayDeviationY = new ArrayList<>();
		List<Float> meanArrayDeviationZ = new ArrayList<>();
		List<Float> meanArrayDeviationMagnitude = new ArrayList<>();

		for (int i = 0; i < x.size(); i++) {
			meanArrayDeviationX.add(Math.abs(x.get(i) - mean.get(0)));
			meanArrayDeviationY.add(Math.abs(y.get(i) - mean.get(1)));
			meanArrayDeviationZ.add(Math.abs(z.get(i) - mean.get(2)));
			meanArrayDeviationMagnitude.add(Math.abs(magnitude.get(i) - mean.get(3)));
		}
		//System.out.println(medianArrayDeviationX.toString());
		return mean(meanArrayDeviationX, meanArrayDeviationY, meanArrayDeviationZ,
				meanArrayDeviationMagnitude);
	}

	public float Correlation(List<Float> xs, List<Float> ys) {
		// check here that arrays are not null, of the same length etc

		double sx = 0.0;
		double sy = 0.0;
		double sxx = 0.0;
		double syy = 0.0;
		double sxy = 0.0;

		int n = xs.size();
		for (int i = 0; i < n; ++i) {
			double x = xs.get(i);
			double y = ys.get(i);

			sx += x;
			sy += y;
			sxx += x * x;
			syy += y * y;
			sxy += x * y;
		}

		// covariation
		double cov = sxy / n - sx * sy / n / n;
		// standard error of x
		double sigmax = Math.sqrt(sxx / n - sx * sx / n / n);
		// standard error of y
		double sigmay = Math.sqrt(syy / n - sy * sy / n / n);

		// correlation is just a normalized covariation
		return (float) (cov / sigmax / sigmay);
	}
}
