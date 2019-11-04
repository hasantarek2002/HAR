import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class RFSaveLoadModelFiveActivity {

	public static void main(String[] args) throws Exception {
		int percent = 80;
		DataSource source = new DataSource("data/Five_activities_data_with__50_window_size_without_overlapping.arff");
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
		
		rf.setMaxDepth(10);
		rf.setNumTrees(100);
		rf.buildClassifier(Train);
		
		System.out.println("Evaluation from trained model");
		Evaluation eval = new Evaluation(Test); 
		eval.evaluateModel(rf, Test);
		System.out.println(eval.toSummaryString());
		
		// save model
		weka.core.SerializationHelper.write("model/rf5.model", rf);
		System.out.println("RF model saved");
		
		System.out.println("Evaluation from Loaded model");
		// model evaluation
		RandomForest rf2 = (RandomForest) weka.core.SerializationHelper.read("model/rf5.model");
		
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
	}
}
