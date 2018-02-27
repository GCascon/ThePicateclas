package the.picateclas.pizzaslices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import the.picateclas.util.FactoresPrimos;

public class PizzaCutter {
	Integer actualRow=0;
    Integer actualCol=0;
    
    public List<Slice> cutPizza(Pizza pizza){
        
                
        //1-Calcular numero de porciones: (int)(R*C/H) [+ 1 (si resto > 0)]
        int numSlices=pizza.getRows()*pizza.getColumns()/pizza.getMaxCellsPerSlice();
        int mod=pizza.getRows()*pizza.getColumns()%pizza.getMaxCellsPerSlice();
        int smallSlicesCount=mod!=0?1:0;
        System.out.println("numSlices="+numSlices);
        System.out.println("mod="+mod);
        
        //2-Solucion ideal
        System.out.println("The ideal solution would be "+numSlices
                +" slices of "+pizza.getMaxCellsPerSlice()+" cells");
        
        if(smallSlicesCount==1){
            System.out.println("... and one slice of "+mod+" cells");
        }                
        
        //3-Calcular slices posibles
        List<Slice> possibilities=getPossibleSlices(pizza, mod, 0);
        
        int bigSlices=0;
        int smallSlices=0;
        int maxArea=0;
        int tempRow=0;
        int tempCol=0;
        
        Stack<Slice> bestStack=null;
        Stack<Slice> resultStack=new Stack<>();
        Stack<Slice> slicesStack=new Stack<>();
        slicesStack.addAll(possibilities);
        
        //4-Iterar hasta obtener solucion ideal o fin de posibilidades (Backtrack. Stack de slices)        
        while(!(maxArea==pizza.getRows()*pizza.getColumns()) &&
                !slicesStack.isEmpty()){
            Slice s=slicesStack.pop();
            
            if(s.getLevel()<(bigSlices+smallSlices)) {
            	undoSlice(pizza,resultStack.pop());
            	if(pizza.getMaxCellsPerSlice()==s.getRows()*s.getColumns()){
                    bigSlices--;
                }else{
                    smallSlices--;
                }
            }
            
            tempCol=actualCol;
            tempRow=actualRow;
            if (moveCursor(pizza ,s)) {
            	s.setR1(actualRow);
                s.setC1(actualCol);
                s.setR2(actualRow+(s.getRows()-1));
                s.setC2(actualCol+(s.getColumns()-1));
                
                if(allowedSlice(pizza,s)){
                    //Reservar celdas    
                    cutSlice(pizza,s);
                    
                    //Actualizar contadores
                    if(pizza.getMaxCellsPerSlice()==s.getRows()*s.getColumns()){
                        bigSlices++;
                    }else{
                        smallSlices++;
                    }
                    
                    //Meter slice en el resultStack
                    resultStack.push(s);
                    
                    //Meter siguiente nivel en slicesStack
                    List<Slice> possibilitiesNext=getPossibleSlices(pizza, mod, bigSlices+smallSlices);
                    for(Slice sn: possibilitiesNext) {
                    	slicesStack.push(sn);
                    }
                    
                    
                    //Comprobar si la suma de areas del resultStack, es la mayor encontrada hasta el momento, si lo es 
                    //guardo una foto del stack actual
                    int resultStackArea=getArea(resultStack);
                    if(resultStackArea > maxArea) {
                    	maxArea=resultStackArea;
                    	bestStack=resultStack;                	
                    }
                }else {
                	//Deshacer movimiento de cursor
                	actualCol=tempCol;
                    actualRow=tempRow;
                }
            }    
        }        
        
        //Ejemplo.....
        /*List<Slice> slices=new ArrayList<>();
        slices.add(new Slice(3,2,0,0,2,1));
        slices.add(new Slice(3,1,0,2,2,2));
        slices.add(new Slice(3,2,0,3,2,4));
        return slices;*/
        
        if(bestStack==null) {//SIN SOLUCION!!!!
        	System.out.println("No solution found!!! :(");
        	return new ArrayList<Slice>();
        }
        
        return new ArrayList<Slice>(bestStack);
    }
    
    private int getArea(Stack<Slice> stack) {
    	int area=0;
    	for(Slice s: stack) {
    		area+=s.getRows()*s.getColumns();
    	}
    	return area;
    }
    
