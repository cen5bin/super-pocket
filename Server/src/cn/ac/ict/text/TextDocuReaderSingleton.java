package cn.ac.ict.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
public class TextDocuReaderSingleton {
	public static TextDocuReaderSingleton sigleton = new TextDocuReaderSingleton();
	private TextDocuReaderSingleton(){
		
	}
	
	public List<String> readDocument(File file,String split_regex){
		BufferedReader br = null;
		List<String> doc = new ArrayList<String>();
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String line = null;
			while( (line = br.readLine() ) != null ){
				String arr[] = line.split(split_regex);
				
				for( String word : arr ){
					doc.add(word);
				}
			}
			
			return doc;
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if( br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return null;
	}
	
}
