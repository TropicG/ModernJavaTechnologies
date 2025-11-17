package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowAttributeSumEliminationRule implements EliminationRule {

    private final int threshold;

    public LowAttributeSumEliminationRule(int threshold) {

        // threshold is left at 0 intentional, there could be not interesting ergenkas with humour level 0 and romance level 0
        this.threshold = Math.max(threshold,0) ;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {

        // if for example we are given nothing we return empty array
        if(ergenkas == null || ergenkas.length == 0) {
            return new Ergenka[0];
        }

        // first we check all the ergenkas to see how many of them are going to be eliminated
        int toRemoveCount = 0;
        for(Ergenka ergenka : ergenkas) {

            // ergenkas which are null in the array passed to the function are going to be marked for removal
            if(ergenka == null) {
                continue;
            }

            boolean conditionForElimination = (ergenka.getRomanceLevel() + ergenka.getHumorLevel()) < threshold;
            if(conditionForElimination) {
                toRemoveCount++;
            }
        }

        // all of the ergenkas must be eliminated, return empty array
        if(toRemoveCount == ergenkas.length) {
            return new Ergenka[0];
        }

        // this array will hold all the ergenkas that are passing from the elimination
        Ergenka[] ergenkasAfterElimination = new Ergenka[ergenkas.length - toRemoveCount];
        int afterElimationArrayIndex = 0;
        for(Ergenka ergenka : ergenkas) {

            // those ergenkas that are null are going to be skipped and put at the very end of the new array
            if(ergenka == null) {
                continue;
            }

            boolean conditionForElimination = (ergenka.getRomanceLevel() + ergenka.getHumorLevel()) < threshold;
            if(!conditionForElimination) {
                ergenkasAfterElimination[afterElimationArrayIndex++] = ergenka;
            }
        }

        // all pre-existing null values are going to be put at the very end
        for(Ergenka ergenka : ergenkas) {
            if(ergenka == null) {
                ergenkasAfterElimination[afterElimationArrayIndex++] = null;
            }
        }

        return ergenkasAfterElimination;
    }
}