    private boolean moveCursor(Pizza pizza, Slice slice){
    	Cell[][] cells=pizza.getCells();
    	
        //Mover en horizontal y si llego al limite lo pongo debajo
    	if(actualCol+slice.getColumns()<=pizza.getColumns()) {
    		actualCol+=(slice.getColumns()-1);
    		return true;
    	}else {    		
    		for(int i=0;i<pizza.getRows();i++) {
    			if(cells[i][0].isReserved()==false) {
    				actualCol=0;
    				actualRow=i;
    				return true;
    			}
    		}
    	}
    	return false;        
    }
    
     
    private boolean allowedSlice(Pizza pizza, Slice slice){
        
        Cell[][] cells=pizza.getCells();
        Set<String> ingredients=pizza.getIngredientMap().keySet();
        Map<String,Integer> sliceIngredientCount=new HashMap<>();
        for(String ingredient:ingredients){
            sliceIngredientCount.put(ingredient, 0);
        }
        
        //Slice dentro de los limites de la pizza
        if(slice.getR2()>=pizza.getRows() || slice.getC2()>=pizza.getColumns()){
            return false;
        }
        
        //Cada cell solo en un slice (reserved=false)
        for(int i=slice.getR1();i<=slice.getR2();i++){
            for(int j=slice.getC1();j<=slice.getR2();j++){
                if(cells[i][j].isReserved()){
                    return false;
                }else{
                    Integer valor=sliceIngredientCount.get(cells[i][j].getIngredient());
                    sliceIngredientCount.put(cells[i][j].getIngredient(), valor+1);
                }
            }
        }
        
        //Cada slice debe tener al menos  pizza.minIngredientsPerSlice de cada ingrediente
        for(String ingredient:ingredients){
            if(sliceIngredientCount.get(ingredient)<pizza.getMinIngredientsPerSlice()){
                return false;
            }
        }
        
        return true;
    }
    
    private void cutSlice(Pizza pizza, Slice slice){
        Cell[][] cells=pizza.getCells();
        for(int i=slice.getR1();i<=slice.getR2();i++){
            for(int j=slice.getC1();j<=slice.getC2();j++){
                cells[i][j].setReserved(true);
            }
        }
    }
    
    private void undoSlice(Pizza pizza, Slice slice){
        Cell[][] cells=pizza.getCells();
        actualRow=slice.getR1();
        actualCol=slice.getC1();
        for(int i=slice.getR1();i<=slice.getR2();i++){
            for(int j=slice.getC1();j<=slice.getC2();j++){
                cells[i][j].setReserved(false);
            }
        }
    }
    
    private List<Slice> getPossibleSlices(Pizza pizza, int mod, int level){
        List<Slice> result=new ArrayList<>();        
        LinkedList<Integer> bigFactor = FactoresPrimos.descomponEnFactoresPrimos(pizza.getMaxCellsPerSlice()); 
        result.addAll(getSlicesFromFactors(pizza,bigFactor,level));
        if(mod!=0){
            LinkedList<Integer> smallFactor = FactoresPrimos.descomponEnFactoresPrimos(mod);
            result.addAll(getSlicesFromFactors(pizza,smallFactor,level));
        }
        return result;
    }
    
    private List<Slice> getSlicesFromFactors(Pizza pizza, List<Integer> factors, int level){
        Map<String, Slice> slices=new HashMap<>();
        
        for(int i=0;i<factors.size();i++){
            int r1=productoAnteriores(i,factors);
            int c1=productoSiguientes(i,factors);
            if(r1<=pizza.getRows() && c1<=pizza.getColumns()){                
                String key=r1+","+c1;
                if(!slices.containsKey(key)){                    
                    slices.put(key, new Slice(r1,c1,level));
                }
            }                  
            
            int r2=c1;
            int c2=r1;
            if(r2<=pizza.getRows() && c2<=pizza.getColumns()){                
                String key=r2+","+c2;
                if(!slices.containsKey(key)){
                    slices.put(key, new Slice(r2,c2,level));
                }
            }  
        }
        return new ArrayList<Slice>(slices.values());
    }
    
    private int productoAnteriores(int p, List<Integer> lista){
        int producto=1;
        for(int i=p;i>=0;i--){
            producto*=lista.get(i);
        }
        return producto;
    }
    
    private int productoSiguientes(int p, List<Integer> lista){
        int producto=1;
        for(int i=p+1;i<lista.size();i++){
            producto*=lista.get(i);
        }
        return producto;
    }
}
