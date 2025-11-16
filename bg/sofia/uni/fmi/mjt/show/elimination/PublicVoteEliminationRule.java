package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

import java.util.Arrays;

public class PublicVoteEliminationRule implements EliminationRule {

    private String[] votes;

    public PublicVoteEliminationRule(String[] votes) {
        votes = Arrays.copyOf(votes, votes.length);
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {

        int totalVotes = votes.length;
        int eliminationThreshold = (int)(votes.length / 2) + 1;

        // all the vote counts for the ergenka
        // each index of numberOfCounts represents the index of the ergenka in ergenkas
        int[] numberOfCounts = new int[ergenkas.length];
        for(int i = 0; i < votes.length; i++){
            for(int j = 0; j < ergenkas.length; j++){
                if(votes[i].equals(ergenkas[j].getName())){
                    numberOfCounts[j]++;
                }
            }
        }

        // how many ergenkas are going to be eliminated
        int eliminatedCount = 0;
        for(int i = 0; i < numberOfCounts.length; i++){
            if(numberOfCounts[i] >= eliminationThreshold) {
                eliminatedCount++;
            }
        }

        // ergenkasAfterElim will hold all the ergenkas with votes below the threshold
        Ergenka[] ergenkasAfterElim = new Ergenka[ergenkas.length - eliminatedCount];
        int afterElimIndex = 0;
        for(int i = 0; i < ergenkas.length; i++){
            if(numberOfCounts[i] < eliminationThreshold ){
                ergenkasAfterElim[afterElimIndex++] = ergenkas[i];
            }
        }

        return ergenkasAfterElim;
    }

}
