package bg.sofia.uni.fmi.mjt.fittrack.workout;

public sealed interface Workout permits CardioWorkout, StrengthWorkout, YogaSession {

    /**
     * Returns the name of the workout.
     *
     * @return the workout name.
     */
    String getName();

    /**
     * Returns the duration of the workout in minutes.
     *
     * @return the duration in minutes.
     */
    int getDuration();

    /**
     * Returns the number of calories burned by performing the workout.
     *
     * @return the calories burned.
     */
    int getCaloriesBurned();

    /**
     * Returns the difficulty of the workout (1 - easy, 5 - very hard).
     *
     * @return the difficulty.
     */
    int getDifficulty();

    /**
     * Returns the type of the workout.
     *
     * @return the workout type.
     */
    WorkoutType getType();

}