package cn.ac.ict.text.train;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.ac.ict.text.StopWordSingleton;
import cn.ac.ict.text.TextDocuReaderSingleton;

public class DocuVocaLoader {
	private static Logger logger = LogManager.getLogger();
	public DocuVocaLoader(){}
	
	public File[] getFileList(String dir){
		
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
	
	
	/**
	 * load all documents from directory
	 * @param dir_path the path of directory 
	 */
	public void loadDocuments(String dir_path){
		File[] file_list = getFileList(dir_path);
		if( null == file_list ) return;
		class Pair{
			int id,count;
		}
		TextDocuReaderSingleton singleton = TextDocuReaderSingleton.sigleton;
		
		Map<String,Pair> word_count_map = new HashMap<String,Pair>();//(word,count) set
//		Map<String,Integer> tmp_word_id = new HashMap<String,Integer>();
		int current_word_num = 0;//id should begin from 0
		int unk_threshold = 2;//if word count bigger than unk_threshold, the word is known.
		int kwn_word_count = 1;//involve particular word [UNK]
		
		/*
		 * firstly, construct temporary index corpus and vocabulary from raw corpus. 
		 * And, specify the unknown words witch has word count less equal to unk_threshold
		 */
		corpus = new int[file_list.length][];
		int doc_id = 0;
		for( File f: file_list){
			List<String> word_list = singleton.readDocument(f, "\\|\\|");
			
			/*for( String word :word_list){
				System.out.print(word+" ");
			}
			System.out.println();*/
			word_list = StopWordSingleton.singleton.filteStopWords(word_list);
			/*for( String word :word_list){
				System.out.print(word+" ");
			}
			System.out.println();*/
			
			corpus[doc_id] = new int[word_list.size()];
			int word_idx = 0;
			for(String word: word_list ){
				Pair p = word_count_map.get(word);
				if( p != null ){
//					System.out.println( word +"<==" + word_count_map.get(word).count );
					if( p.count == unk_threshold )//the newly known word was found, and only count once.
						kwn_word_count ++;
					p.count += 1;
//					System.out.println( word +"<==" + word_count_map.get(word).count );
				}else{
					p = new Pair();
					p.id = current_word_num ++ ;
					p.count = 1;
					word_count_map.put(word, p);
				}
				corpus[doc_id][word_idx ++ ] = p.id;
			}
			
			doc_id ++;
		}
		
		logger.info("There total different word count is : " + word_count_map.size());
		logger.info("There total KWN word count is : " +kwn_word_count);
		
		/*
		 *secondly, reconstruct the index corpus and vocabulary by replace UNK words to [UNK]. 
		 */
		vocabulary = new String[kwn_word_count];
		current_word_num = 0;
		vocabulary[ current_word_num ++ ] = "[UNK]";
		int tmp_vocabu_map[] = new int[word_count_map.size()];
		for( Map.Entry<String,Pair> entry : word_count_map.entrySet() ){
			Pair p = entry.getValue();
			if( p.count > unk_threshold ){
				tmp_vocabu_map[p.id] = current_word_num;
				vocabulary[ current_word_num ++ ] = entry.getKey();
			}else{
				tmp_vocabu_map[p.id ] = 0;
			}
		}
		
		for( int i = 0; i < corpus.length; ++ i ){
//			System.out.println("doc"+i+" :");
			for( int j = 0; j < corpus[i].length; ++ j ){
				int tmp_id = corpus[i][j];
				corpus[i][j] = tmp_vocabu_map[tmp_id];
//				System.out.print(vocabulary[corpus[i][j]]+" ");
			}
//			System.out.println();
		}
	}
	
	public int [][] getCorpus(){
		if( corpus == null ){
			logger.error("Documents haven't been loaded");
			return null;
		}
		return corpus;
	}
	
	public String [] getVocabulary(){
		if( vocabulary == null ){
			logger.error("Documents haven't been loaded");
			return null;
		}
		return vocabulary;
	}


	public static void main(String args[]){
		String test_dir = "./src/data";
		DocuVocaLoader loader = new DocuVocaLoader();
		loader.loadDocuments(test_dir);
//		File file_list[] = loader.getFileList(test_dir);
//		for( File f: file_list ){
//			System.out.println(f.getAbsolutePath());
//		}
	}
	
	
	public int [][]corpus;
	public String [] vocabulary;
}
