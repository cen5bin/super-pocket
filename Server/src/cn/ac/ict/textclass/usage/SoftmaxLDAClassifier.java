package cn.ac.ict.textclass.usage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.superpocket.classifier.ClassifierInterface;
import com.superpocket.kit.JiebaKit;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.textclass.algorithm.TopKSelector;
import cn.ac.ict.textclass.classifier.Softmax;

public class SoftmaxLDAClassifier implements ClassifierInterface{

	private static final double[][] theta;
	private static final String []category;
	private static final Softmax softmax;
	private static final int K;
	static{
		String cate_file = Constants.SOFTMAX_DIR + "id_category";
		category = readCategory(cate_file);
		String theta_file = Constants.SOFTMAX_DIR + "theta";
		K = category.length - 1;
		theta = readTheta( theta_file,K, Constants.LDA_TOPIC_NUM_K);
		softmax = new Softmax();
		softmax.config(K , Constants.LDA_TOPIC_NUM_K ,Constants.SOFTMAX_ITER, Constants.SOFTMAX_ALPHA,Constants.SOFTMAX_LAMBDA);
		softmax.setTheta(theta);
	}
	
	public static double [][] readTheta( String filename,int K,int M ){
		BufferedReader br;
		double theta[][] = new double[K][M];
		try {
			br = new BufferedReader(new InputStreamReader( new FileInputStream(filename),"utf-8"));
			String line ;
			int k = 0;
			while( (line = br.readLine()) != null ){
				String arr[] = line.split(" ");
				for( int m = 0; m < M; ++ m ){
					theta[k][m] = Double.parseDouble(arr[m]);
				}
				k ++;
			}

			return theta;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String [] readCategory(String filename){
		BufferedReader br;
		List<String> cate_list = new ArrayList<String>();
		try {
			br = new BufferedReader(new InputStreamReader( new FileInputStream(filename),"utf-8"));
			String line ;
			while( (line = br.readLine()) != null ){
				String arr[] = line.split(" ");
				cate_list.add(arr[1]);
			}
			
			String cates[] = new String[cate_list.size()];
			cate_list.toArray(cates);
			return cates;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public ArrayList<String>  classify(String title, String content) {
		
		content = JiebaKit.divide(content);
		
		int doc[] = getVector(content);
		if( doc.length == 0 || doc == null ) return null;
		
        
		LDAGibbsInference infer = new LDAGibbsInference(doc,Constants.VOCAB_SIZE);
		infer.config(Constants.LDA_TOPIC_NUM_K, Constants.LDA_BURN_IN, 
				Constants.LDA_ITER, Constants.LDA_SAMPLE_GAP, 100);
		infer.gibbsSample(50/Constants.LDA_TOPIC_NUM_K, Constants.LDA_PHI);
		double pzd[] = infer.normalizeTheta();
		double p[] = softmax.predict(pzd);
		
		double max = 0;
		double sum = 0;
		int c = 0;
		double []pt = new double[p.length + 1];
		for( int i = 0; i < p.length; ++ i ){
			sum += p[i];
			pt[i] = p[i];
//			System.out.print(p[i] + " ");
//			if( max < p[i])
//			{
//				max = p[i];
//				c = i;
//			}
		}
		pt[p.length] = 1 - sum;
		TopKSelector selector = new TopKSelector(3);
		int clz[] = selector.getTopK(pt);
//		if( 1 - sum > max ) c = K;
//		System.out.println(1-sum);
		ArrayList<String> res = new ArrayList<String>();
		for( int ct : clz){
			res.add(category[ct]);
		}
		return res;
	}

	private int[] getVector(String content){
		List<String> word_list = null;
		word_list = StopWordSingleton.singleton.filterStopWords(content.split("\\|\\|"));
		
		int doc[] = new int[word_list.size()];
		int idx = 0;
		for( String word: word_list ){
			Integer wid = Constants.VOCAB.wordId.get(word);
			if( wid == null ) doc[idx++] = 0;
			else doc[idx++] = wid;
		}
		
		return doc;
	}

	public int classify(List<String[]> class_docs, String content) {
		// TODO Auto-generated method stub
		return -1;
	}
}
