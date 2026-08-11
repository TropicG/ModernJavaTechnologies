package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date,
                      Detail detail, RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {
    private static final int ID_INDEX = 0;
    private static final int COMPANY_INDEX = 1;
    private static final int LOCATION_INDEX = 2;
    private static final int DATE_INDEX = 3;
    private static final int ROCKET_NAME_INDEX = 4;
    private static final int ROCKET_PAYLOAD_INDEX = 5;
    private static final int ROCKET_STATUS_INDEX = 6;
    private static final int COST_INDEX = 7;
    private static final int MISSION_STATUS_INDEX = 8;
    private static final int TOTAL_ARGS = 9;

    private static final char CHAR_ARGUMENT_SEPARATOR = ',';
    private static final char DETAIL_SEPARATOR = '|';
    private static final char QUOTA = '"';

    // currently this symbol is not found anywhere in the sheets, it could be used for separator
    private static final String FORMATED_SEPARATE_REGEX = "@";
    // if empty argument is found for a String object, this will be assigned
    private static final String EMPTY_STRING = "";
    // some missions contains cost with , in the csv file, this variable is used to remove the comma if found
    private static final String ARGUMENT_SEPARATOR = ",";

    // AI, I researched how to accept a date pattern of Mon Jul 20, 2020 for example
    private static final String INPUT_DATA_PATTERN = "EEE MMM d, yyyy";

    private static final String CHECK_ACTIVE_STRING = "Active";
    private static final String CHECK_PARTIAL_FAILURE = "Partial";

    public static Mission of(String line) {
        // adding @ in place of , to have better argument separation
        StringBuilder formatedLine = formatLine(line);

        // arguments are going to be separated on @, in case of insufficient args empty args are added
        List<String> arguments = new ArrayList<>(List.of(formatedLine.toString().split(FORMATED_SEPARATE_REGEX)));
        while (arguments.size() < TOTAL_ARGS) {
            arguments.add(EMPTY_STRING);
        }

        return new Mission(
                arguments.get(ID_INDEX).trim(),
                arguments.get(COMPANY_INDEX).trim(),
                arguments.get(LOCATION_INDEX).trim(),
                formatDate(arguments.get(DATE_INDEX)),
                new Detail(arguments.get(ROCKET_NAME_INDEX).trim(), arguments.get(ROCKET_PAYLOAD_INDEX).trim()),
                formatRocketStatus(arguments.get(ROCKET_STATUS_INDEX).trim()),
                formatCost(arguments.get(COST_INDEX).trim()),
                formatMissionStatus(arguments.get(MISSION_STATUS_INDEX).trim())
        );
    }

    private static Optional<Double> formatCost(String line) {
        if (line.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Double.parseDouble(line.replace(ARGUMENT_SEPARATOR, EMPTY_STRING)));
    }

    private static LocalDate formatDate(String stringDate) {
        if (stringDate.isEmpty()) {
            return LocalDate.now();
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(INPUT_DATA_PATTERN, Locale.ENGLISH);
        return LocalDate.parse(stringDate, inputFormatter);
    }

    private static RocketStatus formatRocketStatus(String line) {
        if (line.contains(CHECK_ACTIVE_STRING)) {
            return RocketStatus.STATUS_ACTIVE;
        } else {
            return RocketStatus.STATUS_RETIRED;
        }
    }

    private static MissionStatus formatMissionStatus(String line) {
        if (line.equals(MissionStatus.SUCCESS.toString())) {
            return MissionStatus.SUCCESS;
        } else if (line.equals(MissionStatus.FAILURE.toString())) {
            return MissionStatus.FAILURE;
        } else if (line.contains(CHECK_PARTIAL_FAILURE)) {
            return MissionStatus.PARTIAL_FAILURE;
        } else {
            return MissionStatus.PRELAUNCH_FAILURE;
        }
    }

    private static StringBuilder formatLine(String line) {
        // every , not found within quotes will be replaced with %
        // this is done in order to have more precise splitting between the arguments from the string
        // Note: this function also removes "" in order to have more raw arguments
        boolean foundQuote = false;
        StringBuilder formatedLine = new StringBuilder();
        for (Character letter : line.toCharArray()) {
            if ((letter == CHAR_ARGUMENT_SEPARATOR || letter == DETAIL_SEPARATOR) && !foundQuote) {
                formatedLine.append(FORMATED_SEPARATE_REGEX);
            } else if (letter == DETAIL_SEPARATOR) {
                formatedLine.append(FORMATED_SEPARATE_REGEX);
            } else if (letter == QUOTA && !foundQuote) {
                foundQuote = true;
            } else if (letter == QUOTA) {
                foundQuote = false;
            } else {
                formatedLine.append(letter);
            }
        }
        return formatedLine;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id='" + id + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", detail=" + detail +
                ", rocketStatus=" + rocketStatus +
                ", cost=" + cost.orElse(0.0d) +
                ", missionStatus=" + missionStatus +
                '}';
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }

        Mission otherMission = (Mission) object;
        return this.id.equals(otherMission.id) &&
                this.company.equals(otherMission.company) &&
                this.location.equals(otherMission.location) &&
                this.date.equals(otherMission.date) &&
                this.detail.equals(otherMission.detail) &&
                this.rocketStatus.equals(otherMission.rocketStatus) &&
                this.cost.orElse(0.0d).equals(otherMission.cost.orElse(0.0d)) &&
                this.missionStatus.equals(otherMission.missionStatus);
    }
}
