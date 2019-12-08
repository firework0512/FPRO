package com.wei.fpro.practicas;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WordStacks {

    private static final String[] TEST_DICTIONARY = {
            "AAA",
            "AAAAAA",
            "BBBBBBBBB",
            "CCCCC",
            "DDDDD",
            "EEEEE",
            "FFFFF",
            "GGGGG",
            "HHHHH",
            "IIIII",
            "JJJJJ"};

    private static final String[] CONSUMER_DICTIONARY = {
            "MANEJAR",
            "ONDA",
            "IMPORTAR",
            "SEGUNDOS",
            "CARRERAS",
            "BARRER",
            "FREGAR",
            "HOJA",
            "HUIR",
            "CARAMELO",
            "CAFETERA",
            "LADRILLO",
            "AZULEJO",
            "COMENTAR",
            "BIKINI",
            "VACA",
            "SENTENCIA",
            "DUPLICADO",
            "DICIEMBRE",
            "LARINGE",
            "NEGATIVO",
            "GUITARRA"
    };

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        String[] randomDictionary = new String[10];
        getRandomList(CONSUMER_DICTIONARY).toArray(randomDictionary);
        System.out.println(Arrays.toString(randomDictionary));

        char[][] matriz = NuevaMatriz.nuevaMatriz(10, 10, randomDictionary);
        printMatrix(matriz);
        System.out.println();

    }

    /**
     * @param originalDictionary
     * @return
     */
    private static List<String> getRandomList(String[] originalDictionary) {
        List<String> randomDictionary = new ArrayList<>();

        int index = 0;
        while (index < 10) {
            String palabra = originalDictionary[generateRandomNumber(0, originalDictionary.length - 1)];
            if (!randomDictionary.contains(palabra)) {
                randomDictionary.add(palabra);
                index++;
            }
        }
        return randomDictionary;
    }


    private static char[][] generateMatrix(int rowsSize, int columnsSize, String[] dictionary) {
        char[][] matrix = new char[rowsSize][columnsSize];
        int randomNumber;

        //First we fill the matrix in

        //We parse every single word of the dictionary
        for (String word : dictionary) {
            TYPE type = null;
            randomNumber = generateRandomNumber(0, 1);
            if (randomNumber == 0) {
                type = TYPE.ROW;
            } else if (randomNumber == 1) {
                type = TYPE.COLUMN;
            }
            matrix = insertInAMatrix(word, type, matrix);
        }
        return matrix;
    }

    private static char[][] insertInAMatrix(String word, TYPE type, char[][] matrix) {
        int needInverse = generateRandomNumber(0, 1);
        int needInsert = generateRandomNumber(0, 1);
        //We reverse the word
        if (needInverse == 1) {
            word = reverseString(word);
        }
        if (type == TYPE.ROW) {
            //We get all of empty spaces positions
            List<Pair<Integer, Integer>> rowEmptySpacesOfARow = getEmptySpacesOfARowInAMatrix(word.length(), matrix);
            //We choose only one of them
            Pair<Integer, Integer> emptySpacePosition = rowEmptySpacesOfARow.get(generateRandomNumber(0, rowEmptySpacesOfARow.size()));
            if (needInsert == 1 && word.length() <= matrix[0].length) {
                int wordRandomPosition = generateRandomNumber(0, word.length());
            } else {

            }
        } else if (type == TYPE.COLUMN) {

        }
        return matrix;
    }


    private static List<Pair<Integer, Integer>> getEmptySpacesOfARowInAMatrix(int spacesNeeded, char[][] matrix) {

        List<Pair<Integer, Integer>> whiteSpacesPositionList = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {

            String rowWord = convertARowDatatoWord(rowIndex, matrix);

            //Condition : we have empty spaces in the middle
            if (getNumberOfSpacesofAString(rowWord) >= spacesNeeded) {
                whiteSpacesPositionList.add(new Pair<>(rowWord.indexOf(" "), rowWord.lastIndexOf(" ")));
                //Condition : we dont have the sufficient spaces for the word in a [rowWord]
            } else if (matrix[rowIndex].length - rowWord.length() >= spacesNeeded) {
                whiteSpacesPositionList.add(new Pair<>(rowWord.length(), rowWord.length() + spacesNeeded));
            }
        }
        return whiteSpacesPositionList;
    }

    private static int getNumberOfSpacesofAString(String data) {
        int whiteSpacesCount = 0;

        for (char z : data.toCharArray()) {
            if (Character.isWhitespace(z)) {
                whiteSpacesCount++;
            }
        }
        return whiteSpacesCount;
    }

    private static String convertARowDatatoWord(int rowIndex, char[][] matrix) {
        StringBuilder rowWord = new StringBuilder();
        for (char letter :
                matrix[rowIndex]) {
            rowWord.append(letter);
        }
        return rowWord.toString();
    }

    private static String convertAColumDatatoWord(int columIndex, char[][] matrix) {
        StringBuilder columnWord = new StringBuilder();
        for (int rowIndex = 0; rowIndex <= (matrix[0].length - 1); rowIndex++) {
            columnWord.append(matrix[rowIndex][columIndex]);
        }
        return columnWord.toString();
    }

    private static void printMatrix(char[][] matrix) {
        for (char[] chars : matrix) {
            for (char aChar : chars) {
                System.out.print(aChar + " ");
            }

            System.out.println();
        }

        System.out.println();
        System.out.println();
        System.out.println();

        char[][] clonedMatrix = getMatrixWithCoordinates(matrix);

        for (char[] chars : clonedMatrix) {
            for (char aChar : chars) {
                System.out.print(aChar + " ");
            }

            System.out.println();
        }
    }


    private static char[][] getMatrixWithCoordinates(char[][] matrix){
        int rowCount = 0;
        int columCount = 0;
        char[][] coordinatedMatrix = new char[matrix.length+2][matrix[0].length+2];
        System.out.println("Rows : " + coordinatedMatrix.length + " Colums : " + coordinatedMatrix[0].length);
        for (int rowIndex = 1; rowIndex <= matrix.length; rowIndex++) {
            char numberInAscci = (char)(rowCount+'0');
            char columnCountInAscci = (char)(columCount+'0');

            coordinatedMatrix[rowIndex][0] = numberInAscci;
            coordinatedMatrix[rowIndex][coordinatedMatrix.length-1] = numberInAscci;

            coordinatedMatrix[0][rowIndex] = columnCountInAscci;
            coordinatedMatrix[coordinatedMatrix.length-1][rowIndex] = columnCountInAscci;

            for (int columnIndex = 1; columnIndex <= matrix[0].length; columnIndex++) {
                coordinatedMatrix[rowIndex][columnIndex] = matrix[rowIndex-1][columnIndex-1];
            }
            rowCount++;
            columCount++;
        }
        return coordinatedMatrix;
    }
    private static String reverseString(String word) {
        StringBuilder reversedString = new StringBuilder(word);
        reversedString.reverse();
        return reversedString.toString();
    }

    /**
     * Method that returns a random number
     *
     * @param origin   the start number inclusive
     * @param finalNum the final number exclusive
     * @return the random number
     */
    private static int generateRandomNumber(int origin, int finalNum) {
        return ThreadLocalRandom.current().nextInt(origin, finalNum);
    }

    public enum TYPE {
        ROW,
        COLUMN,
    }
}
