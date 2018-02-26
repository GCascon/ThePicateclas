package the.picateclas.pizzaslices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Pizza {

    private Cell[][] cells;
    private int rows;
    private int columns;
    private int minIngredientsPerSlice;
    private int maxCellsPerSlice;
    private Map<String,List<Cell>> ingredientMap;

    public Pizza(List<String> lines) {
        int count = 0;
        for (String line : lines) {            
            if (count == 0) {
                StringTokenizer st = new StringTokenizer(line);                
                rows = Integer.parseInt(st.nextToken());
                columns = Integer.parseInt(st.nextToken());
                cells = new Cell[rows][columns];
                minIngredientsPerSlice = Integer.parseInt(st.nextToken());
                maxCellsPerSlice = Integer.parseInt(st.nextToken());
                ingredientMap=new HashMap<>();
            } else {                
                for (int i = 0; i < columns; i++) {
                    Cell cell=new Cell(String.valueOf(line.charAt(i)));
                    cells[count-1][i]=cell;
                    List<Cell> ingredientList=ingredientMap.get(cell.getIngredient());
                    if(ingredientList==null){
                        ingredientList=new ArrayList<>();
                        ingredientList.add(cell);
                        ingredientMap.put(cell.getIngredient(), ingredientList);
                    }else{
                        ingredientList.add(cell);
                        ingredientMap.put(cell.getIngredient(), ingredientList);
                    }
                }
            }
            count++; 
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getMinIngredientsPerSlice() {
        return minIngredientsPerSlice;
    }

    public void setMinIngredientsPerSlice(int minIngredientsPerSlice) {
        this.minIngredientsPerSlice = minIngredientsPerSlice;
    }

    public int getMaxCellsPerSlice() {
        return maxCellsPerSlice;
    }

    public void setMaxCellsPerSlice(int maxCellsPerSlice) {
        this.maxCellsPerSlice = maxCellsPerSlice;
    }

    public Map<String, List<Cell>> getIngredientMap() {
        return ingredientMap;
    }

    public void setIngredientMap(Map<String, List<Cell>> ingredientMap) {
        this.ingredientMap = ingredientMap;
    }
    

    @Override
    public String toString() {
        String cellsToString="\n";
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                cellsToString+=cells[i][j];
            }
            cellsToString+="\n";
        }
        return "Pizza{" + "cells=" + cellsToString + ", rows=" + rows + ", columns=" + columns + ", minIngredientsPerSlice=" + minIngredientsPerSlice + ", maxCellsPerSlice=" + maxCellsPerSlice + '}';
    }
    
    
}



