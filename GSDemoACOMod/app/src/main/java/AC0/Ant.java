package AC0;

import com.dji.GSDemo.GoogleMap.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

    public class Ant implements Callable<Ant>{
        public static final double Q = 0.0005; //parametro que ajusta la pheromona depositada entre 0 y 1
        public static final double RHO = 0.2; //evaporacion de pheromona entre 0 y 1
        public static final double ALPHA = 0.01; //importancia de cola pheromona valor >=0
        public static final double BETA = 9.5; // controla la importancia distancia entre origen y destino
        private AntColonyOptimization aco;
        private int antNumb;
        private Route route = null;
        static int invalidCityIndex = -1;
        static int numbOfCities = MainActivity.initialRoute.size();
        public Route getRoute() { return route;}
        public Ant(AntColonyOptimization aco, int antNumb) {
            this.aco = aco;
            this.antNumb = antNumb;
        }

        public Ant call() throws Exception{
            if(numbOfCities!=MainActivity.initialRoute.size()){
                numbOfCities=MainActivity.initialRoute.size();
            }
            route = null;
            int originalCityIndex = ThreadLocalRandom.current().nextInt(numbOfCities);
            ArrayList<City> routeCities = new ArrayList<City>(numbOfCities);
            HashMap<String,Boolean> visitedCities = new HashMap<String,Boolean>(numbOfCities);
            IntStream.range(0, numbOfCities).forEach(x-> visitedCities.put(MainActivity.initialRoute.get(x).getName(), false));
            int numbOfVisitedCities = 0;
            visitedCities.put(MainActivity.initialRoute.get(originalCityIndex).getName(), true);
            double routeDistance = 0.0;
            int x = originalCityIndex;
            int y = invalidCityIndex;
            if(numbOfVisitedCities != numbOfCities) y = getY(x,visitedCities);
            while( y != invalidCityIndex) {
                routeCities.add(numbOfVisitedCities++,MainActivity.initialRoute.get(x));
                routeDistance +=aco.getDistancesMatrix()[x][y];
                adjustPhermoneLevel(x,y,routeDistance);
                visitedCities.put(MainActivity.initialRoute.get(y).getName(), true);
                x = y;
                if(numbOfVisitedCities != numbOfCities) y = getY(x,visitedCities);
                else y =invalidCityIndex;
            }
            //System.out.println("Ant.call() llamado");
            if(routeDistance<aco.getDistancesMatrix()[x][originalCityIndex]) {
                routeDistance += aco.getDistancesMatrix()[x][originalCityIndex];
            }
            routeCities.add(numbOfVisitedCities,MainActivity.initialRoute.get(x));
            route = new Route(routeCities,routeDistance);
            return this;
        }
        private void adjustPhermoneLevel(int x,int y, double distance) {
            boolean flag = false;
            while (!flag) {
                double currentPhermoneLevel = aco.getPhermoneLevelsMatrix()[x][y].doubleValue();
                double updatedPhermoneLevel = (1-RHO)*currentPhermoneLevel + Q/distance;
                if (updatedPhermoneLevel < 0.00) flag = aco.getPhermoneLevelsMatrix()[x][y].compareAndSet(0);
                else flag = aco.getPhermoneLevelsMatrix()[x][y].compareAndSet(updatedPhermoneLevel);
            }
        }
        private int getY(int x, HashMap	<String,Boolean> visitedCities) {
            int returnY = invalidCityIndex;
            double random = ThreadLocalRandom.current().nextDouble();
            ArrayList<Double> transitionProbabilities = getTransitionProbabilities(x,visitedCities);
            for (int y=0; y<numbOfCities;y++) {
                if(transitionProbabilities.get(y)>random) {
                    returnY = y;
                    break;
                }else random -= transitionProbabilities.get(y);
            }
            return returnY;
        }
        private ArrayList<Double> getTransitionProbabilities(int x,HashMap<String, Boolean> visitedCities){
            ArrayList<Double> transitionProbabilities = new ArrayList<Double>(numbOfCities);
            IntStream.range(0, numbOfCities).forEach(i-> transitionProbabilities.add(0.0));
            double denominator = getTPDenominator(transitionProbabilities,x,visitedCities);
            IntStream.range(0, numbOfCities).forEach(y-> transitionProbabilities.set(y,transitionProbabilities.get(y)/denominator));
            return transitionProbabilities;
        }
        private double getTPDenominator(ArrayList<Double> transitionProbabilities,int x, HashMap<String, Boolean> visitedCities) {
            double denominator = 0.0;
            for(int y=0;y<numbOfCities;y++) {
                if(!visitedCities.get(MainActivity.initialRoute.get(y).getName())) {
                    if(x == y ) transitionProbabilities.set(y, 0.0);
                    else transitionProbabilities.set(y,getTPNumerator(x,y));
                    denominator += transitionProbabilities.get(y);

                }
            }
            return denominator;
        }
        private double getTPNumerator(int x, int y) {
            double numerator = 0.0;
            double phermoneLevel = aco.getPhermoneLevelsMatrix()[x][y].doubleValue();
            if(phermoneLevel != 0.0) numerator = Math.pow(phermoneLevel, ALPHA) * Math.pow(1/aco.getDistancesMatrix()[x][y], BETA);
            return numerator;
        }
        public int getAntNumb() {	return antNumb;	}
        public void SetNullRoute(){
            this.route = null;
        }
    }


