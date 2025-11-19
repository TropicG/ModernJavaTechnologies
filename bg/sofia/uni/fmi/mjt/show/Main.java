package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowAttributeSumEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.PublicVoteEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.HumorousErgenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.RomanticErgenka;

public class Main {

    public static void main(String[] args) {

        Ergenka deni = new HumorousErgenka("deni", (short)22,4,3,25);
        Ergenka shmatka1 = new RomanticErgenka("", (short)-2,-1,-1,20,null);


        //DateEvent dateEvent = new DateEvent("beach", 5, 15);
        ShowAPI show = new ShowAPIImpl(new Ergenka[]{deni,shmatka1, null}, new EliminationRule[]{new LowAttributeSumEliminationRule(3)});

        //show.organizeDate(deni, dateEvent);

        EliminationRule lowAttributeSumEliminationRule = new LowAttributeSumEliminationRule(3);
        EliminationRule lowestRatingEliminationRule = new LowestRatingEliminationRule();
        EliminationRule publicVoteEliminationRule = new PublicVoteEliminationRule(new String[]{"", "", ""});

        Ergenka[] ergenkasAfterElim = publicVoteEliminationRule.eliminateErgenkas(new Ergenka[]{deni, shmatka1});
        for (Ergenka ergenka : ergenkasAfterElim) {
            System.out.println("Name of the ergenka *" +ergenka.getName() + "*");
        }



    }
}
