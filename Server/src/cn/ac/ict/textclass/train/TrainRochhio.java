package cn.ac.ict.textclass.train;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.lda.LDAGibbsSampler;
import cn.ac.ict.lda.TopicWordSelector;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.Vocabulary;
import cn.ac.ict.text.inference.DocumentLoader;
import cn.ac.ict.textclass.usage.Constants;

public class TrainRochhio {
//	public static String dir = "D:/30w/";

	public static double[] readProb(File f,int K) throws IOException{
		double [] p = new double[K];
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(f),"utf-8"));
		
		String line = br.readLine();
		String arr[] = line.split(" ");
		for( int i = 0; i < Constants.LDA_TOPIC_NUM_K; ++ i ){
			p[i] = Double.parseDouble(arr[i]);
		}
		return p;
	}

	
	public static void train() throws IOException{
        File[] dir_list = (new File(Constants.LDA_PHI_DIR)).listFiles();
        
        for( File d: dir_list){
        	double [] pzc = new double[Constants.LDA_TOPIC_NUM_K];
        	int count = 0;
        	for( File f: d.listFiles() ){
        		double pzd[] = readProb(f,Constants.LDA_TOPIC_NUM_K);
    	        for(int i = 0; i < pzd.length; ++ i ){
    	        	pzc[i] += pzd[i];
    	        }
    	        count ++ ;
        	}
        	for( int i = 0; i < Constants.LDA_TOPIC_NUM_K; ++ i ){
        		pzc[i] /= count;
        	}
        	
        	write(Constants.ROCCHIO_PROTO_DIR + d.getName(), "/avg_phi", pzc);
        }
        
	}
	
	public static void write(String dir_name,String filename,double pzd[]) throws IOException{
		File dir = new File(dir_name);
		if( !dir.exists() ) dir.mkdirs();
		
		BufferedWriter br = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( dir_name+filename), "utf-8"));
		for( double d: pzd){
			br.write( d +" ");
		}
		br.close();
	}
	public static void main(String args[]) throws IOException{
//		StopWordSingleton.singleton.loadStopWords();
//		train();
		train();
//		test();
	}

}
