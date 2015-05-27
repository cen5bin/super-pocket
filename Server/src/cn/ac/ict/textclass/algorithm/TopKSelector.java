package cn.ac.ict.textclass.algorithm;

public class TopKSelector {

	public TopKSelector(int k){
		K = k;
		heap = new int[K+1];
	}
	
	public int[] getTopK(double phi[]){
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
	
	
	/**
	 * heap store the top k index of phi's elements
	 */
	private int heap[];
	private double phi[];
	private int K;
}
