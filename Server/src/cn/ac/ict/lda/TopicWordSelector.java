package cn.ac.ict.lda;

public class TopicWordSelector {

	public TopicWordSelector(int k){
		K = k;
		heap = new int[K+1];
	}
	/**
	 * 
	 * @param phi The probability of p(word|topic)
	 * @return top k topic words
	 */
	public int[] getTopicWord(double phi[]){
		this.phi = phi;
		int V = phi.length;
		int sz = 0;
		int i = 0;
		while( i < K && i < V ){
			heap[ ++ sz ] = i;
			heapAdjust( 1, sz);
			i ++;
		}
		
		buildHeap(sz);
		if( i < V ){
			
			for( ; i < V; ++ i){
				if( phi[i] > phi[ heap[1] ] ){
					heap[1] = i;
					heapAdjust(1,sz);
				}
			}
			
		}
		
		heapSort(sz);
		int res[] = new int[sz];
		for( i = 0; i < sz; ++ i ){
			res[i] = heap[i+1];
		}

		return res;
		
	}
	
	private void heapSort(int sz){
//		int res[] = new int[sz];
		while( sz >= 1 ){
			int x = heap[1];
			heap[1] = heap[sz];
			heap[sz] = x;
			heapAdjust(1,--sz);
		}
	}
	private void buildHeap(int sz){
		for( int p = sz/2; p >= 1; -- p ){
			heapAdjust(p,sz);
		}
	}
	private void heapAdjust(int p,int sz){
		
		if( p == 0 ) return ;
		
		while( p <= sz/2 ){
			int x = p*2;
			int y = p*2 + 1;
			
			if( y <= sz && phi[ heap[x] ] > phi[ heap[y] ] ){
				x = y;
			}
			if( x <= sz && phi[ heap[x] ] < phi[ heap[p]] ){
				y = heap[x];
				heap[x] = heap[p];
				heap[p] = y;
				p = x;
			}else{
				break;
			}
		}
	}
	
	public static void main(String args[]){
		double phi[] = { 1,2,3,4,5,6,7,8,9,10,11};
		TopicWordSelector selector = new TopicWordSelector(12);
		int topic_words[] = selector.getTopicWord(phi);
		System.out.println( "size = " + topic_words.length);
		for( int i: topic_words){
			System.out.println( phi[i]+" "  );
		}
		
		double []phi2 = { 2,2,15,14,1,11,2,7,8,9,10,3,4,5,6};
		topic_words = selector.getTopicWord(phi2);
		System.out.println( "size = " + topic_words.length);
		for( int i: topic_words){
			System.out.println( phi2[i]+" "  );
		}
		
		double []phi3 = { 2,2,15,14,2,7,8,4,5,6};
		topic_words = selector.getTopicWord(phi3);
		System.out.println( "size = " + topic_words.length);
		for( int i: topic_words){
			System.out.println( phi3[i]+" "  );
		}
	}
	
	/**
	 * heap store the top k index of phi's elements
	 */
	private int heap[];
	private double phi[];
	private int K;
}
