package cn.ac.ict.textclass.train;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.textclass.classifier.Softmax;
import cn.ac.ict.textclass.usage.Constants;

public class TrainSoftmax {
	private String dir = Constants.SOFTMAX_DIR;
	private int K;
	private double x[][];
	private int y[];
	private String [] category;
	public static double[] readProb(File f,int K) throws IOException{
		double [] p = new double[K];
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(f),"utf-8"));
		
		String line = br.readLine();
		String arr[] = line.split(" ");
		
		for( int i = 0; i < Constants.LDA_TOPIC_NUM_K; ++ i ){
			if( arr[i].equals("NaN")){
				System.out.println();
				System.out.println(f.getName() + " " + line);
				return null;
			}
			p[i] = Double.parseDouble(arr[i]);
		}
		return p;
	}
	
	public void train() throws IOException{
		int M = Constants.LDA_TOPIC_NUM_K;
		
		File[] dir_list = (new File(Constants.LDA_PHI_DIR)).listFiles();
        
		K = dir_list.length;
		category = new String[K];
		List<double[]> x_list = new ArrayList<double[]>();
		List<Integer> y_list = new ArrayList<Integer>();
		
		
		int cate_id = 0;
		
		int maxcount = 1000;
		int maxcopy = 5;
        for( File d: dir_list){
        	int count = 0;
        	int copy = 0;
        	while ( count <= maxcount && copy < maxcopy ){
        		copy ++;
	        	for( File f: d.listFiles() ){
	        		if( count ++ > maxcount ) break;
	        		double pzd[] = readProb(f,M);
	        		if( pzd == null ) continue;
	        		x_list.add(pzd);
	        		y_list.add( cate_id );
	        		System.out.println(cate_id);
	        		if( pzd == null ){
	        			 System.out.println(d.getName());
	        			 System.exit(1);
	        		}
	        		
	        	}
        	}
        	category[cate_id] = d.getName();
        	cate_id ++;
        	
        }
        x = new double[x_list.size()][];
		y = new int[y_list.size()];
		x_list.toArray(x);
		for( int i = 0; i < y.length; ++ i ){
			y[i] = y_list.get(i);
		}
        Softmax softmax = new Softmax();
        softmax.config(K-1, M, Constants.SOFTMAX_ITER, Constants.SOFTMAX_ALPHA,Constants.SOFTMAX_LAMBDA);
        softmax.train(x, y);
        double[][] theta = softmax.getTheta();

        writeTheta(dir,"theta",theta);
        writeCategory(dir,"id_category");
	}
	
	public void writeTheta(String dir_name,String filename,double [][]theta) throws IOException{
		File dir = new File(dir_name);
		if( !dir.exists() ) dir.mkdirs();
		
		BufferedWriter br = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( dir_name+filename), "utf-8"));
		for( double t[]: theta){
			for( double d: t)
				br.write( d +" ");
			br.write("\n");
		}
		br.close();
	}
	
	public void writeCategory(String dir_name,String filename) throws IOException{
		File dir = new File(dir_name);
		if( !dir.exists() ) dir.mkdirs();
		
		BufferedWriter br = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( dir_name+filename), "utf-8"));
		for( int i = 0; i < category.length; ++ i ){
			
			br.write( i +" " + category[i]);
			br.write("\n");
		}
		br.close();
	}
	
	public static void main(String[] args) throws IOException{
		TrainSoftmax train_softmax = new TrainSoftmax();
		train_softmax.train();
	}
}
