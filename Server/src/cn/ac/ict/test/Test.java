package cn.ac.ict.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.lda.LDAGibbsSampler;
import cn.ac.ict.lda.TopicWordSelector;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.Vocabulary;
import cn.ac.ict.text.inference.DocumentLoader;
import cn.ac.ict.text.train.DocuVocaLoader;

public class Test {
//	public static String dir = "D:/30w/";
	public static String dir = "D:/计算语言学数据/";
//	public static String dir = "./src/data/";
	
	public static int K = 100;
	public static int BURN_IN = 360;
	public static int ITER = 1520;
	public static int SAMPLE_GAP = 5;
	
	public static void train(){
		String test_dir = dir +"train";
		DocuVocaLoader loader = new DocuVocaLoader();
		loader.loadDocuments(test_dir);
		// words in documents 
        int[][] documents = loader.getCorpus();
        String voca[] = loader.getVocabulary();
        int M = documents.length;
        int V = voca.length;
        LDAGibbsSampler lda = new LDAGibbsSampler(documents,V);
        lda.config(K,BURN_IN,ITER,SAMPLE_GAP,50);
        
        lda.gibbsSample(50/K, .01);
        //输出模型参数，论文中式 （81）与（82）  
        double[][] theta = lda.normalizeTheta();
        double[][] phi = lda.normalizePhi();  
        double [][]pzw = lda.normalizePZW();
        TopicWordSelector selector = new TopicWordSelector(10);
        for( int k = 0; k < phi.length; ++ k ){
        	int topic_words[] = selector.getTopicWord(phi[k]);
        	for( int i : topic_words )
        		System.out.print( voca[i] +" ");
        	System.out.println();
        }
        
        lda.dumpProba(dir + "phi", phi);
        lda.dumpProba(dir + "pzw", pzw);

        Vocabulary.dumpVocabulary(dir + "vocabulary", voca);
	}
	
	public static void test(){
		Vocabulary vocabulary = new Vocabulary();
        
        vocabulary.loadVocabulary(dir + "vocabulary");
        int V = vocabulary.vocabulary.length;
        
        File[] file_list = (new File(dir+"test")).listFiles();
        double phi[][] = LDAGibbsInference.loadProba(dir + "phi");
        double pzw[][] = LDAGibbsInference.loadProba(dir + "pzw");
        TopicWordSelector selector = new TopicWordSelector(100);
        for( File f: file_list){
	        int doc[] = DocumentLoader.loadDocument(f.getAbsolutePath(), vocabulary);
	        LDAGibbsInference ldainfer = new LDAGibbsInference(doc,V);
	        
	        ldainfer.config(K,BURN_IN,ITER,SAMPLE_GAP,30);    
	        ldainfer.gibbsSample(50/K, phi);
	        double pzd[] = ldainfer.normalizeTheta();
	        
	        Set<Integer> wset = new HashSet<Integer>();	
	        for( int w : doc ){
	        	wset.add(w);
	        }
	        Integer wsetarr[] = new Integer[wset.size()];
	        wset.toArray(wsetarr);
	        double p[][] = new double[4][wsetarr.length];
	        
	        for( int i = 0; i < wsetarr.length; ++ i ){
	        	int w = wsetarr[i];
	//        	p[i] = 0;
	        	for( int k = 0; k < K; ++ k ){
	        		p[0][i] += pzw[k][w]*phi[k][w]*pzd[k];
	        		p[1][i] += phi[k][w]*pzd[k];
	        		p[2][i] += pzw[k][w]*pzd[k];
	        		p[3][i] += pzw[k][w];
	        	}
	        }
	        
	//        TopicWordSelector selector = new TopicWordSelector(200);
	        
	        int topic_words[] = selector.getTopicWord(p[0]);
	    	for( int i : topic_words )
	    		System.out.print( vocabulary.vocabulary[wsetarr[i]] +" ");
	    	System.out.println();
	    	topic_words = selector.getTopicWord(p[1]);
	    	for( int i : topic_words )
	    		System.out.print( vocabulary.vocabulary[wsetarr[i]] +" ");
	    	System.out.println();
	    	topic_words = selector.getTopicWord(p[2]);
	    	for( int i : topic_words )
	    		System.out.print( vocabulary.vocabulary[wsetarr[i]] +" ");
	    	System.out.println();
	    	topic_words = selector.getTopicWord(p[3]);
	    	for( int i : topic_words )
	    		System.out.print( vocabulary.vocabulary[wsetarr[i]] +" ");
	    	System.out.println();
        }
        	
	}
	public static void main(String args[]){
		StopWordSingleton.singleton.loadStopWords("./src/cn/ac/ict/text/stopword.txt");
		train();
//		test();
	}

}
