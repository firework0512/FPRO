package com.wei.fpro.practicas;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.wei.fpro.practicas.WordStacksV2.generateRandomBoolean;
import static com.wei.fpro.practicas.WordStacksV2.generateRandomNumber;

public class WordStacksV2 {

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
            "GUITARRA",
            "FEO",
            "FAX",
            "WIFI",
            "SAL",
            "MAR",
            "PAN",
    };

    private static final String[] CONSUMER_DICTIONARY_ENG = {
            "STRESSED",
            "DESSERTS",
    };


    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        String[] randomDictionary = new String[10];
        getRandomList(CONSUMER_DICTIONARY).toArray(randomDictionary);
        System.out.println(Arrays.toString(randomDictionary));

        GameMatrix gameMatrix = new GameMatrix(10, 10, randomDictionary);
        //gameMatrix.printMatrix();

        //gameMatrix.gravity();
        //gameMatrix.printMatrix();
        System.out.println();
        System.out.println();


        String operation = keyboard.nextLine();
        operation = operation.toUpperCase(Locale.ROOT);
        GameMatrix.TYPE type = null;
        int rowIndex = Integer.parseInt(String.valueOf(operation.charAt(0)));
        int columnIndex = Integer.parseInt(String.valueOf(operation.charAt(1)));
        switch (operation.charAt(2)) {
            case 'N':
                type = GameMatrix.TYPE.N;
                break;
            case 'S':
                type = GameMatrix.TYPE.S;
                break;
            case 'E':
                type = GameMatrix.TYPE.E;
                break;
            case 'O':
                type = GameMatrix.TYPE.O;
                break;
        }
        int lenght = Integer.parseInt(String.valueOf(operation.charAt(3)));
        gameMatrix.tryEliminateWord(rowIndex, columnIndex, type, lenght);
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

    /**
     * Method that returns a random number
     *
     * @param origin the start number inclusive
     * @param bound  the final number exclusive
     * @return the random number
     */
    public static int generateRandomNumber(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }


    public static boolean generateRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}

class GameMatrix {

    private final String[] dictionary;
    private List<String> insertedDictionary = new ArrayList<>();
    private List<String> eliminatedDictionary = new ArrayList<>();
    private final File dataFile = new File("C://Users//luna//Documents/data.txt");
    private int record = 0;
    private char[][] matrix;


    private GameMatrix() {
        throw new UnsupportedOperationException("Please call the right constructor");
    }

    public GameMatrix(int rowsSize, int columnsSize, String[] dictionary) {
        this.dictionary = dictionary;
        this.matrix = generateMatrix(rowsSize, columnsSize);
        getLastRecord();
        printMatrix();
        // this.matrix = NuevaMatriz.nuevaMatriz(10, 10, dictionary);
    }

