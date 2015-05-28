package cn.ac.ict.text.inference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.TextDocuReaderSingleton;
import cn.ac.ict.text.Vocabulary;

public class DocumentLoader {
	private static Logger logger = LogManager.getLogger();
	public static  File[] getFileList(String dir){
		
		File dir_file = new File(dir);
		if( !dir_file.exists() ){
			logger.warn(dir_file + " does not exist.");
			return null;
		}
		else if( !dir_file.isDirectory() ){
			logger.warn(dir_file + " is not a directory.");
			return null;
		}
		
		File[] file_list = dir_file.listFiles();
		logger.info("List all files in directory " + dir_file );
		return file_list;
	}
	
	public static List<int[]> loadDocuments(String dirname,Vocabulary voca){
		File [] file_list = getFileList(dirname);
		if( file_list == null )
			return null;
		
		List<int[]> list = new ArrayList<int[]>();
		for( File f: file_list ){
			list.add(loadDocument(f.getAbsolutePath(),voca));
		}
		
		return list;
	}
	
	
	public static int [] loadDocument(String filename,Vocabulary voca){
		
		TextDocuReaderSingleton singleton = TextDocuReaderSingleton.sigleton;
	
		List<String> word_list = singleton.readDocument(new File(filename), "\\|\\|");
		word_list = StopWordSingleton.singleton.filteStopWords(word_list);
		
		int doc[] = new int[word_list.size()];
		int idx = 0;
		for( String word: word_list ){
			Integer wid = voca.wordId.get(word);
			if( wid == null ) doc[idx++] = 0;
			else doc[idx++] = wid;
		}
		
		return doc;
	}
	

	public static void main(String args[]){
		double small = Double.parseDouble("1.0E-12");
		System.out.println(small);
		System.out.printf("%.13f",small);
//		String test_dir = "./src/data";
//		DocumentLoader loader = new DocumentLoader();
//		loader.loadDocuments(test_dir);
//		File file_list[] = loader.getFileList(test_dir);
//		for( File f: file_list ){
//			System.out.println(f.getAbsolutePath());
//		}
	}
	
}
