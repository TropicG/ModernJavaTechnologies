package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    // currently this symbol is not found anywhere in the sheets, it could be used for separator
    private static final String FORMATED_SEPARATE_REGEX = "@";
    // if empty argument is found for a String object, this will be assigned
    private static final String EMPTY_ARGUMENT = "";

    private static final char CHAR_ARGUMENT_SEPARATOR = ',';
    private static final char QUOTA = '"';

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int WIKI_INDEX = 2;
    private static final int HEIGHT_INDEX = 3;
    private static final int TOTAL_ARGS = 4;

    public static Rocket of(String line) {
        // adding @ in place of , to have better argument separation
        StringBuilder formatedLine = formatLine(line);

        // arguments are going to be separated on @, in case of insufficient args empty args are added
        List<String> arguments = new ArrayList<>(List.of(formatedLine.toString().split(FORMATED_SEPARATE_REGEX)));
        while (arguments.size() < TOTAL_ARGS) {
            arguments.add(EMPTY_ARGUMENT);
        }

        return new Rocket(
                arguments.get(ID_INDEX).trim(),
                arguments.get(NAME_INDEX).trim(),
                formatWiki(arguments.get(WIKI_INDEX).trim()),
                formatHeight(arguments.get(HEIGHT_INDEX).trim())
        );
    }

    private static Optional<String> formatWiki(String argument) {
        return argument.isBlank() ? Optional.empty() : Optional.of(argument);
    }

    private static Optional<Double> formatHeight(String argument) {
        String formatedHeight = argument.toLowerCase().replace(",", "").replace("m", "").trim();

        if (argument.isBlank()) {
            return Optional.empty();
        }

        // m suffix is read for the height from the csv file that is why it is needed to get the substring
        return Optional.of(Double.parseDouble(formatedHeight));
    }

    private static StringBuilder formatLine(String line) {
        // every , not found within quotes will be replaced with %
        // this is done in order to have more precise splitting between the arguments from the string
        // Note: this function also removes "" in order to have more raw arguments
        boolean foundQuote = false;
        StringBuilder formatedLine = new StringBuilder();
        for (Character letter : line.toCharArray()) {
            if ((letter == CHAR_ARGUMENT_SEPARATOR) && !foundQuote) {
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
        return "Rocket{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", wiki=" + wiki.orElse("") +
                ", height=" + height.orElse(0.0d) +
                '}';
    }
}

