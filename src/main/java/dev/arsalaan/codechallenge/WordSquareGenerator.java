package dev.arsalaan.codechallenge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WordSquareGenerator {

    public static void main(String[] args) throws IOException {

        // App Info & User Inputs
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("APP INFO");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("This application will generate a valid Word Square given:\n" + "- the size of the Word Square grid (n)\n" +
                "- a pool of characters to generate valid words from\n");

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("DISCLAIMER");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("The length of the pool of characters entered MUST equal the size of the Word Square grid squared (n^2)");
        System.out.println("For example, when prompted, entering: 4 aaccdeeeemmnnnoo");
        System.out.println("is valid because a grid size of 4 requires 16 characters in the pool!\n");

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("USER INPUT");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Please enter grid size (n), followed by a space, followed by the pool of characters: ");

        Scanner scanner = new Scanner(System.in);
        int gridSize = scanner.nextInt();
        String characterPool = scanner.next();

        if (characterPool.length() != gridSize*gridSize) {
            throw new IllegalStateException("The number of characters you have entered in the pool is NOT equal to the grid size squared!");
        }


        /*--------------- STEP 1: Filter dictionary.txt list of words by word length equal to the grid size ---------------*/


        List<String> listOfWords = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/dictionary.txt"));
        String currentWord;

        while ((currentWord = reader.readLine()) != null) {
            listOfWords.add(currentWord);
        }

        reader.close();

        List<String> liftOfWordsFiltered = listOfWords.stream().filter(word -> word.length() == gridSize)
                                            .collect(Collectors.toList());


        /*--------------- STEP 2: Filter list of words again, but by words that can be constructed from the pool of characters given ---------------*/


        Map<Character, Integer> poolCharCountMap = getCharCountMap(characterPool);
        List<String> validWordSquareWords = new ArrayList<>();

        for (String word : liftOfWordsFiltered) {

            Map<Character, Integer> currentWordCharCountMap = getCharCountMap(word);

            boolean canMakeCurrentWord = true;
            for (char character : currentWordCharCountMap.keySet()) {

                int currentWordCharCount = currentWordCharCountMap.get(character);
                int poolCharCount = poolCharCountMap.getOrDefault(character, 0);

                if (currentWordCharCount > poolCharCount) {
                    canMakeCurrentWord = false;
                    break;
                }
            }

            if (canMakeCurrentWord) {
                validWordSquareWords.add(word);
            }
        }


        /*--------------- STEP 3 - Word Square Algorithm From List of Feasible Words! ---------------*/


        getPrefixesMap(validWordSquareWords);
        List<List<String>> result = new ArrayList<>();

        for (String validWordSquareWord : validWordSquareWords) {

            LinkedList<String> list = new LinkedList<>();
            list.add(validWordSquareWord);
            backTrack(1, list, result, validWordSquareWord.length());
        }

        List<String> randomWordSquareSolution = result.get(new Random().nextInt(result.size()));
        System.out.println("\n" + "A possible Word Square solution for this is: " + "\n");
        randomWordSquareSolution.forEach(System.out::println);
    }


    /*------------------------------ METHODS ------------------------------*/


    private static final HashMap<String, List<String>> map = new HashMap<>();


    private static void getPrefixesMap(List<String> words) {

        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {

                String prefix = word.substring(0, i);
                map.putIfAbsent(prefix, new ArrayList<>());
                List<String> list = map.get(prefix);
                list.add(word);
            }
        }
    }


    private static void backTrack(int step, LinkedList<String> list, List<List<String>> result, int n) {

        if (list.size() == n) {
            result.add(new ArrayList<>(list));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String word : list) {
            sb.append(word.charAt(step));
        }

        String prefix = sb.toString();
        List<String> wordList = map.getOrDefault(prefix, new ArrayList<>());

        for (String word : wordList){
            list.add(word);
            backTrack(step+1, list, result, n);
            list.removeLast();
        }
    }


    private static Map<Character, Integer> getCharCountMap(String characterPool) {

        Map<Character, Integer> countCharsMap = new HashMap<>();

            for (int i = 0; i < characterPool.length(); i++) {

                char currentChar = characterPool.charAt(i);
                int count = countCharsMap.getOrDefault(currentChar, 0);
                countCharsMap.put(currentChar, count + 1);
            }
            return countCharsMap;
    }

}
