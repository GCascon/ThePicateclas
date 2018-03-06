package the.picateclas.planificador;

public class Car {

    private int id;

    private int rc;

    private int cc;

    private int timeToBeFree;

    public Car(int id, int rc, int cc, int timeToBeFree) {
        super();
        this.id = id;
        this.rc = rc;
        this.cc = cc;
        this.timeToBeFree = timeToBeFree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public int getCc() {
		return cc;
	}

	public void setCc(int cc) {
		this.cc = cc;
	}

	public int getTimeToBeFree() {
        return timeToBeFree;
    }

    public void setTimeToBeFree(int timeToBeFree) {
        this.timeToBeFree = timeToBeFree;
    }

	@Override
	public String toString() {
		return "Car [id=" + id + ", rc=" + rc + ", cc=" + cc + ", timeToBeFree=" + timeToBeFree + "]";
	}

    

}
