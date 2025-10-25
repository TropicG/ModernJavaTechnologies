package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

import java.util.Arrays;

public class ShowAPIImpl implements  ShowAPI{

    private Ergenka[] ergenkas;
    private EliminationRule[] eliminationRules;

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
    }

    @Override
    public void eliminateErgenkas(EliminationRule[] eliminationRules){
        for(EliminationRule eliminationRule : eliminationRules) {
            ergenkas = eliminationRule.eliminateErgenkas(ergenkas);
        }
    }

    @Override
    public void organizeDate(Ergenka ergenka, DateEvent dateEvent) {
        ergenka.reactToDate(dateEvent);
    }
}
