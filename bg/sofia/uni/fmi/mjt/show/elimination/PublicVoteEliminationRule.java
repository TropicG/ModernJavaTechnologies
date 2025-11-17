package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

import java.util.Arrays;

public class PublicVoteEliminationRule implements EliminationRule {

    private final String[] votes;

    public PublicVoteEliminationRule(String[] votes) {
        this.votes = votes;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {

        // no votes means that no ergenka will be eliminated
        if(votes == null || votes.length == 0) {

            // in case ergenkas are null we return emptry ergenka list
            return ergenkas == null ? new Ergenka[0] : ergenkas;
        }

        // no ergenkas means no elimination means nobody goes out
        if(ergenkas == null || ergenkas.length == 0) {
            return new Ergenka[0];
        }

        // removing all the votes that are null since they are not going to be taken into considuration
        int totalVotes = votes.length;
        for(int i = 0 ; i < votes.length; i++){
            if(votes[i] == null) {
                totalVotes--;
            }
        }
        int eliminationThreshold = (totalVotes / 2) + 1;

        // all the vote counts for the ergenka
        // each index of numberOfCounts represents the index of the ergenka in ergenkas
        int[] numberOfCounts = new int[ergenkas.length];
        for(int i = 0; i < votes.length; i++){
            for(int j = 0; j < ergenkas.length; j++){

                if(votes[i] == null) {
                    continue;
                }

                // if an ergenka is null we dont keep count on her and continue with the rest
                if(ergenkas[j] == null) {
                    numberOfCounts[j] = 0;
                    continue;
                }

                if(votes[i].equals(ergenkas[j].getName())){
                    numberOfCounts[j]++;
                }
            }
        }

        // how many ergenkas are going to be eliminated
        int eliminatedCount = 0;
        for(int i = 0; i < numberOfCounts.length; i++){

            // if an ergenka is null it means automatic elimination
            // remember that the i ergenka has number of votes at i index in numberOfCounts, this is how we guarantee no out of bounds
            if(ergenkas[i] == null) {
                eliminatedCount++;
                continue;
            }

            if(numberOfCounts[i] >= eliminationThreshold) {
                eliminatedCount++;
            }
        }

        // all of the ergenkas didnt qualify and are going to be eliminated, return emptry ergenka list
        if(eliminatedCount == ergenkas.length) {
            return new Ergenka[0];
        }

        // ergenkasAfterElim will hold all the ergenkas with votes below the threshold
        Ergenka[] ergenkasAfterElim = new Ergenka[ergenkas.length - eliminatedCount];

        // in case two have the same num of votes, the first one met in the array is going to be removed
        boolean firstMetEliminatedErgenka = false;
        int afterElimIndex = 0;
        for(int i = 0; i < ergenkas.length; i++){

            // the ergenkas that are null are not going to be inserted
            if(ergenkas[i] == null) {
                continue;
            }

            // the first ergenka met who has reached the majority of votes will be removed
            // the second one wont be removed and will be inserted into the array
            if(numberOfCounts[i] >= eliminationThreshold && !firstMetEliminatedErgenka) {
                firstMetEliminatedErgenka = true;
                continue;
            }

            if(numberOfCounts[i] < eliminationThreshold){
                ergenkasAfterElim[afterElimIndex++] = ergenkas[i];
            }
            // if this ergenka has also majority vote, she will advance because the one with majority vote was already kicked out
            else if(firstMetEliminatedErgenka) {
                ergenkasAfterElim[afterElimIndex++] = ergenkas[i];
            }
        }

        return ergenkasAfterElim;
    }

}
