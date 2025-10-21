package fmi.mjt.uniquesubstringfinder;

import java.util.Set;
import java.util.HashSet;

public class UniqueSubstringFinder {


    //O(N^2) time complexity, here is used only brute force without any helping data structures
    //it implements the open window method
    public static String longestUniqueSubstringV1(String str) {


        //here we will apend characters in order to check do we have proper unique substring
        StringBuilder currentSubstring = new StringBuilder();
        String longestSubstring = "";

        //looking through the whole str string
        for(int i = 0; i < str.length(); i++) {

            boolean isFound = false;
            int indexOfDublication = 0;

            //checking if we have dublication in currentSubstring, if we have we save the index for letter
            for(int j = 0; j < currentSubstring.length(); j++) {
                if(currentSubstring.charAt(j) == str.charAt(i)) {
                    isFound = true;
                    indexOfDublication = j;
                    break;
                }
            }


            if(!isFound) {
                currentSubstring.append(str.charAt(i));
            }
            else {

                // removing character before the duplicate as well as the duplicate
                // imagine we are removing everything from the left side of the current substring until we meet and delete the dublicated letter
                currentSubstring.delete(0, indexOfDublication + 1);
                currentSubstring.append(str.charAt(i));
            }

            if(currentSubstring.length() >= longestSubstring.length()) {
                longestSubstring = currentSubstring.toString();
            }

        }

        return longestSubstring;
    }

    //O(N) time complexity, because of the HashSet<>
    //It is not O(N^2) this time, since index i and startIndex iterate only once meaning this is O(N) + O(N) = O(N)
    public static String longestUniqueSubstringV2(String str) {

        String maximumSubstring = "";
        Set<Character> encounteredCharacters = new HashSet<>();

        int startIndex = 0;

        for(int i = 0; i < str.length(); i++) {

            if(!encounteredCharacters.contains(str.charAt(i))){
                encounteredCharacters.add(str.charAt(i));
            }
            else {

                //everything before the meaning character will be removed from the current substring
                while(startIndex < i) {

                    //if we have met the dublicated character, we skipp it since it is already in the HashSet
                    //and we want to keep it there
                    if(str.charAt(startIndex) == str.charAt(i)) {
                        startIndex++;
                        break;
                    }

                    //everything else is going to be removed from the current subtring
                    encounteredCharacters.remove(str.charAt(startIndex));
                    startIndex++;

                }
            }

            if(str.substring(startIndex, i + 1).length() > maximumSubstring.length()) {
                maximumSubstring = str.substring(startIndex, i + 1);
            }
        }

        return maximumSubstring;
    }


    //O(N^3), although it is not very efficient it still solves our problem in an interesting way
    //it works with taking every possible substring from string with size S and solve for S, S-1, S-2
    public static String longestUniqueSubstringV3(String str) {

        int currentLength = str.length();

        // first we search for the longest unique substring with length 9 for example
        // after that we search with length 8 and so on
        // We skip when we reach length 1 because it is always going to be unique
        while(currentLength != 1) {


            int index = 0;

            // if for example we are looking for that substring with length 8 in a str with length 10
            // we are going to have those options: [0, 7], [1, 8], [2,9]
            // this while loop is to check all those possible substrings
            while( ( index + currentLength - 1 ) < str.length()) {

                int endIndex = index + currentLength;

                //checking every possible substring if it is unique, if it is we are returning it imediately
                if(isWithUniqueLetters(str.substring( index, endIndex))) {
                    return str.substring( index,endIndex );
                }

                index++;
            }

            //if we couldn't find substring with size S, we are going to check for size S - 1
            currentLength--;
        }

        //in the end we return only one substring, if we couldn't find anything
        return str.substring(0,1);
    }

    private static boolean isWithUniqueLetters(String str) {

        Set<Character> currentLetters = new HashSet<>();

        for(int i = 0; i < str.length(); i++) {

            if(currentLetters.contains(str.charAt(i))){
                return false;
            }
            else {
                currentLetters.add(str.charAt(i));
            }

        }

        return true;
    }

