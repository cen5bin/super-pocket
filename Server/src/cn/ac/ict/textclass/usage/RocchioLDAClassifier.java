package cn.ac.ict.textclass.usage;

import java.io.BufferedReader;
import java.io.File;
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
import cn.ac.ict.textclass.classifier.Rocchio;
import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.sim.JensenShanonSimilarity;
import cn.ac.ict.textclass.sim.KullbackLeiblerSimilarity;
import cn.ac.ict.textclass.sim.Similarity;

public class RocchioLDAClassifier implements ClassifierInterface{
	
	public static final double proto[][];
	public static final String category[];
	static {
		File dir = new File(Constants.ROCCHIO_PROTO_DIR);
		File [] cate_dirs = dir.listFiles();
		proto = new double[cate_dirs.length][Constants.LDA_TOPIC_NUM_K];
		category = new String[cate_dirs.length];
		int cate_id = 0;
		try{
			for( File d : cate_dirs ){
				String avg_phi_name = d.getAbsolutePath() + "/avg_phi";
				BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(avg_phi_name),"utf-8"));
				String line = br.readLine();
				String arr[] = line.split(" ");
				for( int i = 0; i < Constants.LDA_TOPIC_NUM_K; ++ i ){
					proto[cate_id][i] = Double.parseDouble(arr[i]);
				}
				category[cate_id ++] = d.getName();
			}
		}catch( Exception ex){
			ex.printStackTrace();
		}
	}

	private Similarity similarity;
	public RocchioLDAClassifier(Similarity similarity){
		this.similarity = similarity;
	}
	
	@Override
	public ArrayList<String> classify(String title, String content) {
		content = JiebaKit.divide(content);
		int doc[] = getVector(content);
		if( doc.length == 0 || doc == null ) return null;
		
        
		LDAGibbsInference infer = new LDAGibbsInference(doc,Constants.VOCAB_SIZE);
		infer.config(Constants.LDA_TOPIC_NUM_K, Constants.LDA_BURN_IN, 
				Constants.LDA_ITER, Constants.LDA_SAMPLE_GAP, 100);
		infer.gibbsSample(50/Constants.LDA_TOPIC_NUM_K, Constants.LDA_PHI);
		double pzd[] = infer.normalizeTheta();
		Rocchio rocchio = new Rocchio(similarity);
		rocchio.setProto(proto);
		int[] clz = rocchio.classify(pzd,3);
		ArrayList<String> res = new ArrayList<String>();
		for( int c : clz){
			res.add(category[c]);
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
