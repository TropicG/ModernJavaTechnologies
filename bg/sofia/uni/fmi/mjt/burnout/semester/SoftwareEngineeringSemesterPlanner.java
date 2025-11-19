package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

import java.util.Arrays;

public final class SoftwareEngineeringSemesterPlanner extends AbstractSemesterPlanner {

    private int[] calcReqSubjectsForCategories(SubjectRequirement[] subjectRequirements) {

        int[] requiredNumSubjForCategory = new int[4];
        for (SubjectRequirement subjectRequirement : subjectRequirements) {

            // we are skipping the null elements to avoid NullPointerException
            if(subjectRequirement == null) {
                continue;
            }

            switch (subjectRequirement.category()) {
                case MATH -> requiredNumSubjForCategory[MATH_INDEX] = subjectRequirement.minAmountEnrolled();
                case PROGRAMMING -> requiredNumSubjForCategory[PROGRAMMING_INDEX] = subjectRequirement.minAmountEnrolled();
                case THEORY -> requiredNumSubjForCategory[THEORY_INDEX] = subjectRequirement.minAmountEnrolled();
                case PRACTICAL -> requiredNumSubjForCategory[PRACTICAL_INDEX] = subjectRequirement.minAmountEnrolled();
            }
        }

        return requiredNumSubjForCategory;
    }

    private void sortingSubjectsByCredits(UniversitySubject[] subjectsForSemester) {
        //sorting by credits, since we want to minimise the subjects we would like to attend
        UniversitySubject temp;
        for(int i = 0; i < subjectsForSemester.length; i++){
            for(int j = i + 1; j < subjectsForSemester.length; j++) {

                // there is a possibility that the array can have somewhere a null element
                // NOTE: This swapping idea with null values were given to me by Gemini
                boolean shouldSwap = false;

                // if we find a null elements, it is treated as smaller always and pushed at the end of the array
                if(subjectsForSemester[i] == null && subjectsForSemester[j] != null) {
                    shouldSwap = true;
                }
                // proper swapping if both of the elements are not null
                else if(subjectsForSemester[i] != null && subjectsForSemester[j] != null
                        && (subjectsForSemester[i].credits() < subjectsForSemester[j].credits())) {
                    shouldSwap = true;
                }

                if(shouldSwap) {
                    temp = subjectsForSemester[i];
                    subjectsForSemester[i] = subjectsForSemester[j];
                    subjectsForSemester[j] = temp;
                }
            }
        }
    }

    private UniversitySubject[] getSubjectsWeArePlanningToAttend(UniversitySubject[] subjectsForSemester,
                                                                 int numMathSubjects, int numProgrammingSubjects,int numTheorySubjects, int numPracticalSubjects, int minimalAmountOfCredits) {
        //marking which subjects were enrolled to cover quota for categories
        boolean[] isSubjectTaken = new boolean[subjectsForSemester.length];

        //subjects which we are going to enroll
        UniversitySubject[] attendingSubjects = new UniversitySubject[subjectsForSemester.length];
        int attendingIndex = 0;
        int currentCredits = 0;

        // this flag will be true if all the categories were covered meaning that we will attend enough subjects to cover our quota
        boolean areAllCategoriesCovered = false;

        for(int i = 0 ; i < subjectsForSemester.length; i++){

            // we are skipping the null elements to avoid NullPointerException
            if(subjectsForSemester[i] == null) {
                continue;
            }

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

            areAllCategoriesCovered = (numMathSubjects == 0) && (numProgrammingSubjects == 0) && (numPracticalSubjects == 0) && (numTheorySubjects == 0);

            if(areAllCategoriesCovered) {
                break;
            }
        }

        // there is a possibility that we need to cover 3 Theory subjects, but they are not in the semester plan
        // true story
        if(!areAllCategoriesCovered) {
            throw new CryToStudentsDepartmentException("All the subject requirements were not met");
        }


        // if we have covered all the categories, but we still need to add subjects to cover the quota
        if(currentCredits < minimalAmountOfCredits) {
            for(int i = 0; i < subjectsForSemester.length; i ++) {

                if(subjectsForSemester[i] == null) {
                    continue;
                }

                //adding those subjects who were skipped earlier due to already fulfilled category
                if(!isSubjectTaken[i]){
                    attendingSubjects[attendingIndex++] = subjectsForSemester[i];
                    currentCredits += subjectsForSemester[i].credits();
                }

                if(currentCredits >= minimalAmountOfCredits) {
                    break;
                }
            }
        }

        if(currentCredits < minimalAmountOfCredits) {
            throw new CryToStudentsDepartmentException("Even if we have managed to cover our subject requirements," +
                    " we dont have much choice of subjects to cover the credit quota");
        }

        // actual subjects we WILL enroll to, this array has the size of all subjects we are going to attend to without null values
        // since actualAttendingSubjects could hold null values
        UniversitySubject[] actualAttendingSubjects = new UniversitySubject[attendingIndex];
        for(int i = 0; i < attendingIndex; i++){
            actualAttendingSubjects[i] =  attendingSubjects[i];
        }

        return actualAttendingSubjects;
    }

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) {

        validatePassesSemesterPlan(semesterPlan);

        UniversitySubject[] subjectsForSemester = semesterPlan.subjects();
        SubjectRequirement[] subjectRequirements = semesterPlan.subjectRequirements();

        if(checkDuplicateSubjectReq(subjectRequirements)) {
            throw new InvalidSubjectRequirementsException("For the Software Engineering student unporoper subject requirements were passed");
        }

        //keeping track how many subjects for each category we have to enroll
        int[] requiredNumSubjForCategory = calcReqSubjectsForCategories(subjectRequirements);
        int numMathSubjects = requiredNumSubjForCategory[MATH_INDEX];
        int numProgrammingSubjects = requiredNumSubjForCategory[PROGRAMMING_INDEX];
        int numTheorySubjects = requiredNumSubjForCategory[THEORY_INDEX];
        int numPracticalSubjects = requiredNumSubjForCategory[PRACTICAL_INDEX];

        //sorting by credits, since we want to minimise the subjects we would like to attend
        sortingSubjectsByCredits(subjectsForSemester);

        //subjects which we are going to enroll
        UniversitySubject[] subjectsPlannedToAttend = getSubjectsWeArePlanningToAttend(
                subjectsForSemester, numMathSubjects, numProgrammingSubjects, numTheorySubjects, numPracticalSubjects, semesterPlan.minimalAmountOfCredits()
        );

        return subjectsPlannedToAttend;
    }
}
