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

        System.out.println("Score Maximo Teorico=" + scoreMaximo + " Rutas=" + numRides + " Coches=" + numCars);
        /*
         * for (Ride r : rides) { System.out.println("Ruta:" + r); }
         */

    }

    long tiempoInicial = System.currentTimeMillis();

    long ultimoTiempo = System.currentTimeMillis();// Tiempo para impresiones cada 10 sec.

    void imprimeMaximo(int level, int maxScore, Plan pActual) {
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoTiempo > 10000) {
            ultimoTiempo = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("Time: ").append((tiempoActual - tiempoInicial) / 1000).append(" s, MaxScoreFound: ").append(maxScore)
                    .append(", Actual Level: ").append(level).append(", Plan actual: ").append(pActual);
            System.out.println(sb.toString());
        }
    }

    public List<Plan> calculatePlanList() {
        int timeout = 60000;

        List<Plan> possibilities = calculatePossibilities(null);
        planStack.addAll(possibilities);
        int scoreConseguido = 0;
        int level = 0;

        while (!planStack.isEmpty() && scoreConseguido < scoreMaximo && !(System.currentTimeMillis() - tiempoInicial > timeout)) {
            Plan pActual = planStack.pop();

            if (pActual.level < level) {
                Plan pEliminado;
                do {
                    pEliminado = resultStack.pop();
                } while (pEliminado.level != pActual.level && !resultStack.isEmpty());

                level = pActual.level;
            }
            // System.out.println("Level: " + level + " #### Plan: " + pActual);
            // System.out.println("resultStack: " + resultStack);
            imprimeMaximo(level, scoreConseguido, pActual);

            if (isAllowed(pActual)) {
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
                int scoreActual = 0;// Score de la pila de solucion actual
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

    private boolean isAllowed(Plan p) {
        for (Ride r : p.getRides()) {
            if (completedRides.containsKey(r.getRideId())) {
                return false;
            }
        }
        return true;
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
                /*
                 * if (permutada.size() > 3 * n) { return permutada; }
                 */
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
                    /*
                     * if (permutada.size() > 3 * n) { return permutada; }
                     */
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

        // XXXXXXXXXXXXXXXXXXXXXXXXX LEVEL 0 XXXXXXXXXXXXXXXXXXXXXXXXX
        if (previousPlan == null) {

            List<Ride> alcanzables = new ArrayList<>();
            for (Ride r : rides) {
                int timeToGetRoute = r.getR1() + r.getC1();
                int timeToDoRoute = Math.abs(r.getR2() - r.getR1()) + Math.abs(r.getC2() - r.getC1());

                if (timeToGetRoute + timeToDoRoute <= r.getTfin()) {
                    alcanzables.add(r);
                }
            }
            System.out.println("Rutas al alcance: " + alcanzables.size());

            List<List<Ride>> permRides = permutaciones(numCars, alcanzables);
            for (List<Ride> ridesPlan : permRides) {
                List<Car> carsPlan = new ArrayList<>();
                List<Integer> ridesScores = new ArrayList<>();
                int planScore = 0;
                int desplazamientos = 0;

                for (int c = 0; c < numCars; c++) {
                    int timeToGetRoute = ridesPlan.get(c).getR1() + ridesPlan.get(c).getC1();

                    int time = timeToGetRoute + Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                            + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                    int score = Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                            + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                    if (ridesPlan.get(c).getTini() >= timeToGetRoute) {
                        score += bonus;
                        if (ridesPlan.get(c).getTini() > timeToGetRoute) {
                            time += (ridesPlan.get(c).getTini() - timeToGetRoute);// Penalizacion por empezar antes
                        }
                    }

                    // Si NO VOY A LLEGAR a tiempo no hago la ruta
                    if (time > ridesPlan.get(c).getTfin() || time > steps) {
                        score = 0;
                        timeToGetRoute = 0;
                        ridesScores.add(score);// Este coche no me da puntos...
                        ridesPlan.set(c, new Ride(-1, 0, 0, 0, 0, 0, 0));// Ruta vacia. No hace ruta
                        carsPlan.add(new Car(c, 0, 0, 0));// Valor Simbolico
                    } else {
                        // Si LLEGO A TIEMPO tomo la ruta
                        ridesScores.add(score);
                        carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), time));
                    }
                    planScore += score;
                    desplazamientos += timeToGetRoute;

                }
                if (planScore > 0) {
                    planList.add(new Plan(carsPlan, ridesPlan, ridesScores, 0, planScore, desplazamientos));
                }

            }
        } else {// XXXXXXXXXXXXXXXXXXXXXXXXX LEVEL MAYOR QUE 0 XXXXXXXXXXXXXXXXXXXXXXXXX
            List<Ride> pendientes = new ArrayList<>();
            for (Ride r : rides) {
                if (!completedRides.containsKey(r.getRideId())) {
                    pendientes.add(r);
                }
            }

            HashMap<Integer, Ride> mapAlcanzables = new HashMap<>();
            for (int c = 0; c < numCars; c++) {
                for (Ride r : pendientes) {
                    int timeToGetRoute = Math.abs(previousPlan.getCars().get(c).getRc() - r.getR1())
                            + Math.abs(previousPlan.getCars().get(c).getCc() - r.getC1());

                    int timeToDoRoute = previousPlan.getCars().get(c).getTimeToBeFree() + timeToGetRoute + Math.abs(r.getR2() - r.getR1())
                            + Math.abs(r.getC2() - r.getC1());

                    if (timeToDoRoute <= r.getTfin()) {
                        if (!mapAlcanzables.containsKey(r.getRideId())) {
                            mapAlcanzables.put(r.getRideId(), r);
                        }
                    }
                }
            }

            List<Ride> alcanzables = new ArrayList(mapAlcanzables.values());
            // System.out.println("Rutas al alcance: " + alcanzables.size());

            if (alcanzables.size() < numCars) {
                // Rellenar con -1
                int vacios = numCars - alcanzables.size();
                for (int i = 0; i < vacios; i++) {
                    alcanzables.add(new Ride(-1, 0, 0, 0, 0, 0, 0));// Ride Ficticio
                }
            }

            List<List<Ride>> permRides = permutacionesRep(numCars, alcanzables);
            for (List<Ride> ridesPlan : permRides) {
                List<Car> carsPlan = new ArrayList<>();
                List<Integer> ridesScores = new ArrayList<>();
                int planScore = 0;
                int desplazamientos = 0;

                for (int c = 0; c < numCars; c++) {
                    if (ridesPlan.get(c).getRideId() != -1) {// Si el coche tiene ruta asignada...
                        int timeToGetRoute = Math.abs(previousPlan.getCars().get(c).getRc() - ridesPlan.get(c).getR1())
                                + Math.abs(previousPlan.getCars().get(c).getCc() - ridesPlan.get(c).getC1());

                        int time = previousPlan.getCars().get(c).getTimeToBeFree() + timeToGetRoute
                                + Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                                + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                        int score = Math.abs(ridesPlan.get(c).getR2() - ridesPlan.get(c).getR1())
                                + Math.abs(ridesPlan.get(c).getC2() - ridesPlan.get(c).getC1());

                        if (previousPlan.getCars().get(c).getTimeToBeFree() + timeToGetRoute <= ridesPlan.get(c).getTini()) {
                            // Bonus
                            score += bonus;
                            if (previousPlan.getCars().get(c).getTimeToBeFree() + timeToGetRoute < ridesPlan.get(c).getTini()) {
                                // Penalizacion por empezar antes de tiempo
                                time += ridesPlan.get(c).getTini() - (previousPlan.getCars().get(c).getTimeToBeFree() + timeToGetRoute);
                            }
                        }

                        // Si NO VOY A LLEGAR a tiempo no hago la ruta
                        if (time >= ridesPlan.get(c).getTfin() || time >= steps) {
                            score = 0;
                            ridesScores.add(score);// Este coche no me da puntos...
                            timeToGetRoute = 0;// Si no la voy a hacer tampoco penaliza desplazamiento
                            ridesPlan.set(c, new Ride(-1, 0, 0, 0, 0, 0, 0));// Ruta vacia. No hace ruta
                            // Me quedo donde estaba
                            carsPlan.add(new Car(c, previousPlan.getCars().get(c).getRc(), previousPlan.getCars().get(c).getCc(), 0));
                        } else {
                            // Si LLEGO A TIEMPO tomo la ruta
                            ridesScores.add(score);
                            carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), time));
                        }
                        planScore += score;
                        desplazamientos += timeToGetRoute;

                    } else {
                        carsPlan.add(new Car(c, ridesPlan.get(c).getR2(), ridesPlan.get(c).getC2(), 0));// Valor Simbolico
                        ridesScores.add(0);// Valor Simbolico
                    }
                }
                if (planScore > 0) {
                    planList.add(new Plan(carsPlan, ridesPlan, ridesScores, previousPlan.getLevel() + 1, planScore, desplazamientos));
                }

            }

        }

        Collections.sort(planList);
        /*
         * for (Plan p : planList) { System.out.println(p); }
         */

        /*
         * int limite = planList.size(); if (limite > 200) { System.out.println("Planes posibles=" + limite); limite = 200; }
         */

        // Me quedo con los 200 mejores planes (Para ahorrar memoria)
        // return planList.subList(0, limite);

        return planList;
    }

    public List<String> getLineas(List<Plan> planList) {
        ArrayList<String> lineas = new ArrayList<>();
        int totalScore = 0;
        HashMap<Integer, List<Ride>> mapCarRides = new HashMap<>();
        HashMap<Integer, Integer> mapGlobalRides = new HashMap<>();
        for (Plan p : planList) {
            for (int i = 0; i < p.getCars().size(); i++) {
                if (p.getRides().get(i).getRideId() != -1) {
                    if (!mapGlobalRides.containsKey(p.getRides().get(i).getRideId())) {
                        List<Ride> carRides = null;
                        if (!mapCarRides.containsKey(p.getCars().get(i).getId())) {
                            carRides = new ArrayList<>();
                        } else {
                            carRides = mapCarRides.get(p.getCars().get(i).getId());
                        }
                        carRides.add(p.getRides().get(i));
                        mapCarRides.put(p.getCars().get(i).getId(), carRides);
                        mapGlobalRides.put(p.getRides().get(i).getRideId(), p.getCars().get(i).getId());
                    } else {
                        System.out.println("Conflicto en la ruta:" + p.getRides().get(i).getRideId() + " con los coches:"
                                + p.getCars().get(i).getId() + " y " + mapGlobalRides.get(p.getRides().get(i).getRideId()));
                    }
                }
            }
            totalScore += p.getPlanScore();
        }
        System.out.println("Rutas completadas: " + mapGlobalRides.size());
        System.out.println("Final Score=" + totalScore);

        for (List<Ride> ridesToPrint : mapCarRides.values()) {
            StringBuilder sb = new StringBuilder();
            int tam = ridesToPrint.size();
            sb.append(tam);
            for (int j = tam - 1; j >= 0; j--) {
                sb.append(" ");
                sb.append(ridesToPrint.get(j).getRideId());
            }
            lineas.add(sb.toString());
        }
        return lineas;
    }

}
