package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MissionTest {
    private static final double EXPECTED_VALUE_FOR_MISSION_8 = 50.0d;

    @Test
    void testMissionOfValidArgumentsCreatesValidMission() {
        // Note: this test focuses on the reading from a string information for data for a Mission object
        String line =
                "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                        "\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusActive,\"50.0 \",Success";
        Mission tempMission = Mission.of(line);

        // It compares all the data members are they properly parsed
        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("SpaceX", tempMission.company(), "The company of the mission shall be SpaceX");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // compares the dates
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // compares the detail for the rockets
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // compares rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_ACTIVE, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Active");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.SUCCESS, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Success");
    }

    @Test
    void testMissionOfMissingCompanyCreatesValidMissionWithEmptyCompany() {
        // This test focuses on a creating a Mission who has an empty field for company
        String line = "8,,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                "\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusActive,\"50.0 \",Success";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");

        // if a company is missing it should put empty string as a value for a data member
        assertEquals("", tempMission.company(), "The company of the mission shall be empty");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // comparing dates
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // comparing rocket details
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // comparing rocket status, mission status, mission cost
        assertEquals(RocketStatus.STATUS_ACTIVE, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Active");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.SUCCESS, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Success");
    }

    @Test
    void testMissionOfMissingCompanyLocCreatesValidMissionWithEmptyCompanyLoc() {
        // This test focuses on a creating a Mission who has an empty field for company and location
        String line = "8,,,\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusActive,\"50.0 \",Success";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");

        // if both the company and the location are empty return empty strings are created as data members
        assertEquals("", tempMission.company(), "The company of the mission shall be empty");
        assertEquals("", tempMission.location(),
                "The loc of the mission shall be empty");

        // compares the date
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // compares the rocket details
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // compares rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_ACTIVE, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Active");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.SUCCESS, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Success");
    }

    @Test
    void testMissionOfMissingCostMissionStatusArgumentsCreatesValidMissionWithZeroCostPreLaunchFailure() {
        // The test focuses on creating a mission with empty cost and empty mission status from a string
        String line = "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                "\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusActive,,";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("SpaceX", tempMission.company(), "The company of the mission shall be SpaceX");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // comparing the dates
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // comparing the rocket detаils
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // when the cost is mission it is assigned as Optional.empty() and mission status becomes pre-launch failure
        assertEquals(RocketStatus.STATUS_ACTIVE, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Active");
        assertEquals(0.0d, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 0");
        assertEquals(MissionStatus.PRELAUNCH_FAILURE, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.PRELAUNCH_FAILURE");
    }

    @Test
    void testMissionOfMissingDateArgumentsCreatesValidMissionWithCurrentDate() {
        // This test focuses on creating a rocket with missing date, then it is created with today's date
        String line = "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                ",Falcon 9 Block 5 | ANASIS-II,StatusActive,\"50.0 \",Success";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("SpaceX", tempMission.company(), "The company of the mission shall be SpaceX");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // when the date is missing, it is assigned today's date
        assertEquals(LocalDate.now(), tempMission.date(), "The date shall be current one");

        // comparing the rocket details
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // comparing the rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_ACTIVE, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Active");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.SUCCESS, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Success");
    }

    @Test
    void testMissionOfValidArgumentsWithRetiredRocketStatusCreatesValidMission() {
        String line = "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                "\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusRetired,\"50.0 \",Success";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("SpaceX", tempMission.company(), "The company of the mission shall be SpaceX");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // comparing the dates
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // comparing the rocket details
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // comparing the rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_RETIRED, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Retired");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.SUCCESS, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Success");
    }

    @Test
    void testMissionOfValidArgumentsWithFailureMissionStatusCreatesValidMission() {
        String line = "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                "\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusRetired,\"50.0 \",Failure";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("SpaceX", tempMission.company(), "The company of the mission shall be SpaceX");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // comparing the dates
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // comparing the rocket details
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // comparing the rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_RETIRED, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Retired");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.FAILURE, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Failure");
    }

    @Test
    void testMissionOfValidArgumentsWithPartialFailureMissionStatusCreatesValidMission() {
        // This test focuses on creating a Mission with mission status of MissionStatus.PARTIAL_FAILURE
        String line = "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                "\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusRetired,\"50.0 \",Partial Failure";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("SpaceX", tempMission.company(), "The company of the mission shall be SpaceX");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // comparing the dates
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // comparing the rocket details
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // comparing the rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_RETIRED, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Retired");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.PARTIAL_FAILURE, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Partial_Failure");
    }

    @Test
    void testMissionOfValidArgumentsWithPreLaunchFailureMissionStatusCreatesValidMission() {
        // This test focuses on creating a Mission with mission status of MissionStatus.PRELAUNCH_FAILURE
        String line = "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\"," +
                "\"Mon Jul 20, 2020\",Falcon 9 Block 5 | ANASIS-II,StatusRetired,\"50.0 \",Prelaunch Failure";
        Mission tempMission = Mission.of(line);

        assertEquals("8", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("SpaceX", tempMission.company(), "The company of the mission shall be SpaceX");
        assertEquals("SLC-40, Cape Canaveral AFS, Florida, USA", tempMission.location(),
                "The loc of the mission shall be SLC-40, Cape Canaveral AFS, Florida, USA");

        // comparing the dates
        LocalDate dateOfMission =
                LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Mon Jul 20, 2020");

        // comparing the rocket details
        Detail rocketDetails = new Detail("Falcon 9 Block 5", "ANASIS-II");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Falcon 9 Block 5");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ANASIS-II");

        // comparing the rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_RETIRED, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Retired");
        assertEquals(EXPECTED_VALUE_FOR_MISSION_8, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 50.0");
        assertEquals(MissionStatus.PRELAUNCH_FAILURE, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Prelaunch_Failure");
    }

    @Test
    void testValidArgumentsDetailWithinQuotesCreatesValidMission() {
        // This test focuses on getting the proper Details data when it is inside quotes
        String line = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMission = Mission.of(line);

        assertEquals("2112", tempMission.id(), "The Id of the mission shall be 8");
        assertEquals("Arianespace", tempMission.company(),
                "The company of the mission shall be SpaceX");
        assertEquals("ELA-1, Guiana Space Centre, French Guiana, France", tempMission.location(),
                "The loc of the mission shall be ELA-1, Guiana Space Centre, French Guiana, France");

        // comparing the dates
        LocalDate dateOfMission =
                LocalDate.parse("Thu Sep 12, 1985", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(dateOfMission, tempMission.date(), "The date shall be Thu Sep 12, 1985");

        // comparing the rocket details
        Detail rocketDetails = new Detail("Ariane 3", "ECS 3, Spacenet 3");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be Ariane 3");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be ECS 3, Spacenet 3");

        // comparing the rocket status, mission cost and mission status
        assertEquals(RocketStatus.STATUS_RETIRED, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Retired");
        assertEquals(0.0d, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 0.0");
        assertEquals(MissionStatus.FAILURE, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.Failure");
    }

    @Test
    void testEmptyArgumentsCreatesValidMissionWithEmptyFields() {
        String line = ",,,,,,,";
        Mission tempMission = Mission.of(line);

        assertEquals("", tempMission.id(), "The Id of the mission shall be empty");
        assertEquals("", tempMission.company(), "The company of the mission shall be empty");
        assertEquals("", tempMission.location(),
                "The loc of the mission shall be empty");

        assertEquals(LocalDate.now(), tempMission.date(), "The date shall be the current one");

        Detail rocketDetails = new Detail("", "");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be empty");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be empty");

        assertEquals(RocketStatus.STATUS_RETIRED, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Retired");
        assertEquals(0.0d, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 0.0");
        assertEquals(MissionStatus.PRELAUNCH_FAILURE, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.PreLaunch_Failure");
    }

    @Test
    void testEmptyStringCreatesValidMissionWithEmptyFields() {
        String line = "";
        Mission tempMission = Mission.of(line);

        assertEquals("", tempMission.id(), "The Id of the mission shall be empty");
        assertEquals("", tempMission.company(), "The company of the mission shall be empty");
        assertEquals("", tempMission.location(),
                "The loc of the mission shall be empty");

        assertEquals(LocalDate.now(), tempMission.date(), "The date shall be the current one");

        Detail rocketDetails = new Detail("", "");
        assertEquals(rocketDetails.rocketName(), tempMission.detail().rocketName(),
                "The rocket name shall be empty");
        assertEquals(rocketDetails.payload(), tempMission.detail().payload(),
                "The rocket payload shall be empty");

        assertEquals(RocketStatus.STATUS_RETIRED, tempMission.rocketStatus(),
                "The rocket status shall be RocketStatus.Success_Retired");
        assertEquals(0.0d, tempMission.cost().orElse(0.0d),
                "The cost of the mission shall be 0.0");
        assertEquals(MissionStatus.PRELAUNCH_FAILURE, tempMission.missionStatus(),
                "The mission status shall be MissionStatus.PreLaunch_Failure");
    }

    @Test
    void testMissionsEqualsOneMissionIsNull() {
        String line = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMission = Mission.of(line);

        assertNotEquals(null, tempMission,
                "When comparing one mission to null, equls() should return null");
    }

    @Test
    void testMissionsEqualTheOtherMissionIsNull() {
        String line = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMission = Mission.of(line);

        assertFalse(tempMission.equals(null),
                "When comparing one mission to null, equls() should return null");
    }

    @Test
    void testMissionsEqualsDifferentClassReturnsNull() {
        String line = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMission = Mission.of(line);

        String rocketLine = "127,Delta II 7320-10C,https://en.wikipedia.org/wiki/Delta_II,38.9 m";
        Rocket tempRocket = Rocket.of(rocketLine);

        assertNotEquals(tempMission, tempRocket, "When comparing one mission to another class," +
                " for example Rocket.class, equls() should return null");
    }

    @Test
    void testMissionsEqualsSameMissiosnReturnTrue() {
        String line = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(line);
        Mission tempMissionTwo = Mission.of(line);

        assertEquals(tempMissionOne, tempMissionTwo,
                "When comparing two equals missions with missions should return true");
    }

    @Test
    void testMissionsEqualsDifferInIdReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        String lineTwo = "211,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in id return false");
    }

    @Test
    void testMissionsEqualsDifferInCompanyReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        String lineTwo = "2112,Arnespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in company return false");
    }

    @Test
    void testMissionsEqualsDifferInLocationReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        String lineTwo = "2112,Arianespace,\"ELA-1, Guiana, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in location return false");
    }

    @Test
    void testMissionsEqualsDifferInDateReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        String lineTwo = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Wed Sep 11, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in date return false");
    }

    @Test
    void testMissionsEqualsDifferInDetailsReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ariane 3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        String lineTwo = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ari3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in details return false");
    }

    @Test
    void testMissionsEqualsDifferInRocketStatusReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ari3 | ECS 3, Spacenet 3\",StatusActive,,Failure";
        String lineTwo = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ari3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in rocket status return false");
    }

    @Test
    void testMissionsEqualsDifferInHeightReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ari3 | ECS 3, Spacenet 3\",StatusActive,\"64.68 \",Failure";
        String lineTwo = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ari3 | ECS 3, Spacenet 3\",StatusActive,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in height return false");
    }

    @Test
    void testMissionsEqualsDifferInMissionStatusReturnsFalse() {
        String lineOne = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ari3 | ECS 3, Spacenet 3\",StatusRetired,,Success";
        String lineTwo = "2112,Arianespace,\"ELA-1, Guiana Space Centre, French Guiana, France\"," +
                "\"Thu Sep 12, 1985\",\"Ari3 | ECS 3, Spacenet 3\",StatusRetired,,Failure";
        Mission tempMissionOne = Mission.of(lineOne);
        Mission tempMissionTwo = Mission.of(lineTwo);

        assertNotEquals(tempMissionOne, tempMissionTwo,
                "When comparing two missions who differ in mission status return false");
    }
}