    public static void main(String[] args) {


        System.out.println("exdnmjqaof");
        System.out.println("Version 1: " + longestUniqueSubstringV1("exdnmjqaof"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("exdnmjqaof"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("exdnmjqaof"));
        System.out.println();



        System.out.println("gnbomcqcux");
        System.out.println("Version 1: " + longestUniqueSubstringV2("gnbomcqcux"));
        System.out.println("Version 2: " + longestUniqueSubstringV1("gnbomcqcux"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("gnbomcqcux"));
        System.out.println();

        System.out.println("ynkbnfjayx");
        System.out.println("Version 1: " + longestUniqueSubstringV1("ynkbnfjayx"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("ynkbnfjayx"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("ynkbnfjayx"));
        System.out.println();

        System.out.println("jogrtblunu");
        System.out.println("Version 1: " + longestUniqueSubstringV1("jogrtblunu"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("jogrtblunu"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("jogrtblunu"));
        System.out.println();

        System.out.println("hmatjqhpyc");
        System.out.println("Version 1: " + longestUniqueSubstringV1("hmatjqhpyc"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("hmatjqhpyc"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("hmatjqhpyc"));
        System.out.println();

        System.out.println("sqgorigqvf");
        System.out.println("Version 1: " + longestUniqueSubstringV1("sqgorigqvf"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("sqgorigqvf"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("sqgorigqvf"));
        System.out.println();

        System.out.println("djhitggohh");
        System.out.println("Version 1: " + longestUniqueSubstringV1("djhitggohh"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("djhitggohh"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("djhitggohh"));
        System.out.println();

        System.out.println("fdsyyfcvxg");
        System.out.println("Version 1: " + longestUniqueSubstringV1("fdsyyfcvxg"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("fdsyyfcvxg"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("fdsyyfcvxg"));
        System.out.println();

        System.out.println("tjiyrejtky");
        System.out.println("Version 1: " + longestUniqueSubstringV1("tjiyrejtky"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("tjiyrejtky"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("tjiyrejtky"));
        System.out.println();

        System.out.println("bpjsqtcuam");
        System.out.println("Version 1: " + longestUniqueSubstringV1("bpjsqtcuam"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("bpjsqtcuam"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("bpjsqtcuam"));
        System.out.println();

        System.out.println("deniemnogohubava");
        System.out.println("Version 1: " + longestUniqueSubstringV1("deniemnogohubava"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("deniemnogohubava"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("deniemnogohubava"));
        System.out.println();


        System.out.println("aaaaaaaaa");
        System.out.println("Version 1: " + longestUniqueSubstringV1("aaaaaaaaa"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("aaaaaaaaa"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("aaaaaaaaa"));
        System.out.println();

        System.out.println("abcabcbb");
        System.out.println("Version 1: " + longestUniqueSubstringV1("abcabcbb"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("abcabcbb"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("abcabcbb"));
        System.out.println();

        System.out.println("pwwkew");
        System.out.println("Version 1: " + longestUniqueSubstringV1("pwwkew"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("pwwkew"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("pwwkew"));
        System.out.println();

        System.out.println("abcdefg");
        System.out.println("Version 1: " + longestUniqueSubstringV1("abcdefg"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("abcdefg"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("abcdefg"));
        System.out.println();

        System.out.println("x");
        System.out.println("Version 1: " + longestUniqueSubstringV1("x"));
        System.out.println("Version 2: " + longestUniqueSubstringV2("x"));
        System.out.println("Version 3: " + longestUniqueSubstringV3("x"));
        System.out.println();

        System.out.println("");
        System.out.println("Version 1: " + longestUniqueSubstringV1(""));
        System.out.println("Version 2: " + longestUniqueSubstringV2(""));
        System.out.println("Version 3: " + longestUniqueSubstringV3(""));
        System.out.println();
    }
}
