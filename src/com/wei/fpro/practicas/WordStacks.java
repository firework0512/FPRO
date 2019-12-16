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
        //The max number of words
        int lenght = 10;
        //The file which we save locally the user record
        final File dataFile = new File("data.txt");
        //Get the last record
        int lastRecord = FileUtils.getRecordInFile(dataFile);
        //Start to play
        play(lenght, dictionary, null, keyboard, lastRecord, dataFile);
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
     * @param hasFoundWordsList  the list of words the user has found that belongs to the original dictionary but not to the dictionary that builds the matrix
     * @param keyboard           the {@link Scanner} object we have created
     * @param record             the user last record saved
     * @param dataFile           the file which we will save the record
     */
    private static void play(int length, String[] originalDictionary, List<String> hasFoundWordsList, Scanner keyboard, int record, File dataFile) {

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

        if (hasFoundWordsList == null) {
            hasFoundWordsList = new ArrayList<>();
        }

        //List containing words that user hasn`t removed yet
        List<String> restWordsList = new ArrayList<>(randomWordsList);

        //System.out.println(Arrays.toString(randomDictionary));

        //Create [GameMatrix] instance object
        GameMatrix gameMatrix = new GameMatrix();

        //Now we start the game generating the matrix
        char[][] matrix = gameMatrix.startGame(10, 10, randomDictionary);


        //Check if the matrix has readable words
        while (gameMatrix.hasReadableWords(matrix, randomWordsList)) {
            gameMatrix.printMatrix(matrix, record);
            //We request user to input his operation
            Object[] operationData = gameMatrix.requestOperation(keyboard);

            if (operationData.length == 1) {
                String typeClue = (String) operationData[0];
                Object clue = gameMatrix.getAClue(matrix, randomWordsList, typeClue, record);
                String clueWord;
                System.out.println();
                if (!clue.equals("NO")) {
                    switch (typeClue) {
                        case "LET":
                            clueWord = (String) clue;
                            record--;
                            System.out.println("La primera letra legible de una palabra legible del tablero es : " + clueWord);
                            break;
                        case "POS":
                            //noinspection unchecked
                            Position positionPair = (Position) clue;
                            int rowIndex = positionPair.getRowIndex();
                            int columnIndex = positionPair.getColumIndex();
                            record -= 2;
                            System.out.println("La primera posición de una palabra legible del tablero es : (" + rowIndex + "," + columnIndex + ")");
                            break;
                        case "PAL":
                            clueWord = (String) clue;
                            record -= clueWord.length();
                            System.out.println("La primera palabra legible del tablero es : " + clueWord);
                            break;
                    }
                } else {
                    System.out.println("No tienes puntos suficientes!!!");
                    System.out.println("Venga, que esto es muy fácil.");
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
                    if (!hasFoundWordsList.contains(selectedWord)) {
                        record++;
                        hasFoundWordsList.add(selectedWord);
                        gameMatrix.writeRecord(dataFile, record);
                    } else {
                        System.out.println("Oye, que ya lo has encontrado");
                        System.out.println("No seas avaricioso");
                    }
                    //Check if we have this word in the generated dictionary
                } else if (gameMatrix.containsInDictionary(randomDictionary, selectedWord)) {
                    record += selectedWord.length();
                    restWordsList.remove(selectedWord);
                    matrix = gameMatrix.doOperation(matrix, rowIndex, columnIndex, type, lenght);
                }
            }
        }

        if (gameMatrix.isEmptyMatrix(matrix)) {
            gameMatrix.writeRecord(dataFile, record);
            System.out.println();
            System.out.println("Has conseguido vaciar el tablero!!!");
            System.out.println("¿Quieres volver a jugar? Si es el caso, introduzca si");
            String response = keyboard.nextLine();
            if (response.toLowerCase().equals("si")) {
                play(length, originalDictionary, null, keyboard, record, dataFile);
            }
        } else {
            //Matrix not empty
            //We regenerate a new matrix with given dictionary
            System.out.println();
            System.out.println("No hemos podido encontrar palabras legibles en el tablero");
            System.out.println("Vamos a generar otro tablero con las palabras restantes");
            String[] restWordArray = restWordsList.toArray(new String[0]);
            play(restWordArray.length, restWordArray, hasFoundWordsList, keyboard, record, dataFile);
        }
    }

}

class GameMatrix {

    public GameMatrix() {
        //DO NOTHING
    }

    public char[][] startGame(int rowsSize, int columnsSize, String[] dictionary) {
        char[][] matrix = generateMatrix(rowsSize, columnsSize, dictionary);
        gravity(matrix);
        return matrix;
    }


    /**
     * Method that resets the saved user record
     *
     * @param file the {@link File} we have stored the user record
     * @return true if the file has been deleted, otherwise false
     */
    public boolean resetRecord(File file) {
        return file.delete();
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
            insertInAMatrix(matrix, word, type);
        }
        return matrix;
    }

    private void insertInAMatrix(char[][] matrix, String word, TYPE type) {
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

    }


    private void gravity(char[][] matrix) {
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            String columnString = convertAColumnDatatoWord(matrix, columnIndex);
            List<Integer> emptySpacesPositionsList = getEmptySpacesPositionsFromAString(columnString);
            if (!emptySpacesPositionsList.isEmpty()) {
                String columnWordWithoutSpaces = columnString.replace(" ", "");
                int shouldLetterStartIndex = (matrix[0].length - columnWordWithoutSpaces.length());
                insertColumnWordFromPosition(matrix, 0, columnIndex, generateEmptySpacesString(shouldLetterStartIndex));
                insertColumnWordFromPosition(matrix, shouldLetterStartIndex, columnIndex, columnWordWithoutSpaces);
            }
        }
    }

    /**
     * Method that compacts the matrix if a column is empty
     *
     * @param matrix the matrix given
     */
    private void translation(char[][] matrix) {
        int matrixColumns = matrix[0].length;
        //List of columns not emptys
        List<Integer> columnsNotEmptyList = new ArrayList<>();
        //Iterate all columns
        for (int columnIndex = 0; columnIndex < matrixColumns; columnIndex++) {
            //Check if the column is not empty
            if (!isEmptyColumn(matrix, columnIndex)) {
                //Add to the list
                columnsNotEmptyList.add(columnIndex);
            }
        }

        //Get the size of the list
        int shouldEndColumnIndex = columnsNotEmptyList.size();

        //The temporal list count
        int listIndex = 0;

        //Iterate from 0 to number of columns of the matrix
        for (int columnIndex = 0; columnIndex < matrixColumns; columnIndex++) {
            //Check if the columnIndex is less than the size of the list
            if (columnIndex < shouldEndColumnIndex) {
                //Get the column not empty index
                int columnWordNotEmptyIndex = columnsNotEmptyList.get(listIndex);
                //Check if the column index is not equals to the column not empty index
                if (columnIndex != columnWordNotEmptyIndex) {
                    //Convert the column not empty to a String
                    String columnWordNotEmpty = convertAColumnDatatoWord(matrix, columnWordNotEmptyIndex);
                    //Insert the column not empty word to the iterate columnIndex
                    insertColumnWordFromPosition(matrix, 0, columnIndex, columnWordNotEmpty);
                }
                //Update the list index
                listIndex++;
            } else {
                //Check if the comlumn is not empty
                if (!isEmptyColumn(matrix, columnIndex)) {
                    //String with the total number of columns of the matrix empty spaces
                    String tenEmptySpacesString = generateEmptySpacesString(matrixColumns);
                    //Insert the empty spaces word to the iterate columnIndex
                    insertColumnWordFromPosition(matrix, 0, columnIndex, tenEmptySpacesString);
                }
            }
        }
    }

    /**
     * Check if the column is empty
     * @param matrix the matrix given
     * @param columnIndex the column index which we wanna check
     * @return true if the colum is empty, otherwise false
     */
    private boolean isEmptyColumn(char[][] matrix, int columnIndex) {
        boolean result = false;
        String columnWord = convertAColumnDatatoWord(matrix, columnIndex);
        int emptySpaces = getNumberOfSpacesOfAString(columnWord);
        if (emptySpaces == matrix[0].length) result = true;
        return result;
    }

    /**
     * Method that checks if the {@code dictionary} contains a word
     *
     * @param dictionary the String array which we wanna check
     * @param word the word
     * @return true if the String array contains the word, otherwise false
     */
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

    /**
     * Method that obtains all readable words in the matrix
     *
     * @param matrix     the matrix given
     * @param dictionary the dictionary of the matrix
     * @return the {@link HashMap} containing all the readable words with them position
     */
    private HashMap<Position, String> readableWordsList(char[][] matrix, List<String> dictionary) {
        //Create a HashMap with the position of each readable word
        HashMap<Position, String> readableWordsHashMap = new HashMap<>();
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            String rowWord = convertARowDatatoWord(matrix, rowIndex);
            for (String word : dictionary) {
                String reversedWord = reverseString(word);
                Position position;
                int columnIndex;
                if (rowWord.contains(word)) {
                    columnIndex = rowWord.indexOf(word);
                    position = new Position(rowIndex, columnIndex);
                    readableWordsHashMap.put(position, word);
                } else if (rowWord.contains(reversedWord)) {
                    columnIndex = rowWord.indexOf(reversedWord) + reversedWord.length() - 1;
                    position = new Position(rowIndex, columnIndex);
                    readableWordsHashMap.put(position, word);
                }
                //System.out.println("Check rowWord : " + rowWord + " contains : " + word + " result : " + result);
            }
        }
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            String columnWord = convertAColumnDatatoWord(matrix, columnIndex);
            for (String word : dictionary) {
                String reversedWord = reverseString(word);
                Position position;
                int rowIndex = 0;
                if (columnWord.contains(word)) {
                    rowIndex = columnWord.indexOf(word);
                    position = new Position(rowIndex, columnIndex);
                    readableWordsHashMap.put(position, word);
                } else if (columnWord.contains(reversedWord)) {
                    rowIndex = columnWord.indexOf(reversedWord) + reversedWord.length() - 1;
                    position = new Position(rowIndex, columnIndex);
                    readableWordsHashMap.put(position, word);
                }
            }
        }
        return readableWordsHashMap;
    }

    public boolean hasReadableWords(char[][] matrix, List<String> dictionary) {
        return !readableWordsList(matrix, dictionary).isEmpty();
    }

    private boolean isEqualsToOne(char letter, char... letters) {
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

    private boolean isEqualsToOne(String word, String... words) {
        boolean result = false;
        int wordIndex = 0;
        while (wordIndex < words.length) {
            String correctWord = words[wordIndex];
            if (word.equals(correctWord)) {
                result = true;
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
     * Third letter : the direction, which must be one of 'N', 'S', 'E', 'O'.
     * Four letter : the lenght of the desired word, from 0 to 9.
     *
     * @param operationWord the operationWord word we want to check
     * @return true if the operationWord word it´s correct, otherwise false.
     */
    private boolean isCorrectOperationWord(String operationWord) {
        boolean result = false;
        //Operations must be formed by 3 o 4 letters
        if (operationWord.length() == 3) {
            result = isEqualsToOne(operationWord, "LET", "POS", "PAL");
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
                    isEqualsToOne(directionLetter, 'N', 'S', 'O', 'E'),
                    lenghtLetter > beforeZeroAsciiIndex && lenghtLetter < afterNineAsciiIndex);


            if (result) {
                String rowIndexString = String.valueOf(rowIndexLetter);
                String columnIndexString = String.valueOf(columnIndexLetter);
                String directionString = String.valueOf(directionLetter);
                String lenghtString = String.valueOf(lenghtLetter);
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

    //TODO NEED OPTIMISATION
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
        String selectedWord = dataWord.substring(startIndex, finalIndex);
        //System.out.println("selected word : " + selectedWord);
        if (type == TYPE.O || type == TYPE.N) selectedWord = reverseString(selectedWord);

        return new Object[]{startIndex, selectedWord};
    }

    /**
     * Method that returns a clue based of different {@code clue} word
     *
     * @param matrix     the matrix which we wanna find a clue
     * @param dictionary the dictionary provided to create the matrix
     * @param clue       the type of clue we want
     * @param record     the user record
     * @return a clue that will be a {@link String} object or a {@link Position} object
     */

    public Object getAClue(char[][] matrix, List<String> dictionary, String clue, int record) {
        HashMap<Position, String> readableWordsHashMap = readableWordsList(matrix, dictionary);
        Map.Entry<Position, String> firstEntry = readableWordsHashMap
                .entrySet()
                .stream()
                .findFirst()
                .get();

        String firstClueWord = firstEntry.getValue();
        String recordNotEnought = "NO";
        Object returnObject = recordNotEnought;

        switch (clue) {
            case "LET":
                if (record >= 1) {
                    //Find a clue
                    int firstLetterIndex = 0;
                    int secondLetterIndex = 1;
                    firstClueWord = firstClueWord.substring(firstLetterIndex, secondLetterIndex);
                    returnObject = firstClueWord;
                }
                break;
            case "POS":
                if (record >= 2) {
                    returnObject = firstEntry.getKey();
                }
                break;
            case "PAL":
                boolean hasEnoughRecordPoints = readableWordsHashMap
                        .entrySet()
                        .stream()
                        .anyMatch(entry -> entry.getValue().length() <= record);
                if (hasEnoughRecordPoints) {
                    firstEntry = readableWordsHashMap
                            .entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().length() <= record)
                            .findFirst()
                            .get();
                    firstClueWord = firstEntry.getValue();
                    returnObject = firstClueWord;
                }
                break;
        }
        return returnObject;
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

    private void insertColumnWordFromPosition(char[][] matrix, int startRowIndex, int columnIndex, String word) {
        for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            matrix[startRowIndex][columnIndex] = word.charAt(letterIndex);
            startRowIndex++;
        }
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

    private int getNumberOfSpacesOfAString(String data) {
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
            int columnIndex = 0;
            int maxColumnIndex = matrix[rowIndex].length;
            while (columnIndex < maxColumnIndex) {
                if (matrix[rowIndex][columnIndex] != ' ') {
                    result = false;
                    columnIndex = maxColumnIndex - 1;
                }
                columnIndex++;
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
     * @param record the user current record
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
            int randomPosition = generateRandomNumber(0, originalDictionary.length - 1);
            String palabra = originalDictionary[randomPosition];
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

    private FileUtils() {

    }

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
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        return record;
    }

}


//DATA CLASS

class Position {
    private int rowIndex;
    private int columIndex;

    public Position(int rowIndex, int columIndex) {
        this.rowIndex = rowIndex;
        this.columIndex = columIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumIndex() {
        return columIndex;
    }

    public void setColumIndex(int columIndex) {
        this.columIndex = columIndex;
    }

}