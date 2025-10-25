package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;
import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;

/**
 * Public API for controlling the show.
 */
public interface ShowAPI {

    /**
     * Returns the current ergenkas participating in the show.
     *
     * @return an array of ergenkas, never {@code null}; may be empty
     */
    Ergenka[] getErgenkas();

    /**
     * Plays a full round using the provided {@link DateEvent}.
     *
     * @param dateEvent the event to play during the round, never {@code null}
     */
    void playRound(DateEvent dateEvent);

    /**
     * Applies a sequence of elimination rules to the current ergenkas.
     *
     * @param eliminationRules the rules to apply
     */
    void eliminateErgenkas(EliminationRule[] eliminationRules);

    /**
     * Performs a single date with the ergenkas {@link DateEvent}.
     *
     * @param ergenka the ergenka participating in the date, never {@code null}
     * @param dateEvent the date event to organize, never {@code null}
     */
    void organizeDate(Ergenka ergenka, DateEvent dateEvent);

}