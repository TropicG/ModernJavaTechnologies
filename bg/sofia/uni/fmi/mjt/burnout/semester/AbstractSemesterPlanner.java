package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public sealed abstract class AbstractSemesterPlanner implements SemesterPlannerAPI permits ComputerScienceSemesterPlanner, SoftwareEngineeringSemesterPlanner{
    public static final int ONE_JAR_FOR_STUDY_DATES = 5;
    public static final int GRANDPA_URGENT_SUPPLY = 2;

    public static final double MATH_COEFFICIENT = 0.2;
    public static final double PROGRAMMING_COEFFICIENT = 0.1;
    public static final double THEORY_COEFFICIENT = 0.15;
    public static final double PRACTICAL_COEFFICIENT = 0.05;

    protected final int MATH_INDEX = 0;
    protected final int PROGRAMMING_INDEX = 1;
    protected final int THEORY_INDEX = 2;
    protected final int PRACTICAL_INDEX = 3;

    protected boolean checkDuplicateSubjectReq(SubjectRequirement[] subjectRequirements) {

        // every time we meet a subject we set the flag true, if already the flag was true we will throw exception
        boolean[] isSubjectRequirementMet = new boolean[]{false, false, false, false};

        // checking all the requirements to see for a duplicates and setting the flags
        for(SubjectRequirement subjectRequirement : subjectRequirements) {

            // we are skipping the null elements to avoid NullPointerException
            if(subjectRequirement == null) {
                continue;
            }

            switch(subjectRequirement.category()) {

                case Category.MATH -> {
                    if(!isSubjectRequirementMet[MATH_INDEX]) {
                        isSubjectRequirementMet[MATH_INDEX] = true;
                    }
                    else {
                        return true;
                    }
                }
                case Category.PROGRAMMING -> {
                    if(!isSubjectRequirementMet[PROGRAMMING_INDEX]) {
                        isSubjectRequirementMet[PROGRAMMING_INDEX] = true;
                    }
                    else {
                        return true;
                    }
                }
                case Category.THEORY -> {
                    if(!isSubjectRequirementMet[THEORY_INDEX]) {
                        isSubjectRequirementMet[THEORY_INDEX] = true;
                    }
                    else {
                        return true;
                    }
                }
                case Category.PRACTICAL -> {
                    if(!isSubjectRequirementMet[PRACTICAL_INDEX]) {
                        isSubjectRequirementMet[PRACTICAL_INDEX] = true;
                    }
                    else {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected static void validatePassesSemesterPlan(SemesterPlan semesterPlan) {
        if(semesterPlan == null) {
            throw new IllegalArgumentException();
        }
    }

    protected static void validatePassedArgsJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {
        if(subjects == null || subjects.length == 0){
            throw new IllegalArgumentException("Passing zero subjects or null to Jar count is not allowed");
        }

        if(maximumSlackTime <= 0) {
            throw new IllegalArgumentException("Passing negative or zero maximum slacking time is not allowed");
        }

        if(semesterDuration <= 0 ) {
            throw new IllegalArgumentException("Passing negative or zero semester duration time is not allowed");
        }
    }

    @Override
    public int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {
        validatePassedArgsJarCount(subjects, maximumSlackTime, semesterDuration);

        int jarCount = 0;

        int totalStudyDays = 0;
        int totalSlackDays = 0;

        // for each subject, the needed time for rest is calculated based on the subject coeficient
        for(UniversitySubject subject : subjects) {

            // we are skipping the null elements to avoid NullPointerException
            if(subject == null) {
                continue;
            }

            double neededTimeForRest = 0.0;
            switch(subject.category()){
                case Category.MATH -> neededTimeForRest = MATH_COEFFICIENT;
                case Category.PROGRAMMING -> neededTimeForRest = PROGRAMMING_COEFFICIENT;
                case Category.THEORY ->  neededTimeForRest = THEORY_COEFFICIENT;
                case Category.PRACTICAL -> neededTimeForRest = PRACTICAL_COEFFICIENT;
            }

            totalStudyDays += subject.neededStudyTime();

            // remember this calc should be rounded up
            totalSlackDays += (int) Math.ceil((double)subject.neededStudyTime() * neededTimeForRest);

            // in case you are slacking too much
            if(totalSlackDays > maximumSlackTime) {
                throw new DisappointmentException("You dissapointed baba");
            }
        }

        // for each 5 days our grandma will send us one jar
        jarCount += (int) totalStudyDays / ONE_JAR_FOR_STUDY_DATES;

        // in case of emergency, grandma will send us doubled the jars
        if(totalStudyDays + totalSlackDays > semesterDuration) {
            jarCount *= GRANDPA_URGENT_SUPPLY;
        }

        return jarCount;
    }
}
