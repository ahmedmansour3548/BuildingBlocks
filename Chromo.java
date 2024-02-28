/******************************************************************************
*  A Teaching GA                      Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Chromo
{
/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

    public String chromo;
    public double rawFitness;
    public double sclFitness;
    public double proFitness;

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

    private static double randnum;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

    public Chromo(){

        //  Set gene values to a randum sequence of 1's and 0's
        char geneBit;
        chromo = "";
        for (int i=0; i<Parameters.numGenes; i++){
            for (int j=0; j<Parameters.geneSize; j++){
                randnum = Search.r.nextDouble();
                if (randnum > 0.5) geneBit = '0';
                else geneBit = '1';
                this.chromo = chromo + geneBit;
            }
        }

        this.rawFitness = -1;   //  Fitness not yet evaluated
        this.sclFitness = -1;   //  Fitness not yet scaled
        this.proFitness = -1;   //  Fitness not yet proportionalized
    }


/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

    //  Get Alpha Represenation of a Gene **************************************

    public String getGeneAlpha(int geneID){
        int start = geneID * Parameters.geneSize;
        int end = (geneID+1) * Parameters.geneSize;
        String geneAlpha = this.chromo.substring(start, end);
        return (geneAlpha);
    }

    //  Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****

    public int getIntGeneValue(int geneID){
        String geneAlpha = "";
        int geneValue;
        char geneSign;
        char geneBit;
        geneValue = 0;
        geneAlpha = getGeneAlpha(geneID);
        for (int i=Parameters.geneSize-1; i>=1; i--){
            geneBit = geneAlpha.charAt(i);
            if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
        }
        geneSign = geneAlpha.charAt(0);
        if (geneSign == '1') geneValue = geneValue - (int)Math.pow(2.0, Parameters.geneSize-1);
        return (geneValue);
    }

    //  Get Integer Value of a Gene (Positive only) ****************************

    public int getPosIntGeneValue(int geneID){
        String geneAlpha = "";
        int geneValue;
        char geneBit;
        geneValue = 0;
        geneAlpha = getGeneAlpha(geneID);
        for (int i=Parameters.geneSize-1; i>=0; i--){
            geneBit = geneAlpha.charAt(i);
            if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
        }
        return (geneValue);
    }

    //  Mutate a Chromosome Based on Mutation Type *****************************

    public void doMutation(){

        String mutChromo = "";
        char x;

        switch (Parameters.mutationType){

        case 1:     //  Replace with new random number

            for (int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
                x = this.chromo.charAt(j);
                randnum = Search.r.nextDouble();
                if (randnum < Parameters.mutationRate){
                    if (x == '1') x = '0';
                    else x = '1';
                }
                mutChromo = mutChromo + x;
            }
            this.chromo = mutChromo;
            break;

        default:
            System.out.println("ERROR - No mutation method selected");
        }
    }

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

    //  Select a parent for crossover ******************************************

    public static int selectParent(){

        double rWheel = 0;
        int j = 0;
        int k = 0;

        switch (Parameters.selectType){

        case 1:     // Proportional Selection
            randnum = Search.r.nextDouble();
            for (j=0; j<Parameters.popSize; j++){
                rWheel = rWheel + Search.member[j].proFitness;
                if (randnum < rWheel) return(j);
            }
            break;

        case 3:     // Random Selection
            randnum = Search.r.nextDouble();
            j = (int) (randnum * Parameters.popSize);
            return(j);

        case 2:     //  Tournament Selection

        default:
            System.out.println("ERROR - No selection method selected");
        }
    return(-1);
    }

    //  Produce a new child from two parents  **********************************

    public static void mateParents(int pnum1, int pnum2, Chromo parent1, Chromo parent2, Chromo child1, Chromo child2){

        int xoverPoint1;
        int xoverPoint2;
        int temp;
        String tempChild1;
        String tempChild2;
        switch (Parameters.xoverType){
    
        case 1:     //  Single Point Crossover

            //  Select crossover point
            xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));

            //  Create child chromosome from parental material
            child1.chromo = parent1.chromo.substring(0,xoverPoint1) + parent2.chromo.substring(xoverPoint1);
            child2.chromo = parent2.chromo.substring(0,xoverPoint1) + parent1.chromo.substring(xoverPoint1);
            break;
    
        case 2:     //  Two Point Crossover
        
            // Select two crossover points
            xoverPoint1 = (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize));
            xoverPoint2 = (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize));
            
            // check if first crossover point is below second, if not swap them
            if (xoverPoint1 > xoverPoint2){
                temp = xoverPoint1;
                xoverPoint1 = xoverPoint2;
                xoverPoint2 = temp;
            }
    
            // Create child using two crossover points
            child1.chromo = parent1.chromo.substring(0, xoverPoint1) +
                            parent2.chromo.substring(xoverPoint1, xoverPoint2) +
                            parent1.chromo.substring(xoverPoint2);
            child2.chromo = parent2.chromo.substring(0, xoverPoint1) +
                            parent1.chromo.substring(xoverPoint1, xoverPoint2) +
                            parent2.chromo.substring(xoverPoint2);
            break;
        case 3:     //  Uniform Crossover
            StringBuilder child1Chromo = new StringBuilder(parent1.chromo.length());
            StringBuilder child2Chromo = new StringBuilder(parent2.chromo.length());
            // Move along each bit in the gene and randomly choose one from each parent
            for (int i = 0; i < parent1.chromo.length(); i++) {
                if (Search.r.nextDouble() < 0.5) {
                    child1Chromo.append(parent1.chromo.charAt(i));
                    child2Chromo.append(parent2.chromo.charAt(i));
                } else {
                    child1Chromo.append(parent2.chromo.charAt(i));
                    child2Chromo.append(parent1.chromo.charAt(i));
                }
            }
            
            child1.chromo = child1Chromo.toString();
            child2.chromo = child2Chromo.toString();
            break;
        case 4:     // N-Point Crossover
            int[] points = new int[Parameters.xoverPoints];
            for (int i = 0; i < points.length; i++) {
                points[i] = (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize));
            }
            Arrays.sort(points); // Sort the crossover point to ensure no double-backing
            
            // Use Stringbuilders to manipulate Strings easily
            StringBuilder child1Builder = new StringBuilder(parent1.chromo);
            StringBuilder child2Builder = new StringBuilder(parent2.chromo);
            
            for (int i = 0; i < points.length; i++) {
                if (i % 2 == 0) { // For even indices...
                    if (i < points.length - 1) {
                        // ... swap segments using temp Strings
                        tempChild1 = child1Builder.substring(points[i], points[i+1]);
                        tempChild2 = child2Builder.substring(points[i], points[i+1]);
                        child1Builder.replace(points[i], points[i+1], tempChild2);
                        child2Builder.replace(points[i], points[i+1], tempChild1);
                    } else {
                        // Swap the end points normally
                        tempChild1 = child1Builder.substring(points[i]);
                        tempChild2 = child2Builder.substring(points[i]);
                        child1Builder.replace(points[i], child1Builder.length(), tempChild2);
                        child2Builder.replace(points[i], child2Builder.length(), tempChild1);
                    }
                }
            }
            
            // Set the chromosomes
            child1.chromo = child1Builder.toString();
            child2.chromo = child2Builder.toString();
            break;
        default:
            System.out.println("ERROR - Bad crossover method selected");
        }
    
        // Reset fitness values
        child1.rawFitness = -1;   // Fitness not yet evaluated
        child1.sclFitness = -1;   // Fitness not yet scaled
        child1.proFitness = -1;   // Fitness not yet proportionalized
        child2.rawFitness = -1;   // Fitness not yet evaluated
        child2.sclFitness = -1;   // Fitness not yet scaled
        child2.proFitness = -1;   // Fitness not yet proportionalized
    }


    //  Produce a new child from a single parent  ******************************

    public static void mateParents(int pnum, Chromo parent, Chromo child){

        //  Create child chromosome from parental material
        child.chromo = parent.chromo;

        //  Set fitness values back to zero
        child.rawFitness = -1;   //  Fitness not yet evaluated
        child.sclFitness = -1;   //  Fitness not yet scaled
        child.proFitness = -1;   //  Fitness not yet proportionalized
    }

    //  Copy one chromosome to another  ***************************************

    public static void copyB2A (Chromo targetA, Chromo sourceB){

        targetA.chromo = sourceB.chromo;

        targetA.rawFitness = sourceB.rawFitness;
        targetA.sclFitness = sourceB.sclFitness;
        targetA.proFitness = sourceB.proFitness;
        return;
    }

}   // End of Chromo.java ******************************************************
