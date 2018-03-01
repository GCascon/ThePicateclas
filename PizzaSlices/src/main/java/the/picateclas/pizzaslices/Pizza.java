package the.picateclas.pizzaslices;

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

    private int minCellsPerSlice;

    private Map<String, Integer> ingredientMap;

    private long cuttedCells;

    public Pizza(List<String> lines) {
        long cuttedCells = 0;
        int count = 0;
        for (String line : lines) {
            if (count == 0) {
                StringTokenizer st = new StringTokenizer(line);
                rows = Integer.parseInt(st.nextToken());
                columns = Integer.parseInt(st.nextToken());
                cells = new Cell[rows][columns];
                minIngredientsPerSlice = Integer.parseInt(st.nextToken());
                minCellsPerSlice = minIngredientsPerSlice * 2;
                maxCellsPerSlice = Integer.parseInt(st.nextToken());
                ingredientMap = new HashMap<>();
            } else {
                for (int i = 0; i < columns; i++) {
                    Cell cell = new Cell(String.valueOf(line.charAt(i)));
                    cells[count - 1][i] = cell;
                    Integer numberOfElements = ingredientMap.get(cell.getIngredient());
                    if (numberOfElements == null) {
                        ingredientMap.put(cell.getIngredient(), 1);
                    } else {
                        ingredientMap.put(cell.getIngredient(), numberOfElements + 1);
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

    public Map<String, Integer> getIngredientMap() {
        return ingredientMap;
    }

    public void setIngredientMap(Map<String, Integer> ingredientMap) {
        this.ingredientMap = ingredientMap;
    }

    public int getMinCellsPerSlice() {
        return minCellsPerSlice;
    }

    public void setMinCellsPerSlice(int minCellsPerSlice) {
        this.minCellsPerSlice = minCellsPerSlice;
    }

    public long getCuttedCells() {
        return cuttedCells;
    }

    public void setCuttedCells(long cuttedCells) {
        this.cuttedCells = cuttedCells;
    }

    @Override
    public String toString() {
        String cellsToString = "\n";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cellsToString += cells[i][j];
            }
            cellsToString += "\n";
        }
        return "Pizza{" + "cells=" + cellsToString + ", rows=" + rows + ", columns=" + columns + ", minIngredientsPerSlice="
                + minIngredientsPerSlice + ", maxCellsPerSlice=" + maxCellsPerSlice + '}';
    }

}
