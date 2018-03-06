package the.picateclas.planificador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Planificador {

	int rows;

	int cols;

	int numCars;

	int numRides;

	int bonus;

	int steps;

	List<Plan> solution = null;

	ArrayDeque<Plan> resultStack = new ArrayDeque<>();

	ArrayDeque<Plan> planStack = new ArrayDeque<>();

	int scoreMaximo = 0;

	List<Ride> rides;

	HashMap<Integer, Ride> completedRides = new HashMap<>();

	public Planificador(List<String> lines) {
		int count = 0;

		for (String line : lines) {
			StringTokenizer st = new StringTokenizer(line);
			if (count == 0) {
				rows = Integer.parseInt(st.nextToken());
				cols = Integer.parseInt(st.nextToken());
				numCars = Integer.parseInt(st.nextToken());
				numRides = Integer.parseInt(st.nextToken());
				bonus = Integer.parseInt(st.nextToken());
				steps = Integer.parseInt(st.nextToken());

				rides = new ArrayList<>();
			} else {
				int r1 = Integer.parseInt(st.nextToken());
				int c1 = Integer.parseInt(st.nextToken());
				int r2 = Integer.parseInt(st.nextToken());
				int c2 = Integer.parseInt(st.nextToken());
				int tini = Integer.parseInt(st.nextToken());
				int tfin = Integer.parseInt(st.nextToken());

				Ride ride = new Ride(count - 1, r1, c1, r2, c2, tini, tfin);
				rides.add(ride);

				scoreMaximo += Math.abs(ride.getR1() - ride.getR2()) + Math.abs(ride.getC1() - ride.getC2()) + bonus;
			}
			count++;
		}

		Collections.sort(rides);

		System.out.println("Score Maximo=" + scoreMaximo);
		for (Ride r : rides) {
			System.out.println("Ruta:" + r);
		}

	}

	public List<Plan> calculatePlanList() {
		List<Plan> possibilities = calculatePossibilities(null);
		planStack.addAll(possibilities);
		int scoreConseguido = 0;
		int level = 0;

		while (!planStack.isEmpty() && scoreConseguido < scoreMaximo) {
			Plan pActual = planStack.pop();

			if (pActual.level < level) {
				checkRides(resultStack.pop(), false);
				level--;
			}

			// Es posible? Necesario?
			resultStack.push(pActual);
			planStack.addAll(calculatePossibilities(pActual));
			level++;
			checkRides(pActual, true);

			// Guardar Solucion
			if (completedRides.size() == numRides && scoreConseguido > scoreMaximo) {
				solution = new ArrayList<>(resultStack);
			}

		}

		return solution;
	}

	public void checkRides(Plan p, boolean addRide) {
		for (Ride r : p.getRides()) {
			if (addRide) {
				completedRides.put(r.getRideId(), r);
			} else {
				completedRides.remove(r.getRideId());
			}
		}
	}

	public List<List<Ride>> permutaciones(int n, List<Ride> lista) {
		List<List<Ride>> permutada = new ArrayList<>();
		for (int pivote = 0; pivote <= lista.size() - n; pivote++) {
			for (int j = pivote + 1; j <= lista.size() - (n - 1); j++) {
				List<Ride> pm = new ArrayList<>();
				pm.add(lista.get(pivote));
				for (int i = 0; i < (n - 1); i++) {
					pm.add(lista.get(j + i));
				}
				permutada.add(pm);
			}
		}
		return permutada;
	}

	// Max(timeToBeFree)<=steps
	// timeToBeFree<=tfinRuta
	// ruta.completed=false
	// minimizar desp
	// maximizar score
	// descartar no posibles tActual>tfinR-modR
	List<Plan> calculatePossibilities(Plan previousPlan) {
        List<Plan> planList = new ArrayList<>();
        if (previousPlan == null) {
        	List<List<Ride>> permRides=permutaciones(numCars, rides);
        	for(List<Ride> ridesPlan:permRides) {
        		List<Car> carsPlan = new ArrayList<>();
        		List<Integer> ridesScores = new ArrayList<>();
        		int planScore = 0;
        		for (int c = 0; c < numCars; c++) {
                    // Car(int id, int xc, int yc, int timeToBeFree)
                    int time = Math.abs(0 - ridesPlan.get(c).getR1()) + Math.abs(0 - ridesPlan.get(c).getC1())
                            + Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1()) + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                    int score = Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                            + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                    if (ridesPlan.get(c).getTini() > 0) {
                        time += ridesPlan.get(c).getTini();// Penalizacion
                    } else {
                        // Bonus
                        score += bonus;
                    }
                    ridesScores.add(score);
                    planScore += score;
                    carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), time));
                }
                planList.add(new Plan(carsPlan, ridesPlan, ridesScores, 0, planScore));
        	}
        }else {
        	List<Ride> pendientes=new ArrayList<>();
        	for(Ride r: rides) {
        		if(!completedRides.containsKey(r.getRideId())) {
        			pendientes.add(r);
        		}
        	}
        	List<List<Ride>> permRides=null;
        	if(numRides>numCars) {
        		permRides=permutaciones(numCars, pendientes);
        	}else {
        		permRides=new ArrayList<>();
        		for(int c=0;c<numCars;c++) {
        			permRides.add(e);
        		}	
        	}
        	
        	
	        	
	        	for(List<Ride> ridesPlan:permRides) {
	        		List<Car> carsPlan = new ArrayList<>();
	        		List<Integer> ridesScores = new ArrayList<>();
	        		int planScore = 0;
	        		for (int c = 0; c < numCars; c++) {
	                    // Car(int id, int xc, int yc, int timeToBeFree)
	                   
	        			int time = Math.abs(previousPlan.getCars().get(c).getRc() - ridesPlan.get(c).getR1()) 
	        					+ Math.abs(previousPlan.getCars().get(c).getCc() - ridesPlan.get(c).getC1())
	                            + Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1()) 
	                            + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());
	
	                    int score = Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
	                            + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());
	
	                    if (ridesPlan.get(c).getTini() > 0) {
	                        time += ridesPlan.get(c).getTini();// Penalizacion
	                    } else {
	                        // Bonus
	                        score += bonus;
	                    }
	                    ridesScores.add(score);
	                    planScore += score;
	                    carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), time));
	                }
	                planList.add(new Plan(carsPlan, ridesPlan, ridesScores, 0, planScore));        	
        }

        
        for(Plan p:planList) {
        	System.out.println(p);
        }

        return planList;
    }

	public List<String> getLineas(List<Plan> planList) {
		ArrayList<String> lineas = new ArrayList<>();
		int totalScore = 0;
		HashMap<Integer, List<Ride>> mapCarRides = new HashMap<>();
		for (Plan p : planList) {
			for (int i = 0; i < p.getCars().size(); i++) {
				if (!mapCarRides.containsKey(p.getCars().get(i).getId())) {
					List<Ride> carRides = new ArrayList<>();
					carRides.add(p.getRides().get(i));
					mapCarRides.put(p.getCars().get(i).getId(), carRides);
				} else {
					List<Ride> carRides = mapCarRides.get(p.getCars().get(i).getId());
					carRides.add(p.getRides().get(i));
					mapCarRides.put(p.getCars().get(i).getId(), carRides);
				}
			}

			totalScore += p.getPlanScore();
		}
		System.out.println("Score=" + totalScore);

		for (Integer k : mapCarRides.keySet()) {
			StringBuilder sb = new StringBuilder();
			List<Ride> ridesToPrint = mapCarRides.get(k);
			for (int j = 0; j < ridesToPrint.size(); j++) {
				sb.append(ridesToPrint.get(j));
				if (j < ridesToPrint.size() - 1) {
					sb.append(" ");
				}
			}
		}
		return lineas;
	}

}
