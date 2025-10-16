package fmi.mjt.taskdistributor;

import java.util.Arrays;

import static java.lang.Math.abs;

public class TaskDistributor {

    public static int minDifference(int[] tasks) {

        if(tasks == null || tasks.length == 0) {
            return 0;
        }

        // the only task must be positive, otherwise it will be returned 0
        if(tasks.length == 1 && tasks[0] >= 0) {
            return tasks[0];
        }

        //upon finding a zero, the method will return 0
        for(int i = 0; i < tasks.length; i++) {
            if(tasks[i] < 0) {
                return 0;
            }
        }

        Arrays.sort(tasks);

        int teamOne = tasks[tasks.length - 1]; // the last task
        int teamTwo = tasks[tasks.length - 2]; // the before last task

        for(int i = tasks.length - 3; i >= 0; i--) {
            if(teamOne <= teamTwo) {
                teamOne += tasks[i];
            }
            else {
                teamTwo += tasks[i];
            }
        }

        return abs(teamOne - teamTwo);
    }

    public static void main(String[] args) {
        System.out.println(minDifference(new int[]{1,2,3,4,5})); // 1
        System.out.println(minDifference(new int[]{10,20,15,5})); // 0
        System.out.println(minDifference(new int[]{7,3,2,1,5,4})); // 0
        System.out.println(minDifference(new int[]{9,1,1,1})); // 6
        System.out.println(minDifference(new int[]{})); // 0
        System.out.println(minDifference(new int[]{120})); // 120
        System.out.println(minDifference(new int[]{30, 30})); // 0
        System.out.println(minDifference(new int[]{10,2,4,1,5,1,6,7,8,13,10,17,13})); // 1
        System.out.println(TaskDistributor.minDifference(new int[]{1,4,5,6,8}));// 0
        System.out.println(minDifference(new int[]{5,5,5,5})); // 0
        System.out.println(minDifference(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10})); // 1
        System.out.println(minDifference(new int[]{23, 1, 5, 18, 9, 3})); // 1
    }
}
