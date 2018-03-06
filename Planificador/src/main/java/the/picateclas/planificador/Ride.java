package the.picateclas.planificador;

public class Ride implements Comparable {

    int rideId;

    int r1;

    int c1;

    int r2;

    int c2;

    int tini;

    int tfin;

    boolean completed = false;

    public Ride(int rideId, int r1, int c1, int r2, int c2, int tini, int tfin) {
        super();
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
        this.tini = tini;
        this.tfin = tfin;
        this.rideId = rideId;
        this.completed = false;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
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

    public int getTini() {
        return tini;
    }

    public void setTini(int tini) {
        this.tini = tini;
    }

    public int getTfin() {
        return tfin;
    }

    public void setTfin(int tfin) {
        this.tfin = tfin;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int compareTo(Object o) {
        Ride s = (Ride) o;
        // Bigger to Slower
        // return (s.getRows() * s.getColumns()) - (this.getRows() * this.getColumns());

        // Slower to Bigger
        return (tini - s.getTini());
    }

    @Override
    public String toString() {
        return "Ride [rideId=" + rideId + ", r1=" + r1 + ", c1=" + c1 + ", r2=" + r2 + ", c2=" + c2 + ", tini=" + tini + ", tfin=" + tfin
                + ", completed=" + completed + "]";
    }

}
