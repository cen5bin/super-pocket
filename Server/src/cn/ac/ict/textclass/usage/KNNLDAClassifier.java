package cn.ac.ict.textclass.usage;

import java.util.ArrayList;
import java.util.List;

import com.superpocket.classifier.ClassifierInterface;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.textclass.classifier.KNN;
import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.sim.Similarity;

public class KNNLDAClassifier implements ClassifierInterface {
	private int K = 7;
	
	
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


	@Override
	public ArrayList<String> classify(String title, String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private double [] getPzd(int []doc){
		LDAGibbsInference infer = new LDAGibbsInference(doc,Constants.VOCAB_SIZE);
		infer.config(Constants.LDA_TOPIC_NUM_K, Constants.LDA_BURN_IN, 
				Constants.LDA_ITER, Constants.LDA_SAMPLE_GAP, 100);
		infer.gibbsSample(50/Constants.LDA_TOPIC_NUM_K, Constants.LDA_PHI);
		double pzd[] = infer.normalizeTheta();
		return pzd;
	}

	public int classify(List<String[]> class_docs, String content) {
		List<double[][]> docvec_list = new ArrayList<double[][]>();
		for( String[] docs : class_docs){
			List<double[]> corpus = new ArrayList<double[]>();
			for( String doc: docs){
				int doc_wid[] = getVector(doc);
				if( doc_wid == null || doc_wid.length == 0) continue;
				
				double []pzd = getPzd( doc_wid );
				corpus.add(pzd);
			}
			
			double [][] vecs = new double[corpus.size()][];
			corpus.toArray(vecs);
			docvec_list.add(vecs);
		}
		Similarity similarity = new CosineSimilarity();//new KullbackLeiblerSimilarity();
		KNN knn = new KNN(similarity);
		knn.config(K,docvec_list);
		int [] doc = getVector(content);
		if( doc == null || doc.length == 0 ) return -1;
		double []pzd = getPzd( doc);
		return knn.run(pzd);
	}
}
