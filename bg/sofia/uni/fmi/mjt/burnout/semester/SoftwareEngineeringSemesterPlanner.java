package bg.sofia.uni.fmi.mjt.burnout.semester;

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

        //sorting
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

            if((currentCredits > semesterPlan.minimalAmountOfCredits()) || areAllCategoriesCovered) {
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


        return attendingSubjects;
    }
}
