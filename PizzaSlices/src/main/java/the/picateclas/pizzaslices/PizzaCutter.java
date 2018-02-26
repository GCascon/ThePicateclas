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
    public List<Slice> cutPizza(Pizza pizza){
        List<Slice> slices=new ArrayList<>();
                
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
        List<Slice> possibilities=getPossibleSlices(pizza, mod);
        int actualRow=0;
        int actualCol=0;
        int bigSlices=0;
        int smallSlices=0;
        
        Stack<Slice> slicesStack=new Stack<>();
        slicesStack.addAll(possibilities);
        
        //4-Iterar hasta obtener solucion ideal o fin de posibilidades (Backtrack. Stack de slices)        
        while(!(bigSlices==numSlices && smallSlices==smallSlicesCount) &&
                !slicesStack.isEmpty()){
            Slice s=slicesStack.pop();
                        
            s.setR1(actualRow);
            s.setC1(actualCol);
            s.setR2(actualRow+(s.getRows()-1));
            s.setC2(actualCol+(s.getColumns()-1));
            
            if(allowedSlice(pizza,s)){
                //Reservar celdas    
                cutSlice(pizza,s);
                
                //Incrementar contadores
                moveCursor(pizza ,s , actualRow, actualCol);
                
                if(pizza.getMaxCellsPerSlice()==s.getRows()*s.getColumns()){
                    bigSlices++;
                }else{
                    smallSlices++;
                }
                slices.add(s);
            }
        }
        
        //Ejemplo.....
        slices.add(new Slice(3,2,0,0,2,1));
        slices.add(new Slice(3,1,0,2,2,2));
        slices.add(new Slice(3,2,0,3,2,4));
        return slices;
    }
    
    private void moveCursor(Pizza pizza, Slice slice, int actualRow, int actualCol){
        //Mover en horizontal
        actualCol+=slice.getColumns();
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
            for(int j=slice.getC1();j<=slice.getR2();j++){
                cells[i][j].setReserved(true);
            }
        }
    }
    
    private void undoSlice(Pizza pizza, Slice slice){
        Cell[][] cells=pizza.getCells();
        for(int i=slice.getR1();i<=slice.getR2();i++){
            for(int j=slice.getC1();j<=slice.getR2();j++){
                cells[i][j].setReserved(false);
            }
        }
    }
    
    private List<Slice> getPossibleSlices(Pizza pizza, int mod){
        List<Slice> result=new ArrayList<>();        
        LinkedList<Integer> bigFactor = FactoresPrimos.descomponEnFactoresPrimos(pizza.getMaxCellsPerSlice()); 
        result.addAll(getSlicesFromFactors(pizza,bigFactor));
        if(mod!=0){
            LinkedList<Integer> smallFactor = FactoresPrimos.descomponEnFactoresPrimos(mod);
            result.addAll(getSlicesFromFactors(pizza,smallFactor));
        }
        return result;
    }
    
    private List<Slice> getSlicesFromFactors(Pizza pizza, List<Integer> factors){
        Map<String, Slice> slices=new HashMap<>();
        
        for(int i=0;i<factors.size();i++){
            int r1=productoAnteriores(i,factors);
            int c1=productoSiguientes(i,factors);
            if(r1<pizza.getRows() && c1<pizza.getColumns()){                
                String key=r1+","+c1;
                if(!slices.containsKey(key)){                    
                    slices.put(key, new Slice(r1,c1));
                }
            }                  
            
            int r2=c1;
            int c2=r1;
            if(r2<pizza.getRows() && c2<pizza.getColumns()){                
                String key=r2+","+c2;
                if(!slices.containsKey(key)){
                    slices.put(key, new Slice(r2,c2));
                }
            }  
        }
        return new ArrayList(slices.values());
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
