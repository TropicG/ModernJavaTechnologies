package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.workout.CardioWorkout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class Main {

    public static void main(String[] args) {

        try {
            Workout cardioWorkout = new CardioWorkout(null, 1, 1, 1);
        }
        catch (Exception e) {
            System.out.println(e.getCause());
        }

    }
}
