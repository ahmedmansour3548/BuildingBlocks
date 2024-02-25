import java.io.*;
import java.util.*;
import java.text.*;

public class RoyalRoadR1 extends FitnessFunction{



    public RoyalRoadR1(){
        name = "Royal Road R1 Problem";
    }



//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

    public void doRawFitness(Chromo X){
    X.rawFitness = 0;
    int completeSegments = 0;
    for (int i = 0; i < Parameters.numGenes; i++){
        boolean segmentComplete = true;
        for (int j = 0; j < Parameters.geneSize; j++){
            int index = i * Parameters.geneSize + j;
            if (X.chromo.charAt(index) != '1'){
                segmentComplete = false;
                break;
            }
        }
        if (segmentComplete){
            completeSegments++;
        }
    }
    X.rawFitness = 100 * completeSegments;
}


//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

    public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{

        for (int i=0; i<Parameters.numGenes; i++){
            Hwrite.right(X.getGeneAlpha(i),11,output);
        }
        output.write("   RawFitness");
        output.write("\n        ");
        for (int i=0; i<Parameters.numGenes; i++){
            Hwrite.right(X.getPosIntGeneValue(i),11,output);
        }
        Hwrite.right((int) X.rawFitness,13,output);
        output.write("\n\n");
        return;
    }
} 
