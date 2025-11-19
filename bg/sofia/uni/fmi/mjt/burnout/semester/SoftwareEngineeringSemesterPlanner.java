package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

import java.util.Arrays;

public final class SoftwareEngineeringSemesterPlanner extends AbstractSemesterPlanner {

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) {

        UniversitySubject[] subjectsForSemester = semesterPlan.subjects();
        SubjectRequirement[] subjectRequirements = semesterPlan.subjectRequirements();

        //keeping track how many subjects for each category we have to enroll
        int numMathSubjects = 0;
        int numProgrammingSubjects = 0;
        int numTheorySubjects = 0;
        int numPracticalSubjects = 0;
        for(int i = 0; i < subjectRequirements.length; i++) {
            switch(subjectRequirements[i].category()) {
                case MATH -> numMathSubjects = subjectRequirements[i].minAmountEnrolled();
                case PROGRAMMING -> numProgrammingSubjects = subjectRequirements[i].minAmountEnrolled();
                case THEORY -> numTheorySubjects = subjectRequirements[i].minAmountEnrolled();
                case PRACTICAL -> numPracticalSubjects = subjectRequirements[i].minAmountEnrolled();
            }
        }

        //sorting by credits, since we want to minimise the subjects we would like to attend
        UniversitySubject temp = null;
        for(int i = 0; i < subjectsForSemester.length; i++){
            for(int j = i; j < subjectsForSemester.length; j++) {
                if(subjectsForSemester[i].credits() < subjectsForSemester[j].credits()) {
                    temp = subjectsForSemester[i];
                    subjectsForSemester[i] = subjectsForSemester[j];
                    subjectsForSemester[j] = temp;
                }
            }
        }

        //marking which subjects were enrolled to cover quota for categories
        boolean[] isSubjectTaken = new boolean[subjectsForSemester.length];

        //subjects which we are going to enroll
        UniversitySubject[] attendingSubjects = new UniversitySubject[subjectsForSemester.length];
        int attendingIndex = 0;
        int currentCredits = 0;

        for(int i = 0 ; i < subjectsForSemester.length; i++){
            switch(subjectsForSemester[i].category()) {
                case Category.MATH ->  {
                    if(numMathSubjects > 0) {
                        attendingSubjects[attendingIndex++] = subjectsForSemester[i];
                        currentCredits += subjectsForSemester[i].credits();
                        numMathSubjects--;
                        isSubjectTaken[i] = true;
                    }
                    break;
                }
                case Category.PROGRAMMING -> {
                    if(numProgrammingSubjects > 0) {
                        attendingSubjects[attendingIndex++] = subjectsForSemester[i];
                        currentCredits += subjectsForSemester[i].credits();
                        numProgrammingSubjects--;
                        isSubjectTaken[i] = true;
                    }
                    break;
                }
                case Category.THEORY -> {
                    if(numTheorySubjects > 0) {
                        attendingSubjects[attendingIndex++] = subjectsForSemester[i];
                        currentCredits += subjectsForSemester[i].credits();
                        numTheorySubjects--;
                        isSubjectTaken[i] = true;
                    }
                    break;
                }
                case Category.PRACTICAL -> {
                    if(numPracticalSubjects > 0) {
                        attendingSubjects[attendingIndex++] = subjectsForSemester[i];
                        currentCredits += subjectsForSemester[i].credits();
                        numPracticalSubjects--;
                        isSubjectTaken[i] = true;
                    }
                }
            }

            boolean areAllCategoriesCovered =
                    (numMathSubjects == 0) && (numProgrammingSubjects == 0) && (numPracticalSubjects == 0) && (numTheorySubjects == 0);

            if(areAllCategoriesCovered) {
                break;
            }
        }

        // if we have covered all the categories, but we still need to add subjects to cover the quota
        if(currentCredits < semesterPlan.minimalAmountOfCredits()) {
            for(int i = 0; i < subjectsForSemester.length; i ++) {

                //adding those subjects who were skipped earlier due to already fulfilled category
                if(!isSubjectTaken[i]){
                    attendingSubjects[attendingIndex++] = subjectsForSemester[i];
                    currentCredits += subjectsForSemester[i].credits();
                }

                if(currentCredits >= semesterPlan.minimalAmountOfCredits()) {
                    break;
                }
            }
        }

        UniversitySubject[] actualAttendingSubjects = new UniversitySubject[attendingIndex];
        for(int i = 0; i < attendingIndex; i++){
            actualAttendingSubjects[i] =  attendingSubjects[i];
        }
        return actualAttendingSubjects;
    }

    @Override
    public int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {
        int jarCount = 0;

        int totalStudyDays = 0;
        int totalSlackDays = 0;

        for(UniversitySubject subject : subjects) {

            double neededTimeForRest = 0.0;
            switch(subject.category()){
                case Category.MATH -> neededTimeForRest = 0.2;
                case Category.PROGRAMMING -> neededTimeForRest = 0.1;
                case Category.THEORY ->  neededTimeForRest = 0.15;
                case Category.PRACTICAL -> neededTimeForRest = 0.05;
            }

            totalStudyDays += subject.neededStudyTime();
            totalSlackDays += (int) Math.ceil((double)subject.neededStudyTime() * neededTimeForRest);

            // in case you are slacking too much
            if(totalSlackDays > maximumSlackTime) {
                throw new DisappointmentException("You dissapointed baba");
            }
        }

        jarCount += (int) totalStudyDays / 5;
        if(totalStudyDays + totalSlackDays > semesterDuration) {
            jarCount *= 2;
        }

        return jarCount;
    }
}
