package cn.ac.ict.textclass.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.Vocabulary;
import cn.ac.ict.text.inference.DocumentLoader;

public class LDAInference {

	public static String dir = "D:/计算语言学数据/";
//	public static String dir = "./src/data/";
	
	public static int K = 100;
	public static int BURN_IN = 70;
	public static int ITER = 300;
	public static int SAMPLE_GAP = 15;

	
	public static void infer() throws IOException{
		Vocabulary vocabulary = new Vocabulary();
        
        vocabulary.loadVocabulary(dir + "vocabulary");
        int V = vocabulary.vocabulary.length;
        double phi[][] = LDAGibbsInference.loadProba(dir + "phi");
//        double pzw[][] = LDAGibbsInference.loadProba(dir + "pzw");
        File[] dir_list = (new File(dir+"cate-data-cut/")).listFiles();
        
        for( File d: dir_list){
        	for( File f: d.listFiles() ){
        		int[] doc = DocumentLoader.loadDocument(f.getAbsolutePath(), vocabulary);
        		if( doc.length == 0 || doc == null ) continue;
        		LDAGibbsInference ldainfer = new LDAGibbsInference(doc,V);
    	        
    	        ldainfer.config(K,BURN_IN,ITER,SAMPLE_GAP,100);    
    	        ldainfer.gibbsSample(50/K, phi);
    	        double pzd[] = ldainfer.normalizeTheta();
    	        write(dir + "cate-phi/" + d.getName(), "/"+f.getName(),pzd );
    	    }
        	
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
		StopWordSingleton.singleton.loadStopWords(dir+"stopword.txt");
		infer();
	}
}