    private void getLastRecord() {
        if (dataFile.exists())
            try {
                Scanner scanner = new Scanner(dataFile);
                scanner.useDelimiter(":");
                scanner.next();
                String recordString = scanner.next();
                this.record = Integer.parseInt(recordString.trim());
                System.out.println("record saved : " + this.record);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

    }

    private char[][] generateMatrix(int rowsSize, int columnsSize) {
        this.matrix = new char[rowsSize][columnsSize];

        int randomNumber;
        TYPE type = null;

        //First we fill the matrix with empty spaces
        fillMatrixWithEmptySpaces();

        //We parse every single word of the random dictionary
        for (String word : dictionary) {
            randomNumber = generateRandomNumber(0, 2);
            //We insert the word in the matrix randomly
            if (randomNumber == 0) {
                type = TYPE.ROW;
            } else if (randomNumber == 1) {
                type = TYPE.COLUMN;
            }
            this.matrix = insertInAMatrix(word, type);
        }
        gravity();
        return this.matrix;
    }

    private char[][] insertInAMatrix(String word, TYPE type) {
        boolean needReversed = generateRandomBoolean();

        //We reverse the word
        if (needReversed) {
            word = reverseString(word);
        }

        int splittedWordTotalLenght = 0;

        if (type == TYPE.ROW) {
            System.out.println("Insert row words : " + word);

            int count = 0;
            for (int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++) {
                boolean possibleInsertLetterToColumn = isPossibleInsertLetterToColumn(columnIndex, 1);
                if (possibleInsertLetterToColumn) {
                    count++;
                }
            }

            if (count >= splittedWordTotalLenght) {
                int columnIndex = 0;
                for (int wordsCharsCount = 0; wordsCharsCount < word.length(); wordsCharsCount++) {
                    boolean possibleInsertLetterToColumn = isPossibleInsertLetterToColumn(columnIndex, 1);
                    if (possibleInsertLetterToColumn) {
                        String columnWord = convertAColumnDatatoWord(columnIndex);
                        List<Integer> emptyPositionsList = getEmptySpacesPositionsFromAStringV2(columnWord)
                                .stream()
                                .sorted(Collections.reverseOrder())
                                .collect(Collectors.toList());
                        int randomRowPosition = emptyPositionsList.get(0);
                        this.matrix[randomRowPosition][columnIndex] = word.charAt(wordsCharsCount);
                    }
                    columnIndex++;
                }

                insertedDictionary.add(word);
            } else {
                //Call the same method but this time we know that it´s impossible to insert the word in a row
                //So, we insert the word in a column
                insertInAMatrix(word, TYPE.COLUMN);
            }

           /* if (splittedWordCount == 1) {
                String splittedWord = wordsArray[0];
                List<EmptyData> rowEmptySpacesOfRowsList = getEmptySpacesPositionsOfRowsInMatrix(splittedWord.length());


                EmptyData emptyData = rowEmptySpacesOfRowsList.get(generateRandomNumber(0, rowEmptySpacesOfRowsList.size()));
                int randomRowIndex = emptyData.getIndex();
                List<EmptyPosition> collect = emptyData.getEmptySpacesList()
                        .stream()
                        .filter(emptyPosition -> emptyPosition.getEnd() - emptyPosition.getStart() >= splittedWordCount)
                        .collect(Collectors.toList());


                EmptyPosition emptyPosition = collect.get(generateRandomNumber(0, collect.size()));

                int amountSpaceAvailable = emptyPosition.getEnd() - emptyPosition.getStart();
                int maxRandomNumber = amountSpaceAvailable - splittedWord.length();
                int startPositionToFill = generateRandomNumber(emptyPosition.getStart(), maxRandomNumber + 1);

                char[] wordLetters = splittedWord.toCharArray();
                for (int lettersIndex = 0; lettersIndex < wordLetters.length; lettersIndex++) {
                    char wordLetter = wordLetters[lettersIndex];
                    this.matrix[randomRowIndex][startPositionToFill] = wordLetter;
                    //System.out.println("inserted  : " + wordLetter + " in (" + randomRowIndex + "," + lettersIndex + ")");
                    startPositionToFill++;
                }

                if (needInverse) {
                    this.insertedDictionary.remove(reverseString(splittedWord));
                } else {
                    this.insertedDictionary.remove(splittedWord);
                }

                //System.out.println(this.insertedDictionary.toString());

            } else {

                int count = 0;
                for (int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++) {
                    boolean possibleInsertLetterToColumn = isPossibleInsertLetterToColumn(columnIndex);
                    if (possibleInsertLetterToColumn) {
                        count++;
                    }
                }

                if (count >= splittedWordTotalLenght) {
                    int columnIndex = 0;

                    for (int wordsCharsCount = 0; wordsCharsCount <= splittedWordTotalLenght; wordsCharsCount++) {
                        boolean possibleInsertLetterToColumn = isPossibleInsertLetterToColumn(columnIndex);
                        if (possibleInsertLetterToColumn) {
                            String columnWord = convertAColumnDatatoWord(columnIndex);
                            List<Integer> emptyPositionsList = getEmptySpacesPositionsFromAStringV2(columnWord)
                                    .stream()
                                    .sorted(Collections.reverseOrder())
                                    .collect(Collectors.toList());
                            int randomRowPosition = emptyPositionsList.get(0);
                            this.matrix[randomRowPosition][columnIndex] = word.charAt(wordsCharsCount);
                        }
                        columnIndex++;
                    }
                }
            }*/

        } else if (type == TYPE.COLUMN) {
            System.out.println("Insert column words : " + word);
            int count = 0;
            for (int rowIndex = 0; rowIndex < this.matrix[0].length; rowIndex++) {
                boolean possibleInsertLetterToColumn = isPossibleInsertLetterToRow(rowIndex, 1);
                if (possibleInsertLetterToColumn) {
                    count++;
                }
            }
            if (count >= splittedWordTotalLenght) {
                int rowIndex = 0;
                for (int wordsCharsCount = 0; wordsCharsCount < word.length(); wordsCharsCount++) {
                    boolean possibleInsertLetterToColumn = isPossibleInsertLetterToRow(rowIndex, 1);
                    if (possibleInsertLetterToColumn) {
                        String columnWord = convertARowDatatoWord(rowIndex);
                        List<Integer> emptyPositionsList = getEmptySpacesPositionsFromAStringV2(columnWord)
                                .stream()
                                .sorted()
                                .collect(Collectors.toList());
                        int randomRowPosition = emptyPositionsList.get(0);
                        this.matrix[rowIndex][randomRowPosition] = word.charAt(wordsCharsCount);
                    }
                    rowIndex++;
                }
            } else {
                //Call the same method but this time we know that it´s impossible to insert the word in a column
                //So, we insert the word in a row
                insertInAMatrix(word, TYPE.ROW);
            }
        }


        return this.matrix;
    }


    private void gravity() {
        for (int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++) {
            String columnString = convertAColumnDatatoWord(columnIndex);
            List<Integer> emptySpacesPositionsList = getEmptySpacesPositionsFromAStringV2(columnString);
            if (!emptySpacesPositionsList.isEmpty()) {
                //System.out.println("Gravity start, columnWord : " + columnString);
                String columnWordWithoutSpaces = columnString.replace(" ", "");
                //System.out.println("Gravity start, columnWord without spaces : " + columnStringWithEmptySpacesRemoved);
                int shouldLetterStartIndex = (this.matrix[0].length - columnWordWithoutSpaces.length());
                //System.out.println("Gravity start, should start position : " + shouldLetterStartPosition);
                insertColumnWordFromPosition(0, columnIndex, generateEmptySpacesString(shouldLetterStartIndex));
                insertColumnWordFromPosition(shouldLetterStartIndex, columnIndex, columnWordWithoutSpaces);
            }
        }
    }

    private void translation() {
        List<Integer> columnEmptysList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++) {
            String columnWord = convertAColumnDatatoWord(columnIndex);
            int spaces = getNumberOfSpacesOfAString(columnWord);
            if (spaces == this.matrix[0].length) {
                columnEmptysList.add(columnIndex);
            }
        }
        if (!columnEmptysList.isEmpty()) {
            int shouldEndColumnIndex = this.matrix[0].length - columnEmptysList.size();

            for (int columnIndex = 0; columnIndex <= shouldEndColumnIndex; columnIndex++) {
                if (!columnEmptysList.contains(columnIndex)) {

                }
            }
        }
    }


