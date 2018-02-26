package the.picateclas.pizzaslices;

public class Cell {
    private String ingredient;
    private boolean reserved=false;

    public Cell(String ingredient) {
        this.ingredient = ingredient;
    }
    
    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return "Cell{" + "ingredient=" + ingredient + ", reserved=" + reserved + '}';
    }
    
    
    
}
