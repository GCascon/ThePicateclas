package the.picateclas.planificador;

import java.util.List;

public class Plan implements Comparable {

    List<Car> cars;

    List<Ride> rides;

    List<Integer> scores;

    int level;

    int planScore;

    int desplazamientos;

    public Plan(List<Car> cars, List<Ride> rides, List<Integer> scores, int level, int planScore, int desplazamientos) {
        super();
        this.cars = cars;
        this.rides = rides;
        this.scores = scores;
        this.level = level;
        this.planScore = planScore;
        this.desplazamientos = desplazamientos;
    }

    public int getDesplazamientos() {
        return desplazamientos;
    }

    public void setDesplazamientos(int desplazamientos) {
        this.desplazamientos = desplazamientos;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPlanScore() {
        return planScore;
    }

    public void setPlanScore(int planScore) {
        this.planScore = planScore;
    }

    @Override
    public int compareTo(Object o) {
        Plan p = (Plan) o;
        // Bigger to Slower
        return (p.getPlanScore() - p.getDesplazamientos()) - (this.planScore - this.getDesplazamientos());

        // Slower to Bigger
        // return (this.planScore / this.getDesplazamientos()) - (p.getPlanScore() / p.getDesplazamientos());
    }

    @Override
    public String toString() {
        return "Plan [cars=" + cars + ", rides=" + rides + ", scores=" + scores + ", level=" + level + ", planScore=" + planScore
                + ", desplazamientos=" + desplazamientos + "]";
    }

}
