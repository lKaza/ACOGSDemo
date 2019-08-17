package AC0;

import com.dji.GSDemo.GoogleMap.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.List;
import static com.dji.GSDemo.GoogleMap.MainActivity.initialRoute;

public class AntColonyOptimization {
    private AtomicDouble[][] phermoneLevelsMatrix = null;
    private double[][] distancesMatrix = null;
    private List<City> cities = MainActivity.initialRoute;
    private int citiesSize = MainActivity.initialRoute.size();
    public AntColonyOptimization() throws IOException {
        InitializeDistances();
        InitializePhermone();

    }

    public AtomicDouble[][] getPhermoneLevelsMatrix() { return phermoneLevelsMatrix;}
    public double[][] getDistancesMatrix(){ return distancesMatrix;}

    private void InitializeDistances() throws IOException{
        distancesMatrix = new double[citiesSize][citiesSize];
        IntStream.range(0,citiesSize).forEach(x->{
            City cityY = cities.get(x);
            IntStream.range(0,citiesSize).forEach(y-> distancesMatrix[x][y] = cityY.measureDistance(cities.get(y)));

        });

    }
    private void InitializePhermone(){
        phermoneLevelsMatrix = new AtomicDouble[citiesSize][citiesSize];
        Random random = new Random();
        IntStream.range(0, citiesSize).forEach(x-> {
            IntStream.range(0, citiesSize).forEach(y->phermoneLevelsMatrix[x][y] = new AtomicDouble(random.nextDouble()));
        });
    }
}