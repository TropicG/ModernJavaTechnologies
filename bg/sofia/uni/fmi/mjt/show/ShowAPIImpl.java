package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.HumorousErgenka;

import java.util.Arrays;

public class ShowAPIImpl implements  ShowAPI{

    private Ergenka[] ergenkas;
    private final EliminationRule[] eliminationRules;

    public ShowAPIImpl(Ergenka[] ergenkas, EliminationRule[] defaultEliminationRules) {
        ergenkas = Arrays.copyOf(ergenkas, ergenkas.length);
        eliminationRules = Arrays.copyOf(defaultEliminationRules, defaultEliminationRules.length);
    }

    @Override
    public Ergenka[] getErgenkas() {
        return ergenkas;
    }

    @Override
    public void playRound(DateEvent dateEvent) {
        for(Ergenka ergenka : ergenkas) {
            organizeDate(ergenka, dateEvent);
        }
        eliminateErgenkas(this.eliminationRules);
    }

    @Override
    public void eliminateErgenkas(EliminationRule[] eliminationRules){

        if(eliminationRules == null) {
            LowestRatingEliminationRule lowestRatingEliminationRule = new LowestRatingEliminationRule();
            ergenkas = lowestRatingEliminationRule.eliminateErgenkas(ergenkas);
            return;
        }

        for(EliminationRule eliminationRule : eliminationRules) {
            ergenkas = eliminationRule.eliminateErgenkas(ergenkas);
        }
    }

    @Override
    public void organizeDate(Ergenka ergenka, DateEvent dateEvent) {
        ergenka.reactToDate(dateEvent);
    }

    public static void main(String[] args) {

        Ergenka moqtaDeni = new HumorousErgenka("deni", (short)22, 4, 8, 100);

        Ergenka shmatka1 = new HumorousErgenka("shmatka1", (short)23, 0, 2, 0);
        Ergenka shmatka2 = new HumorousErgenka("shmatka2", (short)24, 0, 1, 0);



    }
}
