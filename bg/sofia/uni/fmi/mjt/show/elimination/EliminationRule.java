package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

/**
 * Defines a rule used to eliminate ergenkas from the show.
 */
public interface EliminationRule {

    /**
     * Applies the elimination rule to the provided ergenkas.
     *
     * @param ergenkas the current ergenkas, never {@code null}
     * @return an array with ergenkas that remain after elimination, never {@code null}
     */
    Ergenka[] eliminateErgenkas(Ergenka[] ergenkas);

}
