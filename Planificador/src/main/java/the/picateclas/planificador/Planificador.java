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

        System.out.println("Score Maximo Teorico=" + scoreMaximo+" Rutas="+numRides+" Coches="+numCars);
        /*for (Ride r : rides) {
            System.out.println("Ruta:" + r);
        }*/

    }
    
    long tiempoInicial = System.currentTimeMillis();

    long ultimoTiempo = System.currentTimeMillis();

    void imprimeMaximo(int level, int maxScore) {
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoTiempo > 10000) {
            ultimoTiempo = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("Time: ").append((tiempoActual - tiempoInicial) / 1000)
            	.append(" s, MaxScoreFound: ").append(maxScore)
                .append(", Actual Level: ").append(level);
            System.out.println(sb.toString());
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
                int eliminados = level - pActual.level;
                for (int i = 0; i < eliminados; i++) {
                    checkRides(resultStack.pop(), false);
                }
                level = pActual.level;
            }
            //System.out.println("Level: " + level + " #### Plan: " + pActual);
            //System.out.println("resultStack: " + resultStack);
            imprimeMaximo(level,scoreConseguido);

            resultStack.push(pActual);
            level++;
            checkRides(pActual, true);
            List<Plan> nextLevelPlans = calculatePossibilities(pActual);
            if (nextLevelPlans != null) {
                for (Plan p : nextLevelPlans) {
                    planStack.push(p);
                }
            }

            // Guardar Solucion
            if (completedRides.size() == numRides) {
                int scoreActual = 0;
                for (Plan p : resultStack) {
                    scoreActual += p.getPlanScore();
                }
                if (scoreActual > scoreConseguido) {
                    scoreConseguido = scoreActual;
                    solution = new ArrayList<>(resultStack);
                }
            }
        }

        System.out.println("Solution found: " + solution);

        return solution;
    }

    private void checkRides(Plan p, boolean addRide) {
        for (Ride r : p.getRides()) {
            if (r.getRideId() != -1) {
                if (addRide) {
                    completedRides.put(r.getRideId(), r);
                } else {
                    completedRides.remove(r.getRideId());
                }
            }
        }
    }

    private List<List<Ride>> permutaciones(int n, List<Ride> lista) {
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

    private static <T> List<List<T>> permutacionesRep(int n, List<T> lista) {
        List<List<T>> permutada = new ArrayList<>();
        for (int pivote = 0; pivote < lista.size(); pivote++) {
            for (int j = 0; j <= lista.size() - (n - 1); j++) {
                if (j != pivote) {
                    List<T> pn = new ArrayList<>();
                    pn.add(lista.get(pivote));
                    for (int i = 0; i < (n - 1); i++) {
                        pn.add(lista.get(j + i));
                    }
                    permutada.add(pn);
                }
            }
        }
        return permutada;
    }

    // ruta.completed=false
    // minimizar desp
    // maximizar score
    // descartar no posibles tActual>tfinR-modR
    private List<Plan> calculatePossibilities(Plan previousPlan) {
        if (completedRides.size() == numRides) {
            return null;
        }

        List<Plan> planList = new ArrayList<>();
        if (previousPlan == null) {
            List<List<Ride>> permRides = permutaciones(numCars, rides);
            for (List<Ride> ridesPlan : permRides) {
                List<Car> carsPlan = new ArrayList<>();
                List<Integer> ridesScores = new ArrayList<>();
                int planScore = 0;
                int desplazamientos = 0;
                
                for (int c = 0; c < numCars; c++) {                    
                	int timeToGetRoute=Math.abs(0 - ridesPlan.get(c).getR1()) + Math.abs(0 - ridesPlan.get(c).getC1());
                    desplazamientos += timeToGetRoute;

                    int time = timeToGetRoute
                            + Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                            + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                    int score = Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                            + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                    if (ridesPlan.get(c).getTini() >= timeToGetRoute) {
                    	score += bonus;
                    	if (ridesPlan.get(c).getTini() > timeToGetRoute) {
                    		time+=(ridesPlan.get(c).getTini() - timeToGetRoute);//Penalizacion por empezar antes
                    	}
                    }
                    
                    ridesScores.add(score);
                    planScore += score;
                    carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), time));
                }
                planList.add(new Plan(carsPlan, ridesPlan, ridesScores, 0, planScore, desplazamientos));
                
            }
        } else {
            List<Ride> pendientes = new ArrayList<>();
            for (Ride r : rides) {
                if (!completedRides.containsKey(r.getRideId())) {
                    pendientes.add(r);
                }
            }
            if (pendientes.size() < numCars) {
                // Rellenar con -1
                int vacios = numCars - pendientes.size();
                for (int i = 0; i < vacios; i++) {
                    pendientes.add(new Ride(-1, 0, 0, 0, 0, 0, 0));// Ride Ficticio
                }
            }

            List<List<Ride>> permRides = permutacionesRep(numCars, pendientes);
            for (List<Ride> ridesPlan : permRides) {
                List<Car> carsPlan = new ArrayList<>();
                List<Integer> ridesScores = new ArrayList<>();
                int planScore = 0;
                int desplazamientos = 0;
                int maxTimeToBeFree = 0;
                boolean noPoints = false;
                for (int c = 0; c < numCars; c++) {
                    if (ridesPlan.get(c).getRideId() != -1) {// Si el coche tiene ruta asignada...
                    	int timeToGetRoute=Math.abs(previousPlan.getCars().get(c).getRc() - ridesPlan.get(c).getR1())
                                + Math.abs(previousPlan.getCars().get(c).getCc() - ridesPlan.get(c).getC1());
                        desplazamientos += timeToGetRoute;
                    	
                        int time = previousPlan.getCars().get(c).getTimeToBeFree()
                        		+timeToGetRoute
                                + Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                                + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                        int score = Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                                + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                        
                        if (previousPlan.getCars().get(c).getTimeToBeFree()+timeToGetRoute <= ridesPlan.get(c).getTini()) {
                            // Bonus
                            score += bonus;
                            if (previousPlan.getCars().get(c).getTimeToBeFree()+timeToGetRoute < ridesPlan.get(c).getTini()) {
                            	time += ridesPlan.get(c).getTini()-(previousPlan.getCars().get(c).getTimeToBeFree()+timeToGetRoute);//Penalizacion por empezar antes
                            }
                        }                        
                        ridesScores.add(score);
                        planScore += score;
                        carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), time));

                        if (previousPlan.getCars().get(c).getTimeToBeFree() < ridesPlan.get(c).getTini()
                                || time > ridesPlan.get(c).getTfin() || time > steps) {
                            noPoints = true;
                        }

                    } else {
                        carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), 0));// Valor Simbolico
                        ridesScores.add(0);// Valor Simbolico
                    }
                }
                if (!noPoints) {
                    planList.add(new Plan(carsPlan, ridesPlan, ridesScores, previousPlan.getLevel() + 1, planScore, desplazamientos));
                }
            }

        }

        Collections.sort(planList);
        /*for (Plan p : planList) {
            System.out.println(p);
        }*/

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
                    if (p.getRides().get(i).getRideId() != -1) {
                        carRides.add(p.getRides().get(i));
                    }
                    mapCarRides.put(p.getCars().get(i).getId(), carRides);
                } else {
                    List<Ride> carRides = mapCarRides.get(p.getCars().get(i).getId());
                    if (p.getRides().get(i).getRideId() != -1) {
                        carRides.add(p.getRides().get(i));
                    }
                    mapCarRides.put(p.getCars().get(i).getId(), carRides);
                }
            }

            totalScore += p.getPlanScore();
        }
        System.out.println("Final Score=" + totalScore);

        for (List<Ride> ridesToPrint : mapCarRides.values()) {
            StringBuilder sb = new StringBuilder();
            int tam = ridesToPrint.size();
            sb.append(tam);
            for (int j = tam-1; j >=0; j--) {
                sb.append(" ");
                sb.append(ridesToPrint.get(j).getRideId());
            }
            lineas.add(sb.toString());
        }
        return lineas;
    }

}
