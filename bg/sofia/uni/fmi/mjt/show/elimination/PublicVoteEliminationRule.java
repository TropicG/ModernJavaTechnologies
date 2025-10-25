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
        int countsToBeEliminated = (int)(totalVotes / 2) + 1;

        //each element is equal to the number of votes for this ergenka
        int[] ergenkaVoteCounts = new int[ergenkas.length];


        return null;
    }

}
