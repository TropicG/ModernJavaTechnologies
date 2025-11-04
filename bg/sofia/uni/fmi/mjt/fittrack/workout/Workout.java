package bg.sofia.uni.fmi.mjt.fittrack.workout;

public sealed interface Workout permits CardioWorkout, StrengthWorkout, YogaSession {
    String getName();
    WorkoutType getType();

    int getDuration();
    int getCaloriesBurned();
    int getDifficulty();
}