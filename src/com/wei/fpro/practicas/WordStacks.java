package com.wei.fpro.practicas;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WordStacks {

    public static void main(String[] args) {
        System.out.println("¿Desea empezar el juego en modo de pruebas?");
        System.out.println("Si es el caso pulse p o P, en otro caso pulse otra tecla.");
        Scanner keyboard = new Scanner(System.in);
        String response = keyboard.nextLine();
        String[] dictionary = getDictionary(response);

        int lenght = 10;
        //Start to play
        play(lenght, dictionary, keyboard);
    }

    private static String[] getDictionary(String response) {
        final String[] testDictionary = {
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

        final String[] consumerDictionary = {
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
        return response.toLowerCase().equals("p") ? testDictionary : consumerDictionary;
    }

    /**
     * Method that starts the game
     *
     * @param length             the lenght of the random words we want to have
     * @param originalDictionary the original dictionary which we are going to choose @param lenght words
     * @param keyboard           the {@link Scanner} object we have created
     */
    private static void play(int length, String[] originalDictionary, Scanner keyboard) {

        //The random words dictionary
        String[] randomDictionary = originalDictionary;
        //List of random words
        List<String> randomWordsList;

        if (originalDictionary.length > length) {
            randomWordsList = RandomUtils.getRandomList(originalDictionary, length);
            //Put random words to the random words array
            randomDictionary = randomWordsList.toArray(new String[0]);

        } else {
            randomWordsList = new ArrayList<>(Arrays.asList(randomDictionary));
        }

        //List containing words that user hasn`t removed yet
        List<String> restWordsList = new ArrayList<>(randomWordsList);

        //list of words the user has found that belongs to the original dictionary but not to the dictionary that builds the matrix

        System.out.println(Arrays.toString(randomDictionary));

        //The file which we save locally the user record
        final File dataFile = new File("data.txt");

        //Create [GameMatrix] instance
        GameMatrix gameMatrix = new GameMatrix();

        //Now we start the game generating the matrix
        char[][] matrix = gameMatrix.startGame(10, 10, randomDictionary, keyboard);

        //Get the last record
        int record = gameMatrix.getLastRecord(dataFile);

        //Check if the matrix has readable words
        while (gameMatrix.hasReadableWords(matrix, randomWordsList)) {
            gameMatrix.printMatrix(matrix, record);
            //We request user to input his operation
            Object[] operationData = gameMatrix.requestOperation(keyboard);

            if (operationData.length == 1) {
                String typeClue = (String) operationData[0];
                //TODO
                switch (typeClue) {
                    case "LET":
                        break;
                    case "POS":
                        break;
                    case "PAL":
                        break;
                }
            } else if (operationData.length == 4) {
                int rowIndex = (int) operationData[0];
                int columnIndex = (int) operationData[1];
                GameMatrix.TYPE type = (GameMatrix.TYPE) operationData[2];
                int lenght = (int) operationData[3];

                String selectedWord = (String) gameMatrix.getOperationDataArray(matrix, rowIndex, columnIndex, type, lenght)[1];
                //Check if we can perform an operation
                //Check if we have this word int the original dictionary
                if (gameMatrix.containsInDictionary(originalDictionary, selectedWord) && !gameMatrix.containsInDictionary(randomDictionary, selectedWord)) {
                    if (!restWordsList.contains(selectedWord)) {
                        record++;
                        restWordsList.add(selectedWord);
                        gameMatrix.writeRecord(dataFile, record);
                    } else {
                        System.out.println("Oye, que ya lo has encontrado");
                        System.out.println("No seas avaricioso");
                    }
                    //Check if we have this word int the generated dictionary
                } else if (gameMatrix.containsInDictionary(randomDictionary, selectedWord)) {
                    record += selectedWord.length();
                    gameMatrix.writeRecord(dataFile, record);
                    restWordsList.remove(selectedWord);
                    matrix = gameMatrix.doOperation(matrix, rowIndex, columnIndex, type, lenght);
                }
            }
        }

        if (gameMatrix.isEmptyMatrix(matrix)) {
            System.out.println();
            System.out.println("Has conseguido vaciar el tablero!!!");
            System.out.println("¿Quieres volver a jugar? Si es el caso, introduzca si");
            String response = keyboard.nextLine();
            if (response.toLowerCase().equals("si")) {
                play(length, originalDictionary, keyboard);
            }
        } else {
            //Matrix not empty
            //We regenerate a new matrix with given dictionary
            System.out.println();
            System.out.println("No hemos podido encontrar palabras legibles en el tablero");
            System.out.println("Generaremos otra nueva matriz");
            String[] restWordArray = restWordsList.toArray(new String[0]);
            play(restWordArray.length, restWordArray, keyboard);
        }
    }

}

class GameMatrix {

    public GameMatrix() {
        //DO NOTHING
    }

    public char[][] startGame(int rowsSize, int columnsSize, String[] dictionary, Scanner scanner) {
        char[][] matrix = generateMatrix(rowsSize, columnsSize, dictionary);
        gravity(matrix);
        return matrix;
    }


    /**
     * Method that returns the last user record saved
     *
     * @param file the record {@link File} object we want to read into
     * @return last user record saved
     */
    public int getLastRecord(File file) {
        return FileUtils.getRecordInFile(file);
    }

    /**
     * Method that writes the record to a file
     *
     * @param file   file the record {@link File} object we want to write into
     * @param record the record we want to write
     */
    public void writeRecord(File file, int record) {
        FileUtils.writeRecordInFile(file, record);
    }

    /**
     * Method that generates the game matrix
     *
     * @param rowsSize    the number of rows the matrix has
     * @param columnsSize the number of columns the matrix has
     * @param dictionary  the dictionary provided to generate the matrix
     * @return the matrix
     */
    private char[][] generateMatrix(int rowsSize, int columnsSize, String[] dictionary) {
        char[][] matrix = new char[rowsSize][columnsSize];

        int randomNumber;
        TYPE type = null;

        //First we fill the matrix with empty spaces
        fillMatrixWithEmptySpaces(matrix);

        //We parse every single word of the random dictionary
        for (String word : dictionary) {
            randomNumber = RandomUtils.generateRandomNumber(0, 2);
            //We insert the word in the matrix randomly
            if (randomNumber == 0) {
                type = TYPE.ROW;
            } else if (randomNumber == 1) {
                type = TYPE.COLUMN;
            }
            matrix = insertInAMatrix(matrix, word, type);
        }
        return matrix;
    }

    private char[][] insertInAMatrix(char[][] matrix, String word, TYPE type) {
        boolean needReversed = RandomUtils.generateRandomBoolean();

        //We reverse the word
        if (needReversed) {
            word = reverseString(word);
        }

        if (type == TYPE.ROW) {
            //System.out.println("Insert row words : " + word);
            int count = 0;
            for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
                boolean possibleInsertLetterToColumn = isPossibleInsertWordToColumn(matrix, columnIndex, 1);
                if (possibleInsertLetterToColumn) {
                    count++;
                }
            }

            if (count >= word.length()) {
                int columnIndex = 0;
                int wordsCharsCount = 0;
                while (wordsCharsCount < word.length()) {
                    boolean possibleInsertLetterToColumn = isPossibleInsertWordToColumn(matrix, columnIndex, 1);
                    if (possibleInsertLetterToColumn) {
                        String columnWord = convertAColumnDatatoWord(matrix, columnIndex);
                        List<Integer> emptyPositionsList = getEmptySpacesPositionsFromAString(columnWord)
                                .stream()
                                .sorted(Collections.reverseOrder())
                                .collect(Collectors.toList());
                        int randomRowPosition = emptyPositionsList.get(0);
                        matrix[randomRowPosition][columnIndex] = word.charAt(wordsCharsCount);
                        //System.out.println("Inserted : " + word.charAt(wordsCharsCount) + " at (" + randomRowPosition + "," + columnIndex + ")");
                        wordsCharsCount++;
                    }
                    columnIndex++;
                }
            } else {
                //Call the same method but this time we know that it´s impossible to insert the word in a row
                //So, we insert the word in a column
                insertInAMatrix(matrix, word, TYPE.COLUMN);
            }

        } else if (type == TYPE.COLUMN) {
            //System.out.println("Insert column words : " + word);
            //int count = 0;
            boolean isPossibleToInsert = false;
            int columnIndex = 0;
            for (int index = columnIndex; index < matrix[0].length; index++) {
                boolean possibleInsertLetterToColumn = isPossibleInsertWordToColumn(matrix, index, word.length());
                if (possibleInsertLetterToColumn) {
                    //count++;
                    isPossibleToInsert = true;
                    columnIndex = index;
                    index = matrix[0].length - 1;
                }
            }
            if (isPossibleToInsert) {
                int wordsCharsCount = 0;
                while (wordsCharsCount < word.length()) {
                    String columnWord = convertAColumnDatatoWord(matrix, columnIndex);
                    List<Integer> emptyPositionsList = getEmptySpacesPositionsFromAString(columnWord)
                            .stream()
                            .sorted()
                            .collect(Collectors.toList());
                    int firstPosition = emptyPositionsList.get(0);
                    matrix[firstPosition][columnIndex] = word.charAt(wordsCharsCount);
                    //System.out.println("Inserted : " + word.charAt(wordsCharsCount) + " at (" + firstPosition + "," + columnIndex + ")");
                    wordsCharsCount++;
                }
            } else {
                //Call the same method but this time we know that it´s impossible to insert the word in a column
                //So, we insert the word in a row
                insertInAMatrix(matrix, word, TYPE.ROW);
            }
        }

        return matrix;
    }


    private char[][] gravity(char[][] matrix) {
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            String columnString = convertAColumnDatatoWord(matrix, columnIndex);
            List<Integer> emptySpacesPositionsList = getEmptySpacesPositionsFromAString(columnString);
            if (!emptySpacesPositionsList.isEmpty()) {
                //System.out.println("Gravity start, columnWord : " + columnString);
                String columnWordWithoutSpaces = columnString.replace(" ", "");
                //System.out.println("Gravity start, columnWord without spaces : " + columnStringWithEmptySpacesRemoved);
                int shouldLetterStartIndex = (matrix[0].length - columnWordWithoutSpaces.length());
                //System.out.println("Gravity start, should start position : " + shouldLetterStartPosition);
                insertColumnWordFromPosition(matrix, 0, columnIndex, generateEmptySpacesString(shouldLetterStartIndex));
                insertColumnWordFromPosition(matrix, shouldLetterStartIndex, columnIndex, columnWordWithoutSpaces);
            }
        }
        return matrix;
    }

    /**
     * Method that compacts the matrix if one column is empty
     *
     * @param matrix the matrix given
     * @return the matrix compacted
     */
    private char[][] translation(char[][] matrix) {
        List<Integer> columnsNotEmptyList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            String columnWord = convertAColumnDatatoWord(matrix, columnIndex);
            int spaces = getNumberOfSpacesOfAString(columnWord);
            if (spaces != matrix[0].length) {
                columnsNotEmptyList.add(columnIndex);
            }
        }

        int shouldEndColumnIndex = columnsNotEmptyList.size();

        int listIndex = 0;
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            if (columnIndex < shouldEndColumnIndex) {
                int columnWordNotEmptyIndex = columnsNotEmptyList.get(listIndex);
                String columnWordNotEmpty = convertAColumnDatatoWord(matrix, columnWordNotEmptyIndex);
                if (columnIndex != columnsNotEmptyList.get(listIndex)) {
                    insertColumnWordFromPosition(matrix, 0, columnIndex, columnWordNotEmpty);
                }
                listIndex++;
            } else {
                if (!isEmptyColumn(matrix, columnIndex)) {
                    int lenght = 10;
                    String tenEmptySpacesString = generateEmptySpacesString(lenght);
                    insertColumnWordFromPosition(matrix, 0, columnIndex, tenEmptySpacesString);
                }
            }
        }
        return matrix;
    }

    private boolean isEmptyColumn(char[][] matrix, int columnIndex) {
        boolean result = false;
        String columnWord = convertAColumnDatatoWord(matrix, columnIndex);
        int emptySpaces = getNumberOfSpacesOfAString(columnWord);
        if (emptySpaces == matrix[0].length) result = true;
        return result;
    }

    public boolean containsInDictionary(String[] dictionary, String word) {
        boolean result = false;
        int index = 0;
        while (index < dictionary.length) {
            String dictionaryWord = dictionary[index];
            if (dictionaryWord.equals(word)) {
                result = true;
                index = dictionary.length - 1;
            }
            index++;
        }
        return result;
    }


    private List<String> readableWordsList(char[][] matrix, List<String> dictionary) {
        List<String> readableWordsList = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            String rowWord = convertARowDatatoWord(matrix, rowIndex);
            for (String word : dictionary) {
                String reversedWord = reverseString(word);
                if (rowWord.contains(word)) {
                    readableWordsList.add(word);
                } else if (rowWord.contains(reversedWord)) {
                    readableWordsList.add(reversedWord);
                }
                //System.out.println("Check rowWord : " + rowWord + " contains : " + word + " result : " + result);
            }
        }
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            String columnWord = convertAColumnDatatoWord(matrix, columnIndex);

            for (String word : dictionary) {
                String reversedWord = reverseString(word);
                if (columnWord.contains(word)) {
                    readableWordsList.add(word);
                } else if (columnWord.contains(reversedWord)) {
                    readableWordsList.add(reversedWord);
                }
            }
        }
        return readableWordsList;
    }

    public boolean hasReadableWords(char[][] matrix, List<String> dictionary) {

        return !readableWordsList(matrix, dictionary).isEmpty();
       /* List<String> wordsList = new ArrayList<>(Arrays.asList(dictionary));
        boolean result = false;
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            String rowWord = convertARowDatatoWord(matrix, rowIndex);

            for (int wordsIndex = 0; wordsIndex < wordsList.size(); wordsIndex++) {
                String word = wordsList.get(wordsIndex);
                result = rowWord.contains(word) || rowWord.contains(reverseString(word));
                //System.out.println("Check rowWord : " + rowWord + " contains : " + word + " result : " + result);
                if (result) {
                    wordsIndex = wordsList.size() - 1;
                    rowIndex = matrix.length - 1;
                }
            }
        }
        if (!result) {
            for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
                String columnWord = convertAColumnDatatoWord(matrix, columnIndex);

                for (int wordsIndex = 0; wordsIndex < wordsList.size(); wordsIndex++) {
                    String word = wordsList.get(wordsIndex);
                    result = columnWord.contains(word) || columnWord.contains(reverseString(word));
                    //System.out.println("Check columnWord : " + columnWord + " contains : " + word + " result : " + result);
                    if (result) {
                        wordsIndex = wordsList.size() - 1;
                        columnIndex = matrix[0].length - 1;
                    }
                }
            }
        }
        return result;*/
    }

    private boolean isEqualsTo(char letter, char... letters) {
        boolean result = false;
        int letterIndex = 0;
        while (letterIndex < letters.length) {
            if (letter == letters[letterIndex]) {
                result = true;
                letterIndex = letters.length - 1;
            }
            letterIndex++;
        }
        return result;
    }

    private boolean isEqualsTo(String word, String... words) {
        boolean result = true;
        int wordIndex = 0;
        while (wordIndex < word.length()) {
            if (!words[wordIndex].equals(word)) {
                result = false;
                wordIndex = words.length - 1;
            }
            wordIndex++;
        }
        return result;
    }

    private boolean isCorrect(boolean... bools) {
        boolean result = true;

        int boolIndex = 0;
        while (boolIndex < bools.length) {
            boolean condition = bools[boolIndex];
            if (!condition) {
                result = false;
                boolIndex = bools.length - 1;
            }
            boolIndex++;
        }
        return result;
    }

    /**
     * Method that checks if the operationWord word is correct
     * <p>
     * Operations word must have this format :
     * Formed by 3 letters:
     * Must be one of "LET", "POS", "PAL".
     * Formed by 4 letters :
     * First letter : the row index in number from 0 to 9.
     * Seccond letter : the column index in number from 0 to 9.
     * Third letter : the direction, which must one of 'N', 'S', 'E', 'O'.
     * Four letter : the lenght of the operationWord, from 0 to 9.
     *
     * @param operationWord the operationWord word we want to check
     * @return true if the operationWord word it´s correct, otherwise false.
     */
    private boolean isCorrectOperationWord(String operationWord) {
        boolean result = false;
        //Operations must be formed by 4 letters
        if (operationWord.length() == 3) {
            if (!isEqualsTo(operationWord, "LET", "POS", "PAL")) return result;
        } else if (operationWord.length() == 4) {
            char rowIndexLetter = operationWord.charAt(0);
            char columnIndexLetter = operationWord.charAt(1);
            char directionLetter = operationWord.charAt(2);
            char lenghtLetter = operationWord.charAt(3);

            //PS : 0 in ASCII corresponds to 48
            //PS : 9 in ASCII corresponds to 57
            int beforeZeroAsciiIndex = 47;
            int afterNineAsciiIndex = 58;


            //Check multiples conditions
            result = isCorrect(
                    rowIndexLetter > beforeZeroAsciiIndex && rowIndexLetter < afterNineAsciiIndex,
                    columnIndexLetter > beforeZeroAsciiIndex && columnIndexLetter < afterNineAsciiIndex,
                    isEqualsTo(directionLetter, 'N', 'S', 'O', 'E'),
                    lenghtLetter > beforeZeroAsciiIndex && lenghtLetter < afterNineAsciiIndex);


            if (result) {
                String rowIndexString = String.valueOf(operationWord.charAt(0));
                String columnIndexString = String.valueOf(operationWord.charAt(1));
                String directionString = String.valueOf(operationWord.charAt(2));
                String lenghtString = String.valueOf(operationWord.charAt(3));
                Object[] operationWordDataArray = getOperationData(rowIndexString, columnIndexString, directionString, lenghtString);

                int rowIndex = (int) operationWordDataArray[0];
                int columnIndex = (int) operationWordDataArray[1];
                int lenght = (int) operationWordDataArray[3];

                int minIndex = 0;
                int maxIndex = 9;
                switch (directionLetter) {
                    case 'N':
                        result = rowIndex - lenght + 1 >= minIndex;
                        break;
                    case 'S':
                        result = rowIndex + lenght - 1 <= maxIndex;
                        break;
                    case 'O':
                        result = columnIndex - lenght + 1 >= minIndex;
                        break;
                    case 'E':
                        result = columnIndex + lenght - 1 <= maxIndex;
                        break;
                }
            }
        }
        return result;
    }


    private Object[] getOperationData(String rowIndexString, String columnIndexString, String directionString, String lenghtString) {
        int rowIndex = Integer.parseInt(rowIndexString);
        int columnIndex = Integer.parseInt(columnIndexString);
        GameMatrix.TYPE type = null;
        switch (directionString) {
            case "N":
                type = GameMatrix.TYPE.N;
                break;
            case "S":
                type = GameMatrix.TYPE.S;
                break;
            case "E":
                type = GameMatrix.TYPE.E;
                break;
            case "O":
                type = GameMatrix.TYPE.O;
                break;
        }
        int lenght = Integer.parseInt(lenghtString);
        return new Object[]{rowIndex, columnIndex, type, lenght};
    }

    public Object[] requestOperation(Scanner scanner) {
        System.out.println("Introduzca las coordenadas");
        System.out.println("Si desea obtener una pista, introduzca LET, POS O PAL");
        String operationWord = scanner.nextLine();

        while (!isCorrectOperationWord(operationWord)) {
            System.out.println("Introduzca las coordenadas correctas");
            System.out.println("Si desea obtener una pista, introduzca LET, POS O PAL");
            operationWord = scanner.nextLine();
        }
        Object[] operationDataArray;
        if (operationWord.length() == 4) {
            //operationWord = operationWord.toUpperCase(Locale.ROOT);
            String rowIndex = String.valueOf(operationWord.charAt(0));
            String columnIndex = String.valueOf(operationWord.charAt(1));
            String direction = String.valueOf(operationWord.charAt(2));
            String lenght = String.valueOf(operationWord.charAt(3));
            operationDataArray = getOperationData(rowIndex, columnIndex, direction, lenght);
        } else {
            operationDataArray = new Object[]{operationWord};
        }
        return operationDataArray;
    }

    public Object[] getOperationDataArray(char[][] matrix, int startRowIndex, int startColumnIndex, TYPE type, int lenght) {
        String dataWord = "";
        int startIndex = 0;
        int finalIndex = 0;
        switch (type) {
            case N:
                dataWord = convertAColumnDatatoWord(matrix, startColumnIndex);
                startIndex = startRowIndex - lenght + 1;
                finalIndex = startRowIndex + 1;
                break;
            case S:
                dataWord = convertAColumnDatatoWord(matrix, startColumnIndex);
                startIndex = startRowIndex;
                finalIndex = startRowIndex + lenght;
                break;
            case E:
                dataWord = convertARowDatatoWord(matrix, startRowIndex);
                startIndex = startColumnIndex;
                finalIndex = startColumnIndex + lenght;
                break;
            case O:
                dataWord = convertARowDatatoWord(matrix, startRowIndex);
                startIndex = startColumnIndex - lenght + 1;
                finalIndex = startColumnIndex + 1;
                break;
        }
        //System.out.println("startIndex " + startIndex);
        //System.out.println("finalIndex " + finalIndex);
        String selectedWord = dataWord.substring(startIndex, finalIndex);

        System.out.println("selected word : " + selectedWord);

        if (type == TYPE.O || type == TYPE.N) selectedWord = reverseString(selectedWord);

        return new Object[]{startIndex, selectedWord};
    }

    //TODO
    public int getAClue(String clue, int record) {
        String firstClueWord;
        switch (clue) {
            case "LET":
                if (record >= 1) {
                    //Find a clue
                }
                break;
            case "POS":
                if (record >= 2) {

                }
                break;
            case "PAL":
                break;
        }
        return record;
    }

    public char[][] doOperation(char[][] matrix, int startRowIndex, int startColumnIndex, TYPE type, int lenght) {
        Object[] operationDataArray = getOperationDataArray(matrix, startRowIndex, startColumnIndex, type, lenght);
        int startIndex = (int) operationDataArray[0];
        String selectedWord = (String) operationDataArray[1];
        String spacesString = generateEmptySpacesString(selectedWord.length());
        switch (type) {
            case N:
            case S:
                insertColumnWordFromPosition(matrix, startIndex, startColumnIndex, spacesString);
                break;
            case E:
            case O:
                insertRowWordFromPosition(matrix, startRowIndex, startIndex, spacesString);
                break;
        }
        gravity(matrix);
        translation(matrix);
        return matrix;
    }

    private void insertRowWordFromPosition(char[][] matrix, int rowIndex, int startColumnIndex, String word) {
        for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            matrix[rowIndex][startColumnIndex] = word.charAt(letterIndex);
            startColumnIndex++;
        }
    }

    private char[][] insertColumnWordFromPosition(char[][] matrix, int startRowIndex, int columnIndex, String word) {
        for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            matrix[startRowIndex][columnIndex] = word.charAt(letterIndex);
            startRowIndex++;
        }
        return matrix;
    }


    private boolean isPossibleInsertLetterToRow(char[][] matrix, int rowIndex, int spacesNeeded) {
        boolean result = false;
        String rowWord = convertARowDatatoWord(matrix, rowIndex);
        if (getNumberOfSpacesOfAString(rowWord) >= spacesNeeded) {
            result = true;
        }
        return result;
    }

    private boolean isPossibleInsertWordToColumn(char[][] matrix, int columnIndex, int spacesNeeded) {
        boolean result = false;
        String columnWord = convertAColumnDatatoWord(matrix, columnIndex);
        if (getNumberOfSpacesOfAString(columnWord) >= spacesNeeded) {
            result = true;
        }
        return result;
    }


    private List<Integer> getEmptySpacesPositionsFromAString(String word) {
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

    public int getNumberOfSpacesOfAString(String data) {
        int whiteSpacesCount = 0;
        for (char lettre :
                data.toCharArray()) {
            if (Character.isWhitespace(lettre)) whiteSpacesCount++;

        }
        return whiteSpacesCount;
    }

    private String convertAColumnDatatoWord(char[][] matrix, int columIndex) {
        StringBuilder columnWord = new StringBuilder();

        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            char letter = matrix[rowIndex][columIndex];
            columnWord.append(letter);

        }
        return columnWord.toString();
    }

    /**
     * Method that converts a row of the matrix to String
     *
     * @param rowIndex the row index given
     * @return the entire row in String
     */
    private String convertARowDatatoWord(char[][] matrix, int rowIndex) {
        StringBuilder rowWord = new StringBuilder();
        for (char letter : matrix[rowIndex]) {
            rowWord.append(letter);
        }
        return rowWord.toString();
    }

    public boolean isEmptyMatrix(char[][] matrix) {
        boolean result = true;
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < matrix[rowIndex].length; columnIndex++) {
                if (matrix[rowIndex][columnIndex] != ' ') result = false;
            }
        }
        return result;
    }

    /**
     * Method that fills the matrix with empty spaces
     */
    private void fillMatrixWithEmptySpaces(char[][] matrix) {
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            for (int columIndex = 0; columIndex < matrix[0].length; columIndex++) {
                matrix[rowIndex][columIndex] = ' ';
            }
        }
    }

    /**
     * Method that prints the game matrix with coordinates
     *
     * @param record
     */
    public void printMatrix(char[][] matrix, int record) {

        System.out.println();
        System.out.println("Record : " + record);
        System.out.println();

        char[][] clonedMatrix = getMatrixWithCoordinates(matrix);

        for (char[] chars : clonedMatrix) {
            for (char aChar : chars) {
                System.out.print(aChar + " ");
            }

            System.out.println();
        }

    }

    /**
     * Method that creates a coordinated matrix based of the one given
     *
     * @return this.matrix with coordinates
     */
    private char[][] getMatrixWithCoordinates(char[][] matrix) {
        int rowCount = 0;
        int columCount = 0;
        char[][] coordinatedMatrix = new char[matrix.length + 2][matrix[0].length + 2];

        //System.out.println("Rows : " + coordinatedMatrix.length + " Colums : " + coordinatedMatrix[0].length);

        for (int rowIndex = 1; rowIndex <= matrix.length; rowIndex++) {
            char numberInAscci = (char) (rowCount + '0');
            char columnCountInAscci = (char) (columCount + '0');

            coordinatedMatrix[rowIndex][0] = numberInAscci;
            coordinatedMatrix[rowIndex][coordinatedMatrix.length - 1] = numberInAscci;

            coordinatedMatrix[0][rowIndex] = columnCountInAscci;
            coordinatedMatrix[coordinatedMatrix.length - 1][rowIndex] = columnCountInAscci;

            for (int columnIndex = 1; columnIndex <= matrix[0].length; columnIndex++) {
                coordinatedMatrix[rowIndex][columnIndex] = matrix[rowIndex - 1][columnIndex - 1];
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

class RandomUtils {

    private RandomUtils() {

    }

    public static List<String> getRandomList(String[] originalDictionary, int lenght) {
        List<String> randomDictionary = new ArrayList<>();

        int index = 0;
        while (index < lenght) {
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


    /**
     * Method that returns a random Boolean
     *
     * @return the random boolean
     */
    public static boolean generateRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}

class FileUtils {
    public static void writeRecordInFile(File file, int record) {
        Writer wr;
        try {
            wr = new FileWriter(file);
            wr.write("LastRecord : " + record);
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getRecordInFile(File file) {
        //Initial default record
        int record = 10;
        if (file.exists())
            try {
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter(":");
                while (scanner.hasNext()) {
                    String nextString = scanner.next();
                    nextString = nextString.trim();
                    //Check if the nextString is formed by numeric values
                    char firstLetter = nextString.charAt(0);
                    //PS : 0 in ASCII corresponds to 48
                    //PS : 9 in ASCII corresponds to 57
                    int beforeZeroAsciiIndex = 47;
                    int afterNineAsciiIndex = 58;
                    if (firstLetter > beforeZeroAsciiIndex && firstLetter < afterNineAsciiIndex) {
                        record = Integer.parseInt(nextString);
                        //System.out.println("record saved : " + record);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        return record;
    }

}