package cn.ac.ict.textclass.usage;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.Vocabulary;

public class Constants {

	/*词汇表常量*/
	public static final Vocabulary VOCAB ;
	public static final int VOCAB_SIZE;
	
	public static final String DATA_ROOT_DIR = "/home/wubincen/计算语言学数据/";
	
	/*LDA常量*/
	public static int LDA_TOPIC_NUM_K = 100;
	public static int LDA_BURN_IN = 70;
	public static int LDA_ITER = 300;
	public static int LDA_SAMPLE_GAP = 15;
	public static final double [][]LDA_PHI;
	
	public static final double SOFTMAX_ALPHA = 0.01;
	public static final double SOFTMAX_LAMBDA = 2;
	public static final int SOFTMAX_ITER = 100;
	
	public static final String LDA_PHI_DIR = DATA_ROOT_DIR + "cate-phi";
	public static final String ROCCHIO_PROTO_DIR = DATA_ROOT_DIR+"cate-avg-phi/";
	public static final String SOFTMAX_DIR = DATA_ROOT_DIR + "softmax/";
	static {
		VOCAB = new Vocabulary();
		VOCAB.loadVocabulary(DATA_ROOT_DIR + "vocabulary");
        VOCAB_SIZE = VOCAB.vocabulary.length;
		LDA_PHI = LDAGibbsInference.loadProba(DATA_ROOT_DIR + "phi");
		StopWordSingleton.singleton.loadStopWords(DATA_ROOT_DIR +"stopword.txt");
	}
}
