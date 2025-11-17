package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowestRatingEliminationRule implements EliminationRule {

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {

        // if for example we are given nothing we return emptry array
        if(ergenkas == null || ergenkas.length == 0) {
            return new Ergenka[0];
        }

        Ergenka leastRating = ergenkas[0];
        int toRemoveCount = 0; // keeps track on the ergankas to be deleted in order to have accurate array
        int nullErgenkas = 0; // keeps track on how many ergenkas are null inside the array

        // gets the first non-null ergenka from the array
        boolean areAllErgenkasNull = true;
        for(Ergenka ergenka : ergenkas) {
            if(ergenka != null) {
                leastRating = ergenka;
                areAllErgenkasNull = false;
            }
        }

        // we have to remove all ergenkas if they all have null values
        if(areAllErgenkasNull) {
            return new Ergenka[0];
        }

        //gets the least rated ergenka as well as how much ergenkas are going to be removed
        for (Ergenka ergenka : ergenkas) {

            // it is possible that we can forget to delete a null ergenka since toRemoveCount could be reset at any time
            if(ergenka == null) {
                nullErgenkas++;
                continue;
            }

            // every time we meet a new lowest rated ergenka we reset the counter keeping track how many ergenkas are going to be removed
            if (leastRating.getRating() > ergenka.getRating()) {
                leastRating = ergenka;
                toRemoveCount = 0;
            }

            // if they are multiple ergenkas that have the same lowest rating we increment the counter
            if (leastRating.getRating() == ergenka.getRating()) {
                toRemoveCount++;
            }
        }

        //all of the ergenkas have the lowest counter meaning all of them must be deleted, returning empty array
        if(toRemoveCount == ergenkas.length) {
            return new Ergenka[0];
        }

        // saving all the ergenkas who have passed
        Ergenka[] ergenkasAfterElimination = new Ergenka[ergenkas.length - (toRemoveCount + nullErgenkas)];
        int afterElimationArrayIndex = 0;
        for (Ergenka ergenka : ergenkas) {

            // skipping the null ergenka since we dont need it
            if(ergenka == null) {
                continue;
            }

            if (ergenka.getRating() != leastRating.getRating()) {
                ergenkasAfterElimination[afterElimationArrayIndex++] = ergenka;
            }
        }

        return ergenkasAfterElimination;
    }
}