    public void tryEliminateWord(int startRowIndex, int startColumnIndex, TYPE type, int lenght) {
        String dataWord = "";
        int startIndex = 0;
        int finalIndex = 0;
        switch (type) {
            case N:
                dataWord = convertAColumnDatatoWord(startColumnIndex);
                startIndex = (startRowIndex - lenght) + 1;
                finalIndex = startRowIndex + 1;
                break;
            case S:
                dataWord = convertAColumnDatatoWord(startColumnIndex);
                startIndex = startRowIndex;
                finalIndex = startRowIndex + lenght;
                break;
            case E:
                dataWord = convertARowDatatoWord(startRowIndex);
                startIndex = startColumnIndex;
                finalIndex = startColumnIndex + lenght;
                break;
            case O:
                dataWord = convertARowDatatoWord(startRowIndex);
                startIndex = startColumnIndex - lenght + 1;
                finalIndex = startColumnIndex + 1;
                break;
        }
        //System.out.println("startIndex " + startIndex);
        //System.out.println("finalIndex " + finalIndex);
        String selectedWord = dataWord.substring(startIndex, finalIndex);
        System.out.println("selected word : " + selectedWord);
        String spacesString = generateEmptySpacesString(selectedWord.length());
        List<String> dictionary = new ArrayList<>(Arrays.asList(this.dictionary));
        if (this.insertedDictionary.contains(selectedWord)) {
            this.eliminatedDictionary.add(selectedWord);
            switch (type) {
                case N:
                case S:
                    insertColumnWordFromPosition(startIndex, startColumnIndex, spacesString);
                    break;
                case E:
                case O:
                    insertRowWordFromPosition(startRowIndex, startIndex, spacesString);
                    break;
            }
        } else if (dictionary.contains(selectedWord) || dictionary.contains(reverseString(selectedWord))) {

        } else {
            //Another try to get an information
            getInformation();
        }
        gravity();
        updateRecord(selectedWord.length());
        printMatrix();
    }

