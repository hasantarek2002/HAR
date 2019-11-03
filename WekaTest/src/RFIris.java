import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class RFIris {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int percent = 80;
		DataSource source = new DataSource("data/iris.arff");
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		int numClasses = data.numClasses();
		System.out.println("Dataset contains "+Integer.toString(numClasses)+ " classes");
		
		
		int TrainSize = (int) Math.round(data.numInstances() * percent/ 100);
        int TestSize = data.numInstances() - TrainSize;

        Instances Train = new Instances(data, 0, TrainSize);
        Instances Test = new Instances(data, TrainSize, TestSize);
        
        int trainClasses = Train.numClasses();
		System.out.println("Train Dataset contains "+Integer.toString(trainClasses)+ " classes");
		int testClasses = Test.numClasses();
		System.out.println("Test Dataset contains "+Integer.toString(testClasses)+ " classes");
        
		System.out.println("Random Forest Classifier ");
		System.out.println("Train size : "+ Integer.toString(TrainSize));
		System.out.println("Test size : "+ Integer.toString(TestSize));
		
		RandomForest rf = new RandomForest();
		rf.buildClassifier(Train);
		
		System.out.println("Evaluation from trained model");
		Evaluation eval = new Evaluation(Test); 
		eval.evaluateModel(rf, Test);
		System.out.println(eval.toSummaryString());
		
		// save model
		weka.core.SerializationHelper.write("model/iris.model", rf);
		System.out.println("RF model saved");
		
		System.out.println("Evaluation from Loaded model");
		// model evaluation
		RandomForest rf2 = (RandomForest) weka.core.SerializationHelper.read("model/iris.model");
		
		Evaluation evol = new Evaluation(Test);
		evol.evaluateModel(rf2, Test);
		System.out.println(evol.toSummaryString());
		
		System.out.println("//////////////////////////");
		//System.out.println(Test);
		System.out.println(Test.size());
		System.out.println(Test.numAttributes());
		//System.out.println(Test.get(0).numAttributes());
		
		System.out.println(Test.get(0));
		System.out.println(Test.get(0).classValue());
		double actualClass = Test.instance(0).classValue();
		String actual = Test.classAttribute().value((int) actualClass);
		System.out.println(actual);
		
		
		System.out.println(Test.get(Test.size()-1));
		System.out.println(Test.get(Test.size()-1).classValue());
		actualClass = Test.instance(Test.size()-1).classValue();
		actual = Test.classAttribute().value((int) actualClass);
		System.out.println(actual);
		
		/*
		 * Attribute PT1 = new Attribute("PT1"); Attribute d1 = new Attribute("d1");
		 * Attribute PT2 = new Attribute("PT2"); Attribute d2 = new Attribute("d2");
		 * 
		 * FastVector fvClassVal = new FastVector(2); fvClassVal.addElement("SITTING");
		 * fvClassVal.addElement("STANDING"); fvClassVal.addElement("WALKING");
		 * Attribute Class = new Attribute("class", fvClassVal);
		 * 
		 * FastVector fvWekaAttributes = new FastVector(5);
		 * 
		 * fvWekaAttributes.addElement(PT1); fvWekaAttributes.addElement(d1);
		 * fvWekaAttributes.addElement(PT2); fvWekaAttributes.addElement(d2);
		 * fvWekaAttributes.addElement(Class);
		 * 
		 * Instances dataset = new Instances("whatever", fvWekaAttributes, 0);
		 * 
		 * double[] attValues = new double[dataset.numAttributes()]; attValues[0] = 6.9;
		 * attValues[1] = 3.2; attValues[2] = 5.7; attValues[3] = 2.3;
		 * 
		 * Instance i1 = new Instance(1.0, attValues);
		 */
		
		Attribute PT1 = new Attribute("PT1");
		Attribute d1 = new Attribute("d1");
		Attribute PT2 = new Attribute("PT2");
		Attribute d2 = new Attribute("d2");
		ArrayList<String> labels = new ArrayList<String>();
		/*
		 * labels.add("SITTING"); labels.add("STANDING"); labels.add("WALKING");
		 */
		
		labels.add("Iris-setosa");
		labels.add("Iris-versicolor");
		labels.add("Iris-virginica");
		Attribute cls = new Attribute("class", labels);
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(PT1);
		attributes.add(d1);
		attributes.add(PT2);
		attributes.add(d2);
		attributes.add(cls);
		Instances dataset = new Instances("Test-dataset", attributes, 0);
		
		double[] values = new double[dataset.numAttributes()];
		/*
		 * values[0] = 6.9; values[1] = 3.2; values[2] = 5.7; values[3] = 2.3;
		 */
		
		values[0] = 5.1;
		values[1] = 3.5;
		values[2] = 1.4;
		values[3] = 0.2;
		
		Instance inst = new DenseInstance(1.0, values);
		dataset.add(inst);
		
		dataset.setClassIndex(dataset.numAttributes()-1);
		System.out.println(dataset.numAttributes());
		System.out.println("////");
		
		System.out.println(rf2.classifyInstance(dataset.instance(0)));
		
		//double preNB = rf2.classifyInstance(dataset.get(0));
		//String predString = testdata.classAttribute().value((int) preNB);
		//System.out.println(actual+","+predString);
		//System.out.println(preNB);
		

	}

}
