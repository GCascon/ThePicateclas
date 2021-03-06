package the.picateclas.pizzaslices;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import the.picateclas.util.FactoresPrimos;

public class PizzaCutter {

    List<Slice> teorical = null;

    int lastRow = 0;

    int lastCol = 0;

    boolean reverse = false;

    float cuttedArea = 0;

    long tiempoInicial = System.currentTimeMillis();

    long ultimoTiempo = System.currentTimeMillis();

    void imprimeMaximo(int maxArea, int numSlices) {
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoTiempo > 10000) {
            ultimoTiempo = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("Time: ").append((tiempoActual - tiempoInicial) / 1000).append(" s, MaxAreaFound: ").append(maxArea)
                    .append(", CuttedArea:").append(cuttedArea).append(", Actual Level: ").append(numSlices);
            System.out.println(sb.toString());
        }
    }

    public List<Slice> cutPizza(Pizza pizza, long timeout) {
        // Calcular slices posibles
        List<Slice> possibilities = getPossibleSlices(pizza, 0);

        int numSlices = 0;
        int maxArea = 0;

        // FIX0:Cambio Stack por ArrayDeque (not synchronized)
        ArrayDeque<Slice> bestStack = null;
        ArrayDeque<Slice> resultStack = new ArrayDeque<>();
        ArrayDeque<Slice> slicesStack = new ArrayDeque<>();
        slicesStack.addAll(possibilities);

        // 4-Iterar hasta obtener solucion ideal o fin de posibilidades (Backtrack. Stack de slices)
        while (!(maxArea == pizza.getRows() * pizza.getColumns()) && !slicesStack.isEmpty()
                && !(System.currentTimeMillis() - tiempoInicial > timeout)) {
            Slice s = slicesStack.pop();

            if (s.getLevel() < numSlices) {
                undoSlice(pizza, resultStack.pop());
                numSlices--;
            }

            if (isPossible(pizza, s)) {
                // Reservar celdas
                cutSlice(pizza, s);

                // Actualizar contadores
                numSlices++;

                // Meter slice en el resultStack
                resultStack.push(s);

                // Meter siguiente nivel en slicesStack
                List<Slice> possibilitiesNext = getPossibleSlices(pizza, numSlices);
                for (Slice sn : possibilitiesNext) {
                    slicesStack.push(sn);
                }

                // Comprobar si la suma de areas del resultStack, es la mayor encontrada hasta el momento, si lo es
                // guardo una foto del stack actual
                int resultStackArea = getArea(resultStack);
                if (resultStackArea > maxArea) {
                    maxArea = resultStackArea;
                    bestStack = resultStack;
                }
            }

            if (slicesStack.isEmpty()) {
                boolean finnish = false;
                if (lastCol < (pizza.getColumns() - 1)) {
                    lastCol++;
                } else {
                    if (lastRow < (pizza.getRows() - 1)) {
                        lastCol = 0;
                        lastRow++;
                    } else {
                        finnish = true;
                    }
                }
                if (!finnish) {
                    slicesStack.addAll(getPossibleSlices(pizza, 0));
                }
            }

            imprimeMaximo(maxArea, numSlices);
        }

        System.out.println("PizzaCutter Finished!");
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append((System.currentTimeMillis() - tiempoInicial) / 1000).append(" s, MaxAreaFound: ").append(maxArea)
                .append(", CuttedArea:").append(cuttedArea).append(", Actual Level: ").append(numSlices).append(" Stack:");
        System.out.println(sb.toString());

        if (bestStack == null) {
            // SIN SOLUCION!!!!
            System.out.println("No solution found!!! :(");
            return new ArrayList<Slice>();
        }

        return new ArrayList<Slice>(bestStack);
    }

    private boolean isPossible(Pizza pizza, Slice slice) {

        Cell[][] cells = pizza.getCells();

        // Cada cell solo en un slice (reserved=false)
        for (int i = slice.getR1(); i <= slice.getR2(); i++) {
            for (int j = slice.getC1(); j <= slice.getC2(); j++) {
                if (cells[i][j].isReserved()) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getArea(ArrayDeque<Slice> stack) {
        int area = 0;
        for (Slice s : stack) {
            area += s.getRows() * s.getColumns();
        }
        return area;
    }

    private List<Slice> findGaps(Pizza pizza, Slice slice) {
        Cell[][] cells = pizza.getCells();
        List<Slice> possibilities = new ArrayList<>();

        // From the last cut to the end
        for (int i = lastRow; i < pizza.getRows(); i++) {
            for (int j = lastCol; j < pizza.getColumns(); j++) {
                if (!cells[i][j].isReserved() && (i + slice.getRows()) <= pizza.getRows()
                        && (j + slice.getColumns()) <= pizza.getColumns()) {
                    Slice s = new Slice(slice.getRows(), slice.getColumns(), i, j, i + (slice.getRows() - 1), j + (slice.getColumns() - 1),
                            slice.getLevel());
                    if (allowedSlice(pizza, s)) {
                        possibilities.add(s);
                        break;
                    }
                }
            }
        }
        // From the beginning to the last cut
        if (possibilities.isEmpty()) {
            for (int i = 0; i < lastRow; i++) {
                for (int j = 0; j < lastCol; j++) {
                    if (!cells[i][j].isReserved() && (i + slice.getRows()) <= pizza.getRows()
                            && (j + slice.getColumns()) <= pizza.getColumns()) {
                        Slice s = new Slice(slice.getRows(), slice.getColumns(), i, j, i + (slice.getRows() - 1),
                                j + (slice.getColumns() - 1), slice.getLevel());
                        if (allowedSlice(pizza, s)) {
                            possibilities.add(s);
                            break;
                        }
                    }
                }
            }
        }
        return possibilities;
    }

    private boolean allowedSlice(Pizza pizza, Slice slice) {

        Cell[][] cells = pizza.getCells();
        Set<String> ingredients = pizza.getIngredientMap().keySet();
        Map<String, Integer> sliceIngredientCount = new HashMap<>();
        for (String ingredient : ingredients) {
            sliceIngredientCount.put(ingredient, 0);
        }

        // Slice dentro de los limites de la pizza
        if (slice.getR2() >= pizza.getRows() || slice.getC2() >= pizza.getColumns()) {
            return false;
        }

        // Contar ingredientes Slice
        for (int i = slice.getR1(); i <= slice.getR2(); i++) {
            for (int j = slice.getC1(); j <= slice.getC2(); j++) {
                Integer valor = sliceIngredientCount.get(cells[i][j].getIngredient());
                sliceIngredientCount.put(cells[i][j].getIngredient(), valor + 1);
            }
        }

        // Cada slice debe tener al menos pizza.minIngredientsPerSlice de cada ingrediente
        for (String ingredient : ingredients) {
            if (sliceIngredientCount.get(ingredient) < pizza.getMinIngredientsPerSlice()) {
                return false;
            }
        }

        return true;
    }

    private void setReserved(Pizza pizza, Slice slice, boolean value) {
        Cell[][] cells = pizza.getCells();
        for (int i = slice.getR1(); i <= slice.getR2(); i++) {
            for (int j = slice.getC1(); j <= slice.getC2(); j++) {
                cells[i][j].setReserved(value);
            }
        }
        lastRow = slice.getR1();
        lastCol = slice.getC1();
    }

    private void cutSlice(Pizza pizza, Slice slice) {
        pizza.setCuttedCells(pizza.getCuttedCells() + (slice.getRows() * slice.getColumns()));
        setReserved(pizza, slice, true);
    }

    private void undoSlice(Pizza pizza, Slice slice) {
        pizza.setCuttedCells(pizza.getCuttedCells() - (slice.getRows() * slice.getColumns()));
        setReserved(pizza, slice, false);
    }

    private List<Slice> getPossibleSlices(Pizza pizza, int level) {
        if (teorical == null) {
            teorical = new ArrayList<>();
            for (int i = pizza.getMinCellsPerSlice(); i <= pizza.getMaxCellsPerSlice(); i++) {
                LinkedList<Integer> factors = FactoresPrimos.descomponEnFactoresPrimos(i);
                teorical.addAll(getSlicesFromFactors(pizza, factors, level));
            }

            System.out.println("Posible Slices:");

            if ((pizza.getColumns() * pizza.getRows()) < 50) {
                Collections.sort(teorical, Collections.reverseOrder());
            } else {
                Collections.sort(teorical);
            }

            for (Slice s : teorical) {
                System.out.println(s);
            }
        }
        cuttedArea = (float) pizza.getCuttedCells() / ((pizza.getColumns() * pizza.getRows()));

        if (!reverse && (cuttedArea > 0.6f || (pizza.getColumns() * pizza.getRows()) < 50)) {
            Collections.sort(teorical, Collections.reverseOrder());
            reverse = true;
        }
        if (cuttedArea < 0.6f && reverse) {
            Collections.sort(teorical);
            reverse = false;
        }

        List<Slice> possible = new ArrayList<>();
        for (Slice s : teorical) {
            Slice sc = new Slice(s.getRows(), s.getColumns(), s.getR1(), s.getC1(), s.getR2(), s.getC2(), level);
            possible.addAll(findGaps(pizza, sc));
        }

        return possible;
    }

    private List<Slice> getSlicesFromFactors(Pizza pizza, List<Integer> factors, int level) {
        Map<String, Slice> slices = new HashMap<>();

        for (int i = 0; i < factors.size(); i++) {
            int r1 = productoAnteriores(i, factors);
            int c1 = productoSiguientes(i, factors);
            if (r1 <= pizza.getRows() && c1 <= pizza.getColumns()) {
                String key = r1 + "," + c1;
                if (!slices.containsKey(key)) {
                    slices.put(key, new Slice(r1, c1, level));
                }
            }

            int r2 = c1;
            int c2 = r1;
            if (r2 <= pizza.getRows() && c2 <= pizza.getColumns()) {
                String key = r2 + "," + c2;
                if (!slices.containsKey(key)) {
                    slices.put(key, new Slice(r2, c2, level));
                }
            }
        }
        return new ArrayList<Slice>(slices.values());
    }

    private int productoAnteriores(int p, List<Integer> lista) {
        int producto = 1;
        for (int i = p; i >= 0; i--) {
            producto *= lista.get(i);
        }
        return producto;
    }

    private int productoSiguientes(int p, List<Integer> lista) {
        int producto = 1;
        for (int i = p + 1; i < lista.size(); i++) {
            producto *= lista.get(i);
        }
        return producto;
    }
}
