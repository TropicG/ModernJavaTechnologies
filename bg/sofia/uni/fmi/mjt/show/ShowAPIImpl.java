package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

import java.util.Arrays;

public class ShowAPIImpl implements  ShowAPI{

    private Ergenka[] ergenkas;
    private final EliminationRule[] eliminationRules;

    public ShowAPIImpl(Ergenka[] ergenkas, EliminationRule[] defaultEliminationRules) {

        this.ergenkas = ergenkas;

        if(defaultEliminationRules == null || defaultEliminationRules.length == 0) {
            this.eliminationRules = new EliminationRule[]{new LowestRatingEliminationRule()};
        }
        else {
            eliminationRules = defaultEliminationRules;
        }
    }


    @Override
    public Ergenka[] getErgenkas() {
        return ergenkas;
    }

    @Override
    public void playRound(DateEvent dateEvent) {

        if(dateEvent == null) {
            return;
        }

        if(ergenkas != null && ergenkas.length != 0) {

            // all the ergenkas are going to a date
            for(Ergenka ergenka : ergenkas) {
                organizeDate(ergenka, dateEvent);
            }

            // eliminating the ergenkas based on the elimination rules
            eliminateErgenkas(this.eliminationRules);
        }
    }

    @Override
    public void eliminateErgenkas(EliminationRule[] eliminationRules){

        // if there aren't any elimination rules, by default the lowest rated ergenkas should be removed
        if(eliminationRules == null || eliminationRules.length == 0) {
            ergenkas = this.eliminationRules[0].eliminateErgenkas(ergenkas);
            return;
        }

        if(ergenkas != null && ergenkas.length != 0) {
            for(EliminationRule eliminationRule : eliminationRules) {
                ergenkas = eliminationRule.eliminateErgenkas(ergenkas);

                // all the ergenkas were eliminated based on the rules
                if(ergenkas.length == 0) {
                    return;
                }
            }
        }
    }

    @Override
    public void organizeDate(Ergenka ergenka, DateEvent dateEvent) {

        // if the given ergenka is null the date will happen
        if(ergenka == null) {
            return;
        }

        // the ergenka goes on a date with the ergen
        ergenka.reactToDate(dateEvent);
    }
}
