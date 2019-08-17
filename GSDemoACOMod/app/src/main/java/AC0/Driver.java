package AC0;

import android.content.Context;
import android.widget.Toast;

import com.dji.GSDemo.GoogleMap.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.dji.GSDemo.GoogleMap.MainActivity.FinalRoute;
import static com.dji.GSDemo.GoogleMap.MainActivity.initialRoute;


public class Driver {

    static final int NUMBER_OF_ANTS = 1000;
    static final double PROCESSING_CYCLE_PROBABLITY = 0.5;
    static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static ExecutorCompletionService<Ant> executorCompletionService =
            new ExecutorCompletionService<Ant>(executorService);
    private Route shortestRoute = null;
    private static String RutaMasCorta;
    private static double DistanciaMasCorta;
    private int activeAnts = 0;



    public static void Driver() throws IOException {

        System.out.println("> "+NUMBER_OF_ANTS+" Hormigas Artificiales");
        Driver driver = new Driver();
        driver.printHeading();
        AntColonyOptimization aco = new AntColonyOptimization();
        //System.out.println("> Driver main inicio loop de hormigas");
        IntStream.range(1,NUMBER_OF_ANTS).forEach(x->{
            /*System.out.println("\n Driver executorCompletionService.submit nueva hormiga");*/
            executorCompletionService.submit(new Ant(aco,x));
            driver.activeAnts++;
            if (Math.random()>PROCESSING_CYCLE_PROBABLITY) driver.processAnts();
        });
        //System.out.println("\n Driver main fin loop de hormigas");
        driver.processAnts();
        driver.SetArrayRuta(Arrays.toString(driver.shortestRoute.getCities().toArray()));
        FinalRoute = driver.shortestRoute.getCities();
        driver.SetDistanciaMasCorta(driver.shortestRoute.getDistance());
        System.out.println("\n Ruta optima : "+Arrays.toString(driver.shortestRoute.getCities().toArray()));
        System.out.println("Distancia : " + driver.shortestRoute.getDistance());


    }
    private void processAnts() {
        while(activeAnts > 0) {
            //System.out.println("Driver main take hormiga");
            try {
                Ant ant = executorCompletionService.take().get();
                Route currentRoute = ant.getRoute();
                if(shortestRoute == null || currentRoute.getDistance() < shortestRoute.getDistance()) {
                    shortestRoute = currentRoute;
                    StringBuffer distance = new StringBuffer("      "+String.format("%.2f",currentRoute.getDistance()));
                    IntStream.range(0, 21-distance.length()).forEach(k-> distance.append(" "));
                    System.out.println(Arrays.toString(shortestRoute.getCities().toArray())+ " |" + distance + "| " + ant.getAntNumb());
                }
            }
            catch (Exception e) { e.printStackTrace();}
            activeAnts--;
        }

    }
    public static void showToast(Context mContext, String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void printHeading() {
        String headingColumn1 = "Ruta";
        String remainingHeadingColumns = "Distancia (kilometros) ! ant #";
        int cityNamesLength = 0;
        //for (int x = 0; x < initialRoute.size();x++) cityNamesLength += initialRoute.get(x).getName().length();
        int arrayLength = cityNamesLength + initialRoute.size()*2;
        int partialLength = (arrayLength - headingColumn1.length())/2;
        for (int x = 0; x < partialLength;x++)System.out.println(" ");
        System.out.println(headingColumn1);
        for (int x = 0; x < partialLength;x++)System.out.println(" ");
        if ((arrayLength % 2) == 0 )System.out.println(" ");
        System.out.println(" ! "+ remainingHeadingColumns);
        cityNamesLength += remainingHeadingColumns.length() + 3;
        for (int x = 0;x < cityNamesLength+initialRoute.size()*2;x++)System.out.println("-");
        System.out.println("");

    }
    private void  SetArrayRuta(String Ruta){
        RutaMasCorta = Ruta;
    }
    private void SetDistanciaMasCorta(double Ruta){  DistanciaMasCorta = Ruta; }
    public String GetArrayRuta (){
        return RutaMasCorta;
    }
    public double GetDistanciaMasCorta(){
        return DistanciaMasCorta;
    }
    public void clearShortestRoute(){
        this.shortestRoute = null;
        this.RutaMasCorta = null;
        this.activeAnts=0;
    }

}
