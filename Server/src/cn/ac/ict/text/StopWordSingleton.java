package cn.ac.ict.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * StopWordSingleton is a singleton which load stopwords and provide methond to filter stopwords.
 * If you want to use it please load stopwords before using any methods.
 * @author GuoTianyou
 *
 */
public class StopWordSingleton {
	public static StopWordSingleton singleton = new StopWordSingleton();
	private StopWordSingleton(){
//		File file = new File("");
//		System.out.println(file.getAbsolutePath());
		stopwords = new HashSet<String>();
//		loadStopWords("./src/cn/ac/ict/text/stopword.txt");
	}
	
	public void loadStopWords(String file){
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String line;
			while( ( line = br.readLine() ) != null ){
				stopwords.add(line.trim());
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public List<String> filteStopWords(List<String> word_list){
		
		List<String> nword_list = new ArrayList<String>();
		for( String word: word_list){
			if( !stopwords.contains(word) ){
				nword_list.add(word);
			}
		}
		return nword_list;
	}
	private Set<String> stopwords = null;
}
