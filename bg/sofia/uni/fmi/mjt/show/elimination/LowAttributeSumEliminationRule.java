package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowAttributeSumEliminationRule implements EliminationRule {

    private int threshold;

    public LowAttributeSumEliminationRule(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {

        int toRemoveCount = 0;
        for(Ergenka ergenka : ergenkas) {
            boolean conditionForElimination = (ergenka.getRomanceLevel() + ergenka.getHumorLevel()) == threshold;
            if(conditionForElimination) {
                toRemoveCount++;
            }
        }

        Ergenka[] ergenkasAfterElimination = new Ergenka[ergenkas.length - toRemoveCount];
        int afterElimationArrayIndex = 0;
        for(Ergenka ergenka : ergenkas) {
            boolean conditionForElimination = (ergenka.getRomanceLevel() + ergenka.getHumorLevel()) == threshold;
            if(conditionForElimination) {
                ergenkasAfterElimination[afterElimationArrayIndex++] = ergenka;
            }
        }

        return ergenkasAfterElimination;
    }
}