    private void updateRecord(int lenght) {
        record += lenght;
        Writer wr = null;
        try {
            wr = new FileWriter(dataFile);
            wr.write("LastRecord : " + record);
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        getInformation();
    }

    //TODO
    private void getInformation() {
        printMatrix();
    }


    private void insertRowWordFromPosition(int rowIndex, int startColumnIndex, String word) {
        for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            this.matrix[rowIndex][startColumnIndex] = word.charAt(letterIndex);
            startColumnIndex++;
        }
    }

    private void insertColumnWordFromPosition(int startRowIndex, int columnIndex, String word) {
        for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            this.matrix[startRowIndex][columnIndex] = word.charAt(letterIndex);
            startRowIndex++;
        }
    }


    private boolean isPossibleInsertLetterToRow(int rowIndex, int spacesNeeded) {
        boolean result = false;
        String rowWord = convertARowDatatoWord(rowIndex);
        if (getNumberOfSpacesOfAString(rowWord) >= spacesNeeded) {
            result = true;
        }
        return result;
    }

    private boolean isPossibleInsertLetterToColumn(int columnIndex, int spacesNeeded) {
        boolean result = false;
        String columnWord = convertAColumnDatatoWord(columnIndex);
        if (getNumberOfSpacesOfAString(columnWord) >= spacesNeeded) {
            result = true;
        }
        return result;
    }

    private EmptyData isPossibleInsertRowWordFromColumn(int rowIndex, int columnIndex, int lenght) {
        EmptyData whiteSpacesPosition = null;

        StringBuilder rowWord = new StringBuilder();
        for (int cIndex = columnIndex; cIndex <= this.matrix[0].length - 1; cIndex++) {
            rowWord.append(this.matrix[rowIndex][cIndex]);
        }
        //System.out.println(rowWord);
        if (getNumberOfSpacesOfAString(rowWord.toString()) >= lenght) {
            whiteSpacesPosition = new EmptyData(
                    rowIndex,
                    getEmptySpacesPositionsFromAString(rowWord.toString())
                            .stream()
                            .filter(emptyPosition -> emptyPosition.getEnd() - emptyPosition.getStart() >= lenght)
                            .collect(Collectors.toList()));
        }
        return whiteSpacesPosition;
    }

    private String generatePossibleInsertedWord(String word) {
        assert (word.length() >= 2);
        StringBuilder wordStringBuilder = new StringBuilder(word);

        //int lettresCanBeInserted = word.length() - 1;
        // int maxInsertionsPossible = 10 - lettresCanBeInserted;
        int maxInsertionsPossible = word.length();
        int randomInserts = generateRandomNumber(0, maxInsertionsPossible);
        int insertCount = 0;
        while (insertCount <= randomInserts) {
            int randomInsertPosition = generateRandomNumber(0, word.length());
            wordStringBuilder.insert(randomInsertPosition, " ");
            insertCount++;
        }
        return wordStringBuilder.toString();
    }


    public List<EmptyData> getEmptySpacesPositionsOfColumnsInMatrix(int spacesNeeded) {

        List<EmptyData> whiteSpacesPositionList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex <= this.matrix[0].length - 1; columnIndex++) {
            String columWord = convertAColumnDatatoWord(columnIndex);
            // System.out.println("rowWord : " + rowWord);
            //Condition : we have the sufficient spaces for the word in a [rowWord]
            if (getNumberOfSpacesOfAString(columWord) >= spacesNeeded) {
                whiteSpacesPositionList.add(new EmptyData(columnIndex, getEmptySpacesPositionsFromAString(columWord)));
            }
        }
        return whiteSpacesPositionList;
    }

    public List<EmptyData> getEmptySpacesPositionsOfRowsInMatrix(int spacesNeeded) {

        List<EmptyData> whiteSpacesPositionList = new ArrayList<>();
        for (int rowIndex = 0; rowIndex <= this.matrix.length - 1; rowIndex++) {
            String rowWord = convertARowDatatoWord(rowIndex);
            // System.out.println("rowWord : " + rowWord);
            //Condition : we have the sufficient spaces for the word in a [rowWord]
            if (getNumberOfSpacesOfAString(rowWord) >= spacesNeeded) {
                whiteSpacesPositionList.add(new EmptyData(rowIndex, getEmptySpacesPositionsFromAString(rowWord)));
            }
        }
        return whiteSpacesPositionList;
    }


    private List<Integer> getEmptySpacesPositionsFromAStringV2(String word) {
        List<Integer> emptySpacesPositionList = new ArrayList<>();
        int emptySpaceIndex = word.indexOf(" ");
        int lastIndex = emptySpaceIndex;

        if (emptySpaceIndex != -1) {
            while ((emptySpaceIndex = word.indexOf(" ", lastIndex)) != -1) {
                lastIndex = emptySpaceIndex + 1;
                emptySpacesPositionList.add(emptySpaceIndex);
            }
        }
        return emptySpacesPositionList;
    }

    public List<EmptyPosition> getEmptySpacesPositionsFromAString(String word) {
        List<Integer> consecutiveNumbersList = new ArrayList<>();

        int emptySpaceIndex = word.indexOf(" ");
        int cachedIndex = emptySpaceIndex;

        if (emptySpaceIndex != -1) {
            while ((emptySpaceIndex = word.indexOf(" ", cachedIndex)) != -1) {
                cachedIndex = emptySpaceIndex + 1;
                consecutiveNumbersList.add(emptySpaceIndex);
            }
        }
        return getConsecutiveNumbers(consecutiveNumbersList.toArray(new Integer[0]));
    }

    private List<EmptyPosition> getConsecutiveNumbers(Integer[] dataArray) {
        List<EmptyPosition> emptySpacesRangeList = new ArrayList<>();
        //StringBuilder sb = new StringBuilder();
        int max = 0;
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i] > max)
                max = dataArray[i];
        }
        int[] t = new int[max + 1];
        for (int i = 0; i < dataArray.length; i++) {
            t[dataArray[i]] = 1;
        }
        int start = 0;
        int len = 0;
        for (int i = 0; i < t.length; i++) {
            if (t[i] != 0) {
                if (len == 0)
                    start = i;
                len++;
            } else {
                if (len == 1) {
                    //sb.append(start).append(',');
                    emptySpacesRangeList.add(new EmptyPosition(start, start));
                } else if (len > 1) {
                    //sb.append(start).append('-').append(start + len - 1).append(',');
                    emptySpacesRangeList.add(new EmptyPosition(start, start + len - 1));
                }
                len = 0;
            }
        }
        if (len == 1) {
            //sb.append(start).append(',');
            emptySpacesRangeList.add(new EmptyPosition(start, start));
        } else if (len > 1) {
            //sb.append(start).append('-').append(start + len - 1).append(',');
            emptySpacesRangeList.add(new EmptyPosition(start, start + len - 1));
        }
        //sb.deleteCharAt(sb.length() - 1);
        //System.out.print(sb);
        /*for (Pair<Integer, Integer> rangePair :
                emptySpacesRangeList) {
            System.out.println("Pair : (" + rangePair.getKey() + "," + rangePair.getValue() + ")");
        }*/
        return emptySpacesRangeList;
    }

    public int getNumberOfSpacesOfAString(String data) {
        int whiteSpacesCount = 0;
        for (char lettre :
                data.toCharArray()) {
            if (Character.isWhitespace(lettre)) whiteSpacesCount++;

        }
        return whiteSpacesCount;
    }

    private String convertAColumnDatatoWord(int columIndex) {
        StringBuilder columnWord = new StringBuilder();

        for (int rowIndex = 0; rowIndex < this.matrix.length; rowIndex++) {
            char letter = this.matrix[rowIndex][columIndex];
            columnWord.append(letter);

        }
        return columnWord.toString();
    }

    /**
     * Method that converts a row of @code this.matrix to String
     *
     * @param rowIndex the row index given
     * @return the entire row in String
     */
    private String convertARowDatatoWord(int rowIndex) {
        StringBuilder rowWord = new StringBuilder();
        for (char letter :
                this.matrix[rowIndex]) {
            rowWord.append(letter);
        }
        return rowWord.toString();
    }

    /**
     * Method that fills the matrix with empty spaces
     */
    private void fillMatrixWithEmptySpaces() {
        for (int rowIndex = 0; rowIndex < this.matrix.length; rowIndex++) {
            for (int columIndex = 0; columIndex < this.matrix[0].length; columIndex++) {
                this.matrix[rowIndex][columIndex] = ' ';
            }
        }
    }

    /**
     * Method that prints the game matrix with coordinates
     */
    public void printMatrix() {
        if (this.matrix != null) {
            System.out.println();
            System.out.println("Record : " + record);
            System.out.println();

            char[][] clonedMatrix = getMatrixWithCoordinates();

            for (char[] chars : clonedMatrix) {
                for (char aChar : chars) {
                    System.out.print(aChar + " ");
                }

                System.out.println();
            }
        } else {
            generateMatrix(10, 10);
            printMatrix();
        }
    }

    /**
     * Method that creates a coordinated matrix based of the one given
     *
     * @return this.matrix with coordinates
     */
    private char[][] getMatrixWithCoordinates() {
        int rowCount = 0;
        int columCount = 0;
        char[][] coordinatedMatrix = new char[this.matrix.length + 2][this.matrix[0].length + 2];

        //System.out.println("Rows : " + coordinatedMatrix.length + " Colums : " + coordinatedMatrix[0].length);

        for (int rowIndex = 1; rowIndex <= this.matrix.length; rowIndex++) {
            char numberInAscci = (char) (rowCount + '0');
            char columnCountInAscci = (char) (columCount + '0');

            coordinatedMatrix[rowIndex][0] = numberInAscci;
            coordinatedMatrix[rowIndex][coordinatedMatrix.length - 1] = numberInAscci;

            coordinatedMatrix[0][rowIndex] = columnCountInAscci;
            coordinatedMatrix[coordinatedMatrix.length - 1][rowIndex] = columnCountInAscci;

            for (int columnIndex = 1; columnIndex <= this.matrix[0].length; columnIndex++) {
                coordinatedMatrix[rowIndex][columnIndex] = this.matrix[rowIndex - 1][columnIndex - 1];
            }
            rowCount++;
            columCount++;
        }
        return coordinatedMatrix;
    }

    /**
     * Method that reverses the string
     *
     * @param word the string provided to be reversed
     * @return word but reversed
     */
    private String reverseString(String word) {
        StringBuilder reversedString = new StringBuilder(word);
        reversedString.reverse();
        return reversedString.toString();
    }

    private String generateEmptySpacesString(int lenght) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int startIndex = 0; startIndex < lenght; startIndex++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }


    public enum TYPE {
        ROW,
        COLUMN,
        N,
        S,
        E,
        O,
    }
}


class EmptyPosition {
    private int start, end;

    EmptyPosition(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}

//DATA CLASS
class EmptyData {

    private int index;
    private List<EmptyPosition> emptySpacesList;

    public EmptyData(int index, List<EmptyPosition> emptySpacesList) {
        this.index = index;
        this.emptySpacesList = emptySpacesList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<EmptyPosition> getEmptySpacesList() {
        return emptySpacesList;
    }

    public void setEmptySpacesList(List<EmptyPosition> emptySpacesList) {
        this.emptySpacesList = emptySpacesList;
    }

}
