package cn.ac.ict.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Vocabulary {
	public String [] vocabulary;
	public HashMap<String,Integer> wordId;
	public static void dumpVocabulary(String filename,String voca[]){
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"utf-8"));
			
			for( int i = 0; i < voca.length; ++ i ){
				bw.append(i + " " + voca[i] );
				bw.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if( bw != null )
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	@SuppressWarnings("resource")
	public void loadVocabulary(String filename ){
		ArrayList<String> word_list = new ArrayList<String>();
		wordId = new HashMap<String,Integer>();
		BufferedReader br = null;
		try {
			br = new BufferedReader( new InputStreamReader(new FileInputStream(filename),"utf-8") );
			String line;
			while( (line = br.readLine() ) != null ){
				String arr [] = line.split(" ");
				if( arr.length < 2) {
					System.out.println(line + ", word " + arr[0] + ": ");
					word_list.add(" ");
					wordId.put(" ",Integer.parseInt(arr[0]));
					continue;
				}
				word_list.add(arr[1]);
				wordId.put(arr[1],Integer.parseInt(arr[0]));
			}
			vocabulary = new String[word_list.size()];
			word_list.toArray(vocabulary);
		/*	for( String w: word_list)
				System.out.println(w+" ");
			System.out.println();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
