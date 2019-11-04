import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;



public class CSV2Arff {
	public static void main(String[] args) throws IOException{
		CSVLoader loader = new CSVLoader();
		//loader.setSource(new File("data/Three_activities_data_with__50_window_size_without_overlapping.csv"));
		loader.setSource(new File("data/Five_activities_data_with__50_window_size_without_overlapping.csv"));
		Instances data = loader.getDataSet();
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		//saver.setFile(new File("data/Three_activities_data_with__50_window_size_without_overlapping.arff"));
		saver.setFile(new File("data/Five_activities_data_with__50_window_size_without_overlapping.arff"));
		saver.writeBatch();
		System.out.println("Converted from csv to arff format");
		
	}
}


