import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
public class RF {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		int percent = 80;
		DataSource source = new DataSource("data/Three_activities_data_with__50_window_size_without_overlapping.arff");
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		
		
		int TrainSize = (int) Math.round(data.numInstances() * percent/ 100);
        int TestSize = data.numInstances() - TrainSize;
        
        Instances Train = new Instances(data, 0, TrainSize);
        Instances Test = new Instances(data, TrainSize, TestSize);
		
        
		System.out.println("Random Forest Classifier ");
		System.out.println("Train size : "+ Integer.toString(TrainSize));
		System.out.println("Test size : "+ Integer.toString(TestSize));
		RandomForest rf = new RandomForest();
		rf.buildClassifier(Train);
		Evaluation eval = new Evaluation(Test);
		eval.evaluateModel(rf, Test);
		System.out.println(eval.toSummaryString());
		/*
		 * SMO svm = new SMO(); svm.buildClassifier(dataset); Evaluation eval2 = new
		 * Evaluation(dataset); eval2.evaluateModel(svm, dataset);
		 * System.out.println(eval2.toSummaryString());
		 */

	}

}
