package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private final List<Mission> missionsList;
    private final List<Rocket> rocketsList;
    private final SecretKey secretKey;

    private static final String ENCRYPTION_ALGORITHM = "AES";

    private static final int LEGENDS_LINE = 1;
    private static final double RELIABILITY = 2.0d;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        // reading from the file all lines of information to be loaded into the missionsList
        BufferedReader missionBufferReader = new BufferedReader(missionsReader);
        missionsList = missionBufferReader.lines()
                .skip(LEGENDS_LINE)
                .map(Mission::of)
                .toList();

        // reading from the file all lines of information to be loaded into the rocketList
        BufferedReader rocketBufferReader = new BufferedReader(rocketsReader);
        rocketsList = rocketBufferReader.lines()
                .skip(LEGENDS_LINE)
                .map(Rocket::of)
                .toList();

        this.secretKey = secretKey;
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missionsList.stream().toList();
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Null is not a valid argument when getting all the missions");
        }

        // filters all missions to have the same missionsStatus
        return missionsList.stream()
                .filter(mission -> mission.missionStatus() == missionStatus)
                .toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to cannot be null");
        } else if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("to is before from");
        }
        // predicate that checks is a mission in the time period
        Predicate<Mission> isMissionInPeriod = mission ->
                ((mission.date().equals(from) || mission.date().isAfter(from)) &&
                        (mission.date().isBefore(to) || (mission.date().equals(to))));

        //returns a map containing for each company how many rockets have been launched with success in period [from,to]
        Map<String, Long> companiesWithSuccessfulLaunches = missionsList.stream()
                .filter(isMissionInPeriod)
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .collect(Collectors.groupingBy(Mission::company, Collectors.counting()));

        // gets the company with the most successful launches
        Optional<Map.Entry<String, Long>> companyWithMostSuccessfulMissions =
                companiesWithSuccessfulLaunches.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue());

        // if the value is present it will be returned, otherwise an empty string
        return companyWithMostSuccessfulMissions.isPresent() ?
                companyWithMostSuccessfulMissions.get().getKey() :
                "";
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        // groups for every country the missions that happened in its territory
        // Note: Some missions happened in the Pacific Ocean, missions are grouped even on geographical locations
        return missionsList.stream()
                .collect(Collectors.groupingBy(
                        mission -> formatCountry(mission.location().split(",")).trim(),
                        Collectors.toCollection(ArrayList::new)
                ));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("n cannot be 0 or below it when getting least expensive missions");
        } else if (missionStatus == null) {
            throw new IllegalArgumentException("mission status cannot be null when getting least expensive missions");
        } else if (rocketStatus == null) {
            throw new IllegalArgumentException("rocket status cannot be null when getting least expensive missions");
        }
        // missions will be based on the given parameters
        Predicate<Mission> filteredBasedOnMissionAndRocketStatus = mission ->
                mission.missionStatus() == missionStatus && mission.rocketStatus() == rocketStatus;

        return missionsList.stream()
                .filter(filteredBasedOnMissionAndRocketStatus)
                // ensuring we will have missions that have an information regarding their cost
                .filter(mission -> mission.cost().isPresent())
                // sorting in desc order
                .sorted(Comparator.comparingDouble(mission -> mission.cost().get()))
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        // for each company, count how many missions where on a location
        Map<String, Map<String, Long>> missionsPerCompany = missionsList.stream()
                .collect(Collectors.groupingBy(
                        Mission::company,
                        Collectors.groupingBy(Mission::location, Collectors.counting())
                ));

        // for each company, compare which is the most desired location
        return missionsPerCompany.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("")
                ));
    }

    private Map<String, Map<String, Long>> getSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        // predicate that checks is a mission in the time period
        Predicate<Mission> isMissionInPeriod = mission ->
                ((mission.date().equals(from) || mission.date().isAfter(from)) &&
                        (mission.date().isBefore(to) || (mission.date().equals(to))));

        // for each company, count the times for each location that had missions in [from,to] which were successful
        return missionsList.stream()
                .filter(isMissionInPeriod)
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .collect(
                        Collectors.groupingBy(
                                Mission::company,
                                Collectors.groupingBy(
                                        Mission::location,
                                        Collectors.counting()
                                )
                        )
                );
    }

    private Map<String, String> getForEachCompanyMostSuccessfulLocation(
            Map<String, Map<String, Long>> successfulMissionsPerCompany) {
        // for each company assign the location with the most succ missions
        return successfulMissionsPerCompany.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("")
                ));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null) {
            throw new IllegalArgumentException("from date cannot be null");
        } else if (to == null) {
            throw new IllegalArgumentException("to date cannot be null");
        } else if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("to date is before from date");
        }

        // get for each company every location and with the number of successful missions on it
        Map<String, Map<String, Long>> successfulMissionsPerCompany = getSuccessfulMissionsPerCompany(from, to);

        // for each company assign the location with the most successful missions
        return getForEachCompanyMostSuccessfulLocation(successfulMissionsPerCompany);
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rocketsList.stream().toList();
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The N argument cannot be 0 or lower");
        }

        return rocketsList.stream()
                // filtering to only those rockets that we have information for their height
                .filter(mission -> mission.height().isPresent())
                // comparing based on asc order
                .sorted(Comparator.comparingDouble((Rocket rocket) -> rocket.height().get()).reversed())
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rocketsList.stream()
                // filtering to only those rockets that we have wiki for
                .filter(rocket -> rocket.wiki().isPresent())
                .collect(Collectors.toMap(
                        Rocket::name,
                        Rocket::wiki
                ));
    }

    private Set<String> getRocketNamesInMostExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        return missionsList.stream()
                // all the missions with missions status and rocket status passed as arguments
                .filter(mission -> mission.missionStatus() == missionStatus
                        && mission.rocketStatus() == rocketStatus)
                // getting those missions that have cost
                .filter(mission -> mission.cost().isPresent())
                // sorting the missions based on their cost, the expensive are first
                .sorted(Comparator.comparingDouble((Mission mission) -> mission.cost().get()).reversed())
                // getting the top n
                .limit(n)
                // getting from the missions the rocket name
                .map(mission -> mission.detail().rocketName())
                .collect(Collectors.toSet());
    }

    List<String> getWikisForRocketsUsedInTheMostExpensiveMissions(Set<String> mostExpensiveMissions) {
        return rocketsList.stream()
                .filter(rocket -> rocket.wiki().isPresent())
                // filters to all the rockets that were used in the most n expensive missions
                .filter(rocket -> mostExpensiveMissions.contains(rocket.name()))
                // changes the rocket names to their wiki
                .map(rocket -> rocket.wiki().get())
                .toList();
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("0 or below 0 is not valid argument");
        } else if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null");
        } else if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null");
        }

        Set<String> rocketNamesInMostExpensiveMissions =
                getRocketNamesInMostExpensiveMissions(n, missionStatus, rocketStatus);
        return getWikisForRocketsUsedInTheMostExpensiveMissions(rocketNamesInMostExpensiveMissions);
    }

    private List<Mission> getMissionsInTimeFrame(LocalDate from, LocalDate to) {
        // predicate that checks is a mission in the time period
        Predicate<Mission> isMissionInPeriod = mission ->
                ((mission.date().equals(from) || mission.date().isAfter(from)) &&
                        (mission.date().isBefore(to) || (mission.date().equals(to))));

        // gets all the missions in the time period and rockets used in those missions, no rockets with missing names
        return missionsList.stream()
                .filter(isMissionInPeriod)
                .filter(mission -> !mission.detail().rocketName().isBlank())
                .toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null) {
            throw new IllegalArgumentException("The output stream cannot be null");
        } else if (from == null) {
            throw new IllegalArgumentException("From date cannot be null");
        } else if (to == null) {
            throw new IllegalArgumentException("To date cannot be null");
        } else if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To date cannot be null");
        } else if (rocketsList.isEmpty() || missionsList.isEmpty()) {
            // in case of no data inserted into the program, there is no way that the most reliable rocket can be saved
            return;
        }
        // gets all the missions in the time period and rockets used in those missions, no rockets with missing names
        List<Mission> allMissionsInTimeFrame = getMissionsInTimeFrame(from, to);
        if (allMissionsInTimeFrame.isEmpty()) {
            // there could be no missions in this time period or the names of the rockets to be missing
            return;
        }

        Set<String> allRocketNames = allMissionsInTimeFrame.stream()
                .map(mission -> mission.detail().rocketName()).collect(Collectors.toSet());

        Map<String, Long> allSuccessfulMissionsPerRocket = getAllSuccessfulMissionsPerRocket(allMissionsInTimeFrame);
        Map<String, Long> allFailureMissionsPerRocket = getAllFailureMissionsPerRocket(allMissionsInTimeFrame);

        String mostReliableRocket = getMostReliableRocket(allRocketNames, allSuccessfulMissionsPerRocket,
                allFailureMissionsPerRocket);

        encryptMostReliableRocket(mostReliableRocket, outputStream);
    }

    private void encryptMostReliableRocket(String mostReliableRocket, OutputStream outputStream)
            throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                cipherOutputStream.write(mostReliableRocket.getBytes(StandardCharsets.UTF_8));
                cipherOutputStream.flush();
            }
        } catch (Exception exception) {
            throw new CipherException("Encryption information cannot complete succesfully", exception);
        }
    }

    private String getMostReliableRocket(Set<String> allRocketNames,
                                         Map<String, Long> allSuccessfulMissionsPerRocket,
                                         Map<String, Long> allFailureMissionsPerRocket) {
        // lambda function to calculate the reliability value
        Function<String, Double> reliabilityCalc = rocketName ->
                ((RELIABILITY * allSuccessfulMissionsPerRocket.getOrDefault(rocketName, (long) 0))
                        + allFailureMissionsPerRocket.getOrDefault(rocketName, (long) 0))
                        /
                        (RELIABILITY * (allFailureMissionsPerRocket.getOrDefault(rocketName, (long) 0) +
                                allSuccessfulMissionsPerRocket.getOrDefault(rocketName, (long) 0)));

        return allRocketNames.stream()
                // for each rocket name its reliability value will be calculated
                // NOTE: There will be no conflicts since allRocketsNames are made to sure to be distinct
                .collect(Collectors.toMap(
                        rocketName -> rocketName,
                        reliabilityCalc))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
    }

    private Map<String, Long> getAllSuccessfulMissionsPerRocket(List<Mission> allMissionsInTimeFrame) {
        // counts how many successful missions were for different rockets
        return allMissionsInTimeFrame.stream()
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .collect(Collectors.groupingBy(
                        mission -> mission.detail().rocketName(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getAllFailureMissionsPerRocket(List<Mission> allMissionsInTimeFrame) {
        // counts how many failed missions were for different rockets
        return allMissionsInTimeFrame.stream()
                .filter(mission -> mission.missionStatus() == MissionStatus.FAILURE
                        || mission.missionStatus() == MissionStatus.PRELAUNCH_FAILURE
                        || mission.missionStatus() == MissionStatus.PARTIAL_FAILURE)
                .collect(Collectors.groupingBy(
                        mission -> mission.detail().rocketName(),
                        Collectors.counting()
                ));
    }

    private String formatCountry(String[] locationArgs) {
        return locationArgs[locationArgs.length - 1];
    }
}
