package cn.ac.ict.textclass.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.classifier.ClassifierInterface;

import cn.ac.ict.lda.LDAGibbsInference;
import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.Vocabulary;
import cn.ac.ict.text.inference.DocumentLoader;
import cn.ac.ict.textclass.classifier.KNN;
import cn.ac.ict.textclass.classifier.Rocchio;
import cn.ac.ict.textclass.cluster.Kmeans;
import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.sim.JensenShanonSimilarity;
import cn.ac.ict.textclass.sim.KullbackLeiblerSimilarity;
import cn.ac.ict.textclass.sim.Similarity;
import cn.ac.ict.textclass.usage.KNNLDAClassifier;
import cn.ac.ict.textclass.usage.RocchioLDAClassifier;
import cn.ac.ict.textclass.usage.SoftmaxLDAClassifier;

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
			rocchio.train(docvec_list);
			int c = rocchio.classify(vec);
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
			knn.config(7,docvec_list);
			int c = knn.run(vec);
			System.out.print(dirs[c].getName() + " ");
		}
	}
	
	public static void testKNNLDA() throws IOException{
		dirs = (new File(dir+"classes")).listFiles();
		List<String[]> class_vecs = new ArrayList<String[]>();
		String category[] = new String[dirs.length];
		int cid = 0;
		for( File d: dirs){
			String [] corpus = new String[d.listFiles().length];
			int id = 0;
			for( File f:d.listFiles()){
				String content = getContent(f);
				corpus[id ++ ] = content;
			}
			category[cid ++ ] = d.getName();
			class_vecs.add(corpus);
		}
		String content = "||腾讯||体育||讯||北京||时间||11月||21日||，||小||牛||主场||123-120||逆转||火箭||，||诺维茨基||砍||35分||创||赛季||新高||，||NBA||(||微||博||)||生涯||总||得分||超越||雷||吉||-||米勒||，||NBA||历史||得分||榜||排名||提升||至||第15||位||"
				+ "||诺||维||茨||基||此役||只要||得||到||17分||，||便||可以||完成||对||雷吉||-||米勒||的||超越||"
				+ "||小牛||老板||库||班||赛前||坦言||，||诺维茨基||根本||不||关心||这样||的||里程碑||"
				+ "||没||错||，||作为||一||位||名人||堂||级||老将||，||诺维茨基||已经||能够||更加||平淡||的||对待||那些||历史||数据||，||但||对垒||进攻||火力||强劲||的||火箭||，||诺维茨基||必须||在||进攻||上||做出||更||大||的||贡献|| "
				+ "||诺||天王||今天||进入||状态||很快||，||首||节||就||8||中||5||砍||下||11分||"
				+ "||诺||维||茨||基||在||对||位||中||充分||展示||了||身高||和||技术||优势||，||右翼||持球||背||打||转身||“||金鸡||独立||”||连连||得手||"
				+ "||尽管||诺维茨基||手感||火热||，||但||小||牛||的||防线||难以||抵御||火箭||的||狂||轰||，||首||节||丢掉||了||40分||"
				+ "||诺||维||茨||基||打||得||很||心急||，||首||节||临近||结束||时||补||防||哈||登||吃||到||了||技术||犯规||"
				+ "||里程碑||时刻||在||第二||节||到来||，||诺维茨基||篮下||背||打||转身||杀||入||禁区||，||上||篮||打||进||还||造||犯规||完成||“||2+1||”||，||接||下来||反击||再次||冲||入||禁区||赚取||罚球||，||个人||得分||达到||16分||追||平||雷||吉||-||米勒||"
				+ "||第二||节||结束||前||2分||11秒||，||诺维茨基||罚球||命中||，||拿||到||第17||分||，||正式||超越||雷||吉||-||米勒||"
				+ "||诺||维||茨||基||上半场||得||到||17分||，||个人||火力||不||差||，||但||小||牛||防守||不见||起色||，||尤其||是||限制||霍华德||(||微||博||)||十分||糟糕||||这种||情况||在||第三||节||毫无||改善||，||达勒姆||波特||对||位||霍华德||连续||被||虐||，||“||魔||兽||”||前||3||节||11||次||投篮||全||中||，||火箭||拉开||两||位||数||分||差||"
				+ "||小牛||在||第四||节||绝地||反攻||，||采用||联防||压制||火箭||的||攻势||，||进攻||端||诺||维||茨||基||单||节||7||中||6||轰||下||14分||，||中||投||远射||连连||中||靶||，||如同||一||场||投篮||教学||课||，||技术||上||已经||妙||到||毫||巅||，||速度||不||快||弹跳||不||高||，||全||靠||手腕||手指||上||的||功夫||"
				+ "||在||诺维茨基超||强||投篮||的||带动||下||，||小||牛||第四||节||劈||落||36分||，||最后||时刻||完成||逆||袭||将||火箭||击落||"
				+ "||全场||比赛||，||诺维茨基||20||中||13||得||到||35分||7||篮板||4||助攻||，||创||本||赛季||得分||新高||，||再现||2011年||总||决赛||无坚不摧||的||神勇||"
				+ "||小牛||的||防守||不好||，||但||诺维茨基||硬是||用||神||准||的||投篮||将||比赛||局面||扭转||，||这||就是||冠军级||巨星||的||实力||||（||硬币||）||";
		
		ClassifierInterface classifier =new KNNLDAClassifier();
//		int c = classifier.classify(class_vecs, content);
//		System.out.println(category[c]);
	}
	
	public static String getContent(File f) throws  IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(f),"utf-8"));
		String content ="";
		String line;
		while( (line = br.readLine()) != null){
			content += line;
		}
		br.close();
		return content;
		
	}
	
	public static void test() throws IOException{
		System.out.println("begin");
		dirs = (new File(dir+"classes")).listFiles();
//		ClassifierInterface classifier = new RocchioLDAClassifier(new CosineSimilarity());
		ClassifierInterface classifier = new SoftmaxLDAClassifier();
//		ClassifierInterface classifier = new 
		for( File d: dirs){
			for( File f:d.listFiles()){
				String content = getContent(f);
				ArrayList<String> cate = classifier.classify("", content);
				System.out.println( d.getName() );
//				for( String c :cate)
				System.out.println(" " + cate);
			}
		}
	}
	
	private static final Logger logger = LogManager.getLogger();
	public static void main(String args[]) throws IOException{
//		testKNNLDA();
		//Rocchio();
		
		Scanner in = new Scanner(System.in);
		StringBuilder sb = new StringBuilder();
		String line = "";
		ClassifierInterface classifier = new RocchioLDAClassifier(new CosineSimilarity());
		ClassifierInterface clInterface1 = new SoftmaxLDAClassifier();
		while (in.hasNext()) {
			line = in.nextLine();
			if (line.equals("#END#")) {
				logger.debug(classifier.classify("", sb.toString()));
				logger.debug(clInterface1.classify("", sb.toString()));
				sb = new StringBuilder();
			}
			else sb.append(line);
		}
		
//test();		
		/*
		String cate = classifier.classify("","||腾讯||体育||讯||北京||时间||11月||21日||，||小||牛||主场||123-120||逆转||火箭||，||诺维茨基||砍||35分||创||赛季||新高||，||NBA||(||微||博||)||生涯||总||得分||超越||雷||吉||-||米勒||，||NBA||历史||得分||榜||排名||提升||至||第15||位||"
				+ "||诺||维||茨||基||此役||只要||得||到||17分||，||便||可以||完成||对||雷吉||-||米勒||的||超越||"
				+ "||小牛||老板||库||班||赛前||坦言||，||诺维茨基||根本||不||关心||这样||的||里程碑||"
				+ "||没||错||，||作为||一||位||名人||堂||级||老将||，||诺维茨基||已经||能够||更加||平淡||的||对待||那些||历史||数据||，||但||对垒||进攻||火力||强劲||的||火箭||，||诺维茨基||必须||在||进攻||上||做出||更||大||的||贡献|| "
				+ "||诺||天王||今天||进入||状态||很快||，||首||节||就||8||中||5||砍||下||11分||"
				+ "||诺||维||茨||基||在||对||位||中||充分||展示||了||身高||和||技术||优势||，||右翼||持球||背||打||转身||“||金鸡||独立||”||连连||得手||"
				+ "||尽管||诺维茨基||手感||火热||，||但||小||牛||的||防线||难以||抵御||火箭||的||狂||轰||，||首||节||丢掉||了||40分||"
				+ "||诺||维||茨||基||打||得||很||心急||，||首||节||临近||结束||时||补||防||哈||登||吃||到||了||技术||犯规||"
				+ "||里程碑||时刻||在||第二||节||到来||，||诺维茨基||篮下||背||打||转身||杀||入||禁区||，||上||篮||打||进||还||造||犯规||完成||“||2+1||”||，||接||下来||反击||再次||冲||入||禁区||赚取||罚球||，||个人||得分||达到||16分||追||平||雷||吉||-||米勒||"
				+ "||第二||节||结束||前||2分||11秒||，||诺维茨基||罚球||命中||，||拿||到||第17||分||，||正式||超越||雷||吉||-||米勒||"
				+ "||诺||维||茨||基||上半场||得||到||17分||，||个人||火力||不||差||，||但||小||牛||防守||不见||起色||，||尤其||是||限制||霍华德||(||微||博||)||十分||糟糕||||这种||情况||在||第三||节||毫无||改善||，||达勒姆||波特||对||位||霍华德||连续||被||虐||，||“||魔||兽||”||前||3||节||11||次||投篮||全||中||，||火箭||拉开||两||位||数||分||差||"
				+ "||小牛||在||第四||节||绝地||反攻||，||采用||联防||压制||火箭||的||攻势||，||进攻||端||诺||维||茨||基||单||节||7||中||6||轰||下||14分||，||中||投||远射||连连||中||靶||，||如同||一||场||投篮||教学||课||，||技术||上||已经||妙||到||毫||巅||，||速度||不||快||弹跳||不||高||，||全||靠||手腕||手指||上||的||功夫||"
				+ "||在||诺维茨基超||强||投篮||的||带动||下||，||小||牛||第四||节||劈||落||36分||，||最后||时刻||完成||逆||袭||将||火箭||击落||"
				+ "||全场||比赛||，||诺维茨基||20||中||13||得||到||35分||7||篮板||4||助攻||，||创||本||赛季||得分||新高||，||再现||2011年||总||决赛||无坚不摧||的||神勇||"
				+ "||小牛||的||防守||不好||，||但||诺维茨基||硬是||用||神||准||的||投篮||将||比赛||局面||扭转||，||这||就是||冠军级||巨星||的||实力||||（||硬币||）||");
		*/
//		String python = "各位||看官||们||，||大家||好||，||欢迎||大家||一||起来||听||大型||章回体||科技||小说|| ||：||Vim||。||上||一回||咱们||介绍||了||Vim||中||的||taglist||插件||.||。||这||一回||，||咱们||继续||说||Vim||的||插件||，||不过||我们||说||的||是||另外||一个||插件||：||NerdTree||。||好||了||，||还是||那句话||，||闲话||休提||，||言归||正转||。||让||我们||一起||talk|| ||Vim||吧||！||"
//				+ "||看官||们||，||所谓||的||NerdTree||就是||把||某个||目录||以及||目录||中||的||文件||或者||子目录||以||树状||的||形式||显示||出来||。||它||和||taglis||一样||，||是||一个||插件||。||下面||我们||先说||说||如何||安装||NerdTree||。||"
//				+ "||到||http||:||/||/||www||.||vim||.||org||/||scripts||/||script||.||php||?||script||_||id||=||1658||这个||网址||中||下载||该||插件||。||大小||为||四十多||KB||，||因此||很快||就||能||下载||到||本地||。||"
//				+ "||解压||下载||后||的||压缩包||。||然后||把||plugin||和||nerdtree||_||plugin||目录||下||的||NERD||_||tree||.||vim||、||fs||_||menu||.||vim||和||exec||_||menuitem||.||vim||一起||复制到||vim||的||plugin||目录||中||，||路径||：||/||usr||/||share||/||vim||/||vim74||/||plugin||/||。||再||把||syntax||目录||中||的||nerdtree||.||vim||复制到||vim||的||syntax||目录||中||。||这样||就||完成||了||插件||的||安装||。||因为||NerdTree||是||一个||插件||，||所以||只||需要||把||相关||的||文件||复制到||Vim||对应||的||目录||中||可以||。||这点||和||ctags||等||插件||的||安装||方法||不||一样||，||但是||和||上||一回||咱们||说||的||taglist||安装||方法||一样||。||"
//				+ "||3||.||使用||插件||：||使用||Vim||打开||一个||源代码||文件||。||在||命令||模式||下||输入||：||NERDTree||。||按下||回车||后||就||会||在||Vim||最||左侧||建立||一个||新窗口||。||窗口||中||显示||当前目录||中||的||子目录||和||文件||。||下面||是||具体||一个||截图||。||"
//				+ "||看官||们||从||图||中||可以||看到||整个||Vim||窗口||分为||左右两||部分||，||右边||是||源代码||文件||，||左边||就是||该||文件||所在||目录||信息||。||在||目录||窗口||中||可以||使用||k||,||j||上||下移||到||光标||。||当前||图中||的||光标||移到||了||一个||名叫||calibrate||.||c||的||文件||上||。||这时||可以||按下||o||键||，||然后||就||会||在||右边||的||窗口||中||打开||该||文件||。||图中||，||打开||的||是||Linux||源代码||中||的||一个||文件||，||看官||可以||看到||Linux||的||源代码||比较||多||，||使用||这个||插件||可以||方便||地||浏览||Linux||源代码||下||的||各个||目录||和||文件||，||而且||它||把||目录||以||目录||树||的||形式||显示||出来||，||显得||更||形象||。||"
//				+ "||看官||们||可以||依据||自己||的||习惯||配置||NerdTree||。||配置||方法||在||帮助文件||中写||的||很||详细||，||这里||不||介绍||了||。||配置||时||依据||帮助文件||中||的||方法||修改||vim||的||配置文件||就||可以||。||这点||和||Taglist||的||配置||方法||一样||。||"
//				+ "||各位||看官||，||关于||Vim||的||插件||：||NerdTree||，||今天||就||说||到||这里||为止||。||在||后面||的||章回||中||，||我们||还会||说||其它||的||Vim||插件||。||欲知||以后||还有||什么||插件||？||且||听||下回分解||。";
//		String cate = classifier.classify("", python);
//		System.out.println(cate);
	}
}
