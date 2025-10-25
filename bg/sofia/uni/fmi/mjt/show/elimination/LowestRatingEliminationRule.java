package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowestRatingEliminationRule implements EliminationRule {

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {

        Ergenka leastRating = ergenkas[0];
        int toRemoveCount = 0;

        //gets the least rated ergenka as well as how much ergenkas are going to be remove
        for (Ergenka ergenka : ergenkas) {
            if (leastRating.getRating() > ergenka.getRating()) {
                leastRating = ergenka;
                toRemoveCount = 0;
            }

            if (leastRating.getRating() == ergenka.getRating()) {
                toRemoveCount++;
            }
        }

        //the removeCount is kept to create a new array with accurate number of ergenkas after removal
        Ergenka[] ergenkasAfterElimination = new Ergenka[ergenkas.length - toRemoveCount];
        int afterElimationArrayIndex = 0;
        for (Ergenka ergenka : ergenkas) {
            if (ergenka.getRating() == leastRating.getRating()) {
                ergenkasAfterElimination[afterElimationArrayIndex++] = ergenka;
            }
        }

        return ergenkasAfterElimination;
    }

}
