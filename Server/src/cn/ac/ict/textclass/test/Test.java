package cn.ac.ict.textclass.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.Vocabulary;
import cn.ac.ict.text.inference.DocumentLoader;
import cn.ac.ict.textclass.classifier.KNN;
import cn.ac.ict.textclass.classifier.Rocchio;
import cn.ac.ict.textclass.cluster.Kmeans;
import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.sim.Similarity;

public class Test {
	public static int K = 100;
	public static int BURN_IN = 160;
	public static int ITER = 520;
	public static int SAMPLE_GAP = 5;
	public static String dir = "./data/";
	public static Vocabulary voca = new Vocabulary();
	public static double vecs[][];
	public static File[] dirs;
	public static List<double[][]> docvec_list;
	

	public static void kmean(){
		voca.loadVocabulary(dir+"vocabulary");
		StopWordSingleton.singleton.loadStopWords(dir + "stopword.txt");
		double phi[][] = LDAGibbsInference.loadProba(dir+"phi");
		List<int[]> docs = DocumentLoader.loadDocuments(dir+"test", voca);
		vecs = new double[docs.size()][];
		int V = voca.vocabulary.length;
		int i = 0;
		for( int[] doc: docs){
			LDAGibbsInference infer = new LDAGibbsInference(doc,V);
			infer.config(K, BURN_IN, ITER, SAMPLE_GAP, 100);
			infer.gibbsSample(50.0/K, phi);
			vecs[i++] = infer.normalizeTheta();
		}
		Kmeans kmeans = new Kmeans(new CosineSimilarity());
		kmeans.run(5, vecs);
	}
	
	public static void Rocchio(){
		init();
		Rocchio rocchio = new Rocchio(new CosineSimilarity());
		for( double vec[]:vecs ){
			int c = rocchio.run(docvec_list, vec);
			System.out.print(dirs[c].getName() + " ");
		}
	}
	
	public static void init(){
		Vocabulary voca = new Vocabulary();
		voca.loadVocabulary(dir+"vocabulary");
		StopWordSingleton.singleton.loadStopWords(dir+"stopword.txt");
		double phi[][] = LDAGibbsInference.loadProba(dir+"phi");

		int V = voca.vocabulary.length;
		dirs = (new File(dir+"classes")).listFiles();
		docvec_list = new ArrayList<double[][]>();
		for( File f: dirs){
			List<int[]> docs = DocumentLoader.loadDocuments(f.getAbsolutePath(), voca);
			double vecs[][] = new double[docs.size()][];
			
			int i = 0;
			for( int[] doc: docs){
				LDAGibbsInference infer = new LDAGibbsInference(doc,V);
				infer.config(K, BURN_IN, ITER, SAMPLE_GAP, 100);
				infer.gibbsSample(50.0/K, phi);
				vecs[i++] = infer.normalizeTheta();
			}
			docvec_list.add(vecs);
		}
		
		List<int[]> docs = DocumentLoader.loadDocuments(dir + "test", voca);
		vecs = new double[docs.size()][];
		
		int i = 0;
		for( int[] doc: docs){
			LDAGibbsInference infer = new LDAGibbsInference(doc,V);
			infer.config(K, BURN_IN, ITER, SAMPLE_GAP, 100);
			infer.gibbsSample(50.0/K, phi);
			vecs[i++] = infer.normalizeTheta();
		}
		
	}
	
	public static void KNN(){
		init();
		
		Similarity similarity = new CosineSimilarity();//new KullbackLeiblerSimilarity();
		KNN knn = new KNN(similarity);
		for( double vec[]:vecs ){
			int c = knn.run(7,docvec_list, vec);
			System.out.print(dirs[c].getName() + " ");
		}
	}
	public static void main(String args[]){
		
		Rocchio();
		
	}
}
