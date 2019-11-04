import java.util.ArrayList;
import java.util.List;

import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ClassPredictionFiveActivity {

	public static void main(String[] args) throws Exception {
		RandomForest rf = (RandomForest) weka.core.SerializationHelper.read("model/rf5.model");

		Attribute mean_x = new Attribute("mean_x");
		Attribute mean_y = new Attribute("mean_y");
		Attribute mean_z = new Attribute("mean_z");
		Attribute magnitude_mean = new Attribute("magnitude_mean");
		
		Attribute std_x = new Attribute("std_x");
		Attribute std_y = new Attribute("std_y");
		Attribute std_z = new Attribute("std_z");
		Attribute magnitude_std = new Attribute("magnitude_std");
		
		Attribute rms_x = new Attribute("rms_x");
		Attribute rms_y = new Attribute("rms_y");
		Attribute rms_z = new Attribute("rms_z");
		Attribute magnitude_rms = new Attribute("magnitude_rms");
		
		Attribute min_x = new Attribute("min_x");
		Attribute min_y = new Attribute("min_y");
		Attribute min_z = new Attribute("min_z");
		Attribute magnitude_min = new Attribute("magnitude_min");
		
		Attribute max_x = new Attribute("max_x");
		Attribute max_y = new Attribute("max_y");
		Attribute max_z = new Attribute("max_z");
		Attribute magnitude_max = new Attribute("magnitude_max");
		
		Attribute median_x = new Attribute("median_x");
		Attribute median_y = new Attribute("median_y");
		Attribute median_z = new Attribute("median_z");
		Attribute magnitude_median = new Attribute("magnitude_median");
		
		Attribute mad_x = new Attribute("mad_x");
		Attribute mad_y = new Attribute("mad_y");
		Attribute mad_z = new Attribute("mad_z");
		Attribute magnitude_mad = new Attribute("magnitude_mad");
		
		Attribute corr_xy = new Attribute("corr_xy");
		Attribute corr_yz = new Attribute("corr_yz");
		Attribute corr_xz = new Attribute("corr_xz");
		
		ArrayList<String> labels = new ArrayList<String>();
		
		labels.add("SITTING");
		labels.add("STANDING"); 
		labels.add("WALKING");
		labels.add("WALKING_DOWNSTAIRS");
		labels.add("WALKING_UPSTAIRS");
		
		Attribute cls = new Attribute("class", labels);
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		attributes.add(mean_x);
		attributes.add(mean_y);
		attributes.add(mean_z);
		attributes.add(magnitude_mean);
		
		attributes.add(std_x);
		attributes.add(std_y);
		attributes.add(std_z);
		attributes.add(magnitude_std);
		
		attributes.add(rms_x);
		attributes.add(rms_y);
		attributes.add(rms_z);
		attributes.add(magnitude_rms);
		
		attributes.add(min_x);
		attributes.add(min_y);
		attributes.add(min_z);
		attributes.add(magnitude_min);
		
		attributes.add(max_x);
		attributes.add(max_y);
		attributes.add(max_z);
		attributes.add(magnitude_max);
		
		attributes.add(median_x);
		attributes.add(median_y);
		attributes.add(median_z);
		attributes.add(magnitude_median);
		
		attributes.add(mad_x);
		attributes.add(mad_y);
		attributes.add(mad_z);
		attributes.add(magnitude_mad);
		
		attributes.add(corr_xy);
		attributes.add(corr_yz);
		attributes.add(corr_xz);
		
		attributes.add(cls);
		Instances dataset = new Instances("Test-dataset", attributes, 0);
		System.out.println(dataset.numAttributes());
		double[] values = new double[dataset.numAttributes()];
		
		
		//-1.644151,7.299989,-4.774019,9.194468,1.374718,1.303727,1.601727,0.633384,2.143148,7.415493,5.035552,
		//9.216258,-3.890869,4.947205,-7.264801,7.616315,1.022034,10.116287,-0.647232,10.606999,
		//-1.443993,7.268387,-4.960381,9.253738,1.145463,1.075877,1.239351,0.493521,0.803482,0.755901,0.724554,SITTING

		List<Float> oneWindowFeatureData = new ArrayList<>();
		
		oneWindowFeatureData.add((float) -1.644151);
		oneWindowFeatureData.add((float) 7.299989);
		oneWindowFeatureData.add((float) -4.774019);
		oneWindowFeatureData.add((float) 9.194468);
		
		oneWindowFeatureData.add((float) 1.374718);
		oneWindowFeatureData.add((float) 1.303727);
		oneWindowFeatureData.add((float) 1.601727);
		oneWindowFeatureData.add((float) 0.633384);
		
		oneWindowFeatureData.add((float) 2.143148);
		oneWindowFeatureData.add((float) 7.415493);
		oneWindowFeatureData.add((float) 5.035552);
		oneWindowFeatureData.add((float) 9.216258);
		
		oneWindowFeatureData.add((float) -3.890869);
		oneWindowFeatureData.add((float) 4.947205);
		oneWindowFeatureData.add((float) -7.264801);
		oneWindowFeatureData.add((float) 7.616315);
		
		oneWindowFeatureData.add((float) 1.022034);
		oneWindowFeatureData.add((float) 10.116287);
		oneWindowFeatureData.add((float) -0.647232);
		oneWindowFeatureData.add((float) 10.606999);
		
		oneWindowFeatureData.add((float) -1.443993);
		oneWindowFeatureData.add((float) 7.268387);
		oneWindowFeatureData.add((float) -4.960381);
		oneWindowFeatureData.add((float) 9.253738);
		
		oneWindowFeatureData.add((float) 1.145463);
		oneWindowFeatureData.add((float) 1.075877);
		oneWindowFeatureData.add((float) 1.239351);
		oneWindowFeatureData.add((float) 0.493521);
		
		
		oneWindowFeatureData.add((float) 0.803482);
		oneWindowFeatureData.add((float) 0.755901);
		oneWindowFeatureData.add((float) 0.724554);
		int count =0;
		for (int i=0; i<dataset.numAttributes()-1; i++) {
			values[i] = oneWindowFeatureData.get(i);
			count++;
		}
		//System.out.println(count);
		
		Instance inst = new DenseInstance(1.0, values);
		dataset.add(inst);
		
		dataset.setClassIndex(dataset.numAttributes()-1);
		
		System.out.println("////");
		System.out.println(rf.classifyInstance(dataset.instance(0)));
		
		/*
		 * dataset.clear(); inst = new DenseInstance(1.0, values); dataset.add(inst);
		 * 
		 * dataset.setClassIndex(dataset.numAttributes()-1);
		 * 
		 * System.out.println("////");
		 * System.out.println(rf.classifyInstance(dataset.instance(0)));
		 */

	}

}
