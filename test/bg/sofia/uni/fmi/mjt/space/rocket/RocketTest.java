package bg.sofia.uni.fmi.mjt.space.rocket;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RocketTest {
    private static final double EXPECTED_HEIGHT_FOR_ATLAS_V = 58.3d;
    private static final double EXPECTED_HEIGHT_FOR_DELTA_IV = 62.5;

    @Test
    void testRocketOfCreatesRocketWithValidArgs() {
        // reads the strings and parses its argument to create a valid rocket
        String line = "95,Atlas V 401,https://en.wikipedia.org/wiki/Atlas_V,58.3 m";
        Rocket tempRocket = Rocket.of(line);

        assertEquals("95", tempRocket.id(),
                "The Id of the rocket shall be 95");

        assertEquals("Atlas V 401", tempRocket.name(),
                "The Name of the rocket shall be Atlas V 401");

        assertEquals("https://en.wikipedia.org/wiki/Atlas_V", tempRocket.wiki().orElse(""),
                "The wiki shall be https://en.wikipedia.org/wiki/Atlas_V,58.3 m");

        assertEquals(EXPECTED_HEIGHT_FOR_ATLAS_V, tempRocket.height().orElse(0.0d),
                "The height of the rocket is 58.3");
    }

    @Test
    void testRocketOfWithMissingWikiCreatesValidRocketWithEmptyWiki() {
        // reads the strings and parses its argument to create a valid rocket
        String line = "95,Atlas V 401,,58.3 m";
        Rocket tempRocket = Rocket.of(line);

        assertEquals("95", tempRocket.id(), "The Id of the rocket shall be 95");
        assertEquals("Atlas V 401", tempRocket.name(),
                "The Name of the rocket shall be Atlas V 401");

        assertEquals(Optional.empty(), tempRocket.wiki(),
                "The wiki shall have value of Optional.empty");

        // when wiki is missing it is expected that the data member will have Optional.empty()
        assertEquals("", tempRocket.wiki().orElse(""),
                "The wiki shall have value of empty string");
        assertEquals(EXPECTED_HEIGHT_FOR_ATLAS_V, tempRocket.height().orElse(0.0d),
                "The height of the rocket is 58.3");
    }

    @Test
    void testRocketOfMissingWikiAndHeightCreatesValidRocketWithEmptyWikiAndHeight() {
        // reads the strings and parses its argument to create a valid rocket
        String line = "95,Atlas V 401,,";
        Rocket tempRocket = Rocket.of(line);

        assertEquals("95", tempRocket.id(), "The Id of the rocket shall be 95");
        assertEquals("Atlas V 401", tempRocket.name(),
                "The Name of the rocket shall be Atlas V 401");
        assertEquals(Optional.empty(), tempRocket.wiki(),
                "The wiki shall have value of Optional.empty");
        assertEquals("", tempRocket.wiki().orElse(""),
                "The wiki shall have value of empty string");

        // when height is missing in the string passed for parsing, Optional.empty() should be assigned to the height
        assertEquals(Optional.empty(), tempRocket.height(),
                "The height of the rocket shall have value of Optional.empty");
        assertEquals(0.0d, tempRocket.height().orElse(0.0d),
                "The height of the rocket shall have value of Optional.empty");
    }

    @Test
    void testRocketOfValidArgumentsWithQuotesCreatesValidRocket() {
        // When we have quotes in the string for parsing, everything is expected to be taken
        String line = "148,\"Delta IV Medium+ (4,2)\",https://en.wikipedia.org/wiki/Delta_IV,62.5 m";
        Rocket tempRocket = Rocket.of(line);

        assertEquals("148", tempRocket.id(), "The Id of the rocket shall be 148");
        assertEquals("Delta IV Medium+ (4,2)", tempRocket.name(),
                "The Name of the rocket shall be Delta IV Medium+ (4,2)");

        assertEquals("https://en.wikipedia.org/wiki/Delta_IV", tempRocket.wiki().orElse(""),
                "The wiki shall be https://en.wikipedia.org/wiki/Delta_IV");
        assertEquals(EXPECTED_HEIGHT_FOR_DELTA_IV, tempRocket.height().orElse(0.0d),
                "The height of the rocket is 62.5");
    }

    @Test
    void testRocketOfMissingIDCreatesValidRocketWithEmptyID() {
        String line = ",Atlas V 401,,";
        Rocket tempRocket = Rocket.of(line);

        assertEquals("", tempRocket.id(), "The Id of the rocket shall be empty");
        assertEquals("Atlas V 401", tempRocket.name(),
                "The Name of the rocket shall be Atlas V 401");
        assertEquals(Optional.empty(), tempRocket.wiki(),
                "The wiki shall have value of Optional.empty");
        assertEquals("", tempRocket.wiki().orElse(""),
                "The wiki shall have value of empty string");
        assertEquals(Optional.empty(), tempRocket.height(),
                "The height of the rocket shall have value of Optional.empty");
        assertEquals(0.0d, tempRocket.height().orElse(0.0d),
                "The height of the rocket shall have value of Optional.empty");
    }

    @Test
    void testRocketOfMissingEverythingCreatesValidRocketWithEmptyEverything() {
        // If a string for parsing misses every argument, it should create rocket with empty data member values
        String line = ",,,";
        Rocket tempRocket = Rocket.of(line);

        assertEquals("", tempRocket.id(), "The Id of the rocket shall be empty");
        assertEquals("", tempRocket.name(), "The Name of the rocket shall be empty");

        assertEquals(Optional.empty(), tempRocket.wiki(),
                "The wiki shall have value of Optional.empty");

        assertEquals("", tempRocket.wiki().orElse(""),
                "The wiki shall have value of empty string");

        assertEquals(Optional.empty(), tempRocket.height(),
                "The height of the rocket shall have value of Optional.empty");

        assertEquals(0.0d, tempRocket.height().orElse(0.0d),
                "The height of the rocket shall have value of Optional.empty");
    }

    @Test
    void testRocketOfEmptyArgStringCreatesValidRocketEmptyFields() {
        // Even if the string is totally empty, proper rocket will be created with empty values for the data members
        String line = "";
        Rocket tempRocket = Rocket.of(line);

        assertEquals("", tempRocket.id(), "The Id of the rocket shall be empty");
        assertEquals("", tempRocket.name(), "The Name of the rocket shall be empty");
        assertEquals(Optional.empty(), tempRocket.wiki(),
                "The wiki shall have value of Optional.empty");
        assertEquals("", tempRocket.wiki().orElse(""),
                "The wiki shall have value of empty string");
        assertEquals(Optional.empty(), tempRocket.height(),
                "The height of the rocket shall have value of Optional.empty");
        assertEquals(0.0d, tempRocket.height().orElse(0.0d),
                "The height of the rocket shall have value of Optional.empty");
    }
}
