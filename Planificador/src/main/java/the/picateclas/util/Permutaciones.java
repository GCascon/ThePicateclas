package the.picateclas.util;

import java.util.ArrayList;
import java.util.List;

public class Permutaciones {

	public static void main(String[] args) {
		List<Integer> prueba=new ArrayList<>();
		prueba.add(1);
		
		
		System.out.println(permutaciones(2,prueba));
	}
	
	public static <T> List<List<T>> permutaciones(int n, List<T> lista){
		List<List<T>> permutada=new ArrayList<>();
		for(int pivote=0;pivote<=lista.size()-n;pivote++) {			
			for(int j=pivote+1;j<=lista.size()-(n-1);j++) 
			{
				List<T> pn=new ArrayList<>();
				pn.add(lista.get(pivote));
				for(int i=0;i<(n-1);i++) {									
					pn.add(lista.get(j+i));					
				}
				permutada.add(pn);
			}
		}
		return permutada;
	}

}
