package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

/**
 * Represents an ergenka participating in the show.
 *
 * Implementations must provide the ergenka's basic attributes (name, age)
 * and performance metrics (romance, humor, rating). The {@link #reactToDate(DateEvent)}
 * method allows an ergenka to update her internal state when participating in a {@link DateEvent}.
 */
public interface Ergenka {

    /**
     * Returns the ergenka's full name. The name of each ergenka is a unique identifier.
     *
     * @return the name of the ergenka, never {@code null}
     */
    String getName();

    /**
     * Returns the ergenka's age in years.
     *
     * @return the age as a short
     */
    short getAge();

    /**
     * Returns the ergenka's romance level.
     *
     * @return an integer representing the romance level
     */
    int getRomanceLevel();

    /**
     * Returns the ergenka's humor level.
     *
     * @return an integer representing the humor level
     */
    int getHumorLevel();

    /**
     * Returns the ergenka's current rating.
     *
     * @return the rating as an integer
     */
    int getRating();

    /**
     * Reacts to a {@link DateEvent} and updates internal state accordingly.
     *
     * @param dateEvent the date event the ergenka participates in, never {@code null}
     */
    void reactToDate(DateEvent dateEvent);
}
