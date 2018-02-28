package the.picateclas.pizzaslices;

public class Slice implements Comparable {

    int rows;

    int columns;

    int r1;

    int c1;

    int r2;

    int c2;

    int level;

    public Slice() {
    }

    public Slice(int rows, int columns, int level) {
        this.rows = rows;
        this.columns = columns;
        this.level = level;
    }

    public Slice(int r1, int c1, int r2, int c2) {
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
    }

    public Slice(int rows, int columns, int r1, int c1, int r2, int c2, int level) {
        this.rows = rows;
        this.columns = columns;
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
        this.level = level;
    }

    public int getR1() {
        return r1;
    }

    public void setR1(int r1) {
        this.r1 = r1;
    }

    public int getC1() {
        return c1;
    }

    public void setC1(int c1) {
        this.c1 = c1;
    }

    public int getR2() {
        return r2;
    }

    public void setR2(int r2) {
        this.r2 = r2;
    }

    public int getC2() {
        return c2;
    }

    public void setC2(int c2) {
        this.c2 = c2;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String toLine() {
        StringBuilder sb = new StringBuilder();
        return sb.append(r1).append(c1).append(r2).append(c2).toString();
    }

    @Override
    public int compareTo(Object o) {
        Slice s = (Slice) o;
        // Bigger to Slower
        return (s.getRows() * s.getColumns()) - (this.getRows() * this.getColumns());

        // Slower to Bigger
        // return (s.getRows()*s.getColumns())-(this.getRows()*this.getColumns());
    }

    @Override
    public String toString() {
        return "Slice [rows=" + rows + ", columns=" + columns + ", r1=" + r1 + ", c1=" + c1 + ", r2=" + r2 + ", c2=" + c2 + ", level="
                + level + "]";
    }

}
