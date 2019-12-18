package com.wei.fpro.practicas;

import java.io.*;
import java.util.*;

public class WordStacksV2 {

    public static void main(String[] args) {
        System.out.println("¿Desea empezar el juego en modo de pruebas?");
        System.out.println("Si es el caso pulse p o P, en otro caso pulse otra tecla.");
        Scanner keyboard = new Scanner(System.in);
        String response = keyboard.nextLine();
        String[] originalDictionaryArray = getDictionary(response);
        boolean shouldContinue = true;
        //The max number of words
        int lenght = 10;
        //The file which we save locally the user record
        final File dataFile = new File("data.txt");
        //Get the last record
        int lastRecord = getRecordInFile(dataFile);

        while (shouldContinue) {
            //The random words dictionary
            String[] randomDictionaryArray = originalDictionaryArray;

            if (originalDictionaryArray.length > lenght) {
                //Put random words to the random words array
                randomDictionaryArray = getRandomList(originalDictionaryArray, lenght);
            }
            //Array of Strings(words) that user hasn`t removed yet
            String[] restWordsArray = randomDictionaryArray.clone();

            //Array of Strings(words) the user has found that belongs to the original dictionary but not to the dictionary that builds the matrix
            String[] hasFoundWordsArray = new String[originalDictionaryArray.length];
            int hasFoundWordsArrayIndex = 0;

            //Now we start the game generating the matrix
            char[][] matrix = startGame(10, 10, randomDictionaryArray);
            //Array of Strings (readable words in the matrix)
            String[] readableWordsArray = readableWordsList(matrix, randomDictionaryArray);

            //Check if the matrix has readable words
            while (!isEmptyMatrix(matrix)) {
                if (readableWordsArray.length == 0) {
                    //Matrix not empty
                    //We regenerate a new matrix with rest words dictionary
                    System.out.println();
                    System.out.println("No hemos podido encontrar palabras legibles en el tablero");
                    System.out.println("Vamos a generar otro tablero con las palabras restantes");
                    matrix = startGame(10, 10, restWordsArray);
                }
                printMatrix(matrix, lastRecord);
                //We request user to input his operation
                String userOperationString = requestOperation(keyboard);
                //Check the operation data size, if it´s lenght is 3 then we know that the user has inputted one of "LET", "POS", "PAL"
                if (userOperationString.length() == 3) {
                    String clue = getAClue(userOperationString, lastRecord, readableWordsArray);
                    System.out.println();
                    //Check if the user has enough record points by comparing the clue object to "NO"
                    if (!clue.equals("NO")) {
                        switch (userOperationString) {
                            case "LET":
                                lastRecord--;
                                System.out.println("La primera letra legible de una palabra legible del tablero es : " + clue);
                                break;
                            case "POS":
                                lastRecord -= 2;
                                System.out.println("La primera posición de una palabra legible del tablero es : " + clue);
                                break;
                            case "PAL":
                                lastRecord -= clue.length();
                                System.out.println("Una palabra legible del tablero es : " + clue);
                                break;
                        }
                    } else {
                        System.out.println("No tienes puntos suficientes!!!");
                        System.out.println("Venga, que esto es muy fácil.");
                    }
                } else if (userOperationString.length() == 4) {
                    char rowIndexLetter = userOperationString.charAt(0);
                    char columnIndexLetter = userOperationString.charAt(1);
                    char type = userOperationString.charAt(2);
                    char selectedLenghtLetter = userOperationString.charAt(3);

                    int rowIndex = getNumericValue(rowIndexLetter);
                    int columnIndex = getNumericValue(columnIndexLetter);
                    int selectedLenght = getNumericValue(selectedLenghtLetter);

                    String selectedWord = (String) getOperationDataArray(matrix, rowIndex, columnIndex, type, selectedLenght)[1];
                    //Check if we can perform an operation
                    //Check if we have this word int the original dictionary
                    if (containsInDictionary(originalDictionaryArray, selectedWord) && !containsInDictionary(randomDictionaryArray, selectedWord)) {
                        if (!containsInDictionary(hasFoundWordsArray, selectedWord)) {
                            System.out.println(selectedWord + " está en el diccionario principal pero no en el diccionario que generó la matriz");
                            lastRecord++;
                            hasFoundWordsArray[hasFoundWordsArrayIndex] = selectedWord;
                            hasFoundWordsArrayIndex++;
                        } else {
                            System.out.println("Oye, que ya lo has encontrado");
                            System.out.println("No seas avaricioso");
                        }
                        //Check if we have this word in the generated dictionary
                    } else if (containsInDictionary(randomDictionaryArray, selectedWord)) {
                        System.out.println(selectedWord + " está en el diccionario que generó la matriz");
                        lastRecord += selectedWord.length();
                        restWordsArray = removeElementFromArray(restWordsArray, selectedWord);
                        matrix = doOperation(matrix, rowIndex, columnIndex, type, selectedLenght);
                    }
                }
                readableWordsArray = readableWordsList(matrix, randomDictionaryArray);
            }

            writeRecord(dataFile, lastRecord);
            System.out.println();
            System.out.println("Has conseguido vaciar el tablero!!!");
            System.out.println("¿Quieres volver a jugar? Si es el caso, introduzca si");
            response = keyboard.nextLine();
            if (!response.toLowerCase().equals("si")) {
                shouldContinue = false;
            }
        }
    }

    private static String[] removeElementFromArray(String[] array, String word) {
        for (int elementIndex = 0; elementIndex < array.length; elementIndex++) {
            String element = array[elementIndex];
            if (element != null && element.equals(word)) {
                array[elementIndex] = "";
            }
        }
        array = resizeAndOrderStringArray(array);
        return array;
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

    public static String[] getRandomList(String[] originalDictionary, int lenght) {
        String[] randomDictionary = new String[lenght];

        int index = 0;
        while (index < lenght) {
            int randomPosition = generateRandomNumber(0, originalDictionary.length - 1);
            String palabra = originalDictionary[randomPosition];

            if (!containsInDictionary(randomDictionary, palabra)) {
                randomDictionary[index] = palabra;
                index++;
            }
        }
        return randomDictionary;
    }

    /**
     * Method that returns a random number
     *
     * @param origin the start number inclusive
     * @param bound  the final number inclusive
     * @return the random number
     */
    public static int generateRandomNumber(int origin, int bound) {
        return ((int) (Math.random() * ((bound - origin) + 1)) + origin);
    }


    /**
     * Method that returns a random Boolean
     *
     * @return the random boolean
     */
    public static boolean generateRandomBoolean() {
        return generateRandomNumber(0, 1) == 0;
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


    public static char[][] startGame(int rowsSize, int columnsSize, String[] dictionary) {
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
    public static void writeRecord(File file, int record) {
        writeRecordInFile(file, record);
    }

    /**
     * Method that generates the game matrix
     *
     * @param rowsSize    the number of rows the matrix has
     * @param columnsSize the number of columns the matrix has
     * @param dictionary  the dictionary provided to generate the matrix
     * @return the matrix
     */
    private static char[][] generateMatrix(int rowsSize, int columnsSize, String[] dictionary) {
        char[][] matrix = new char[rowsSize][columnsSize];

        int randomNumber;

        //First we fill the matrix with empty spaces
        fillMatrixWithEmptySpaces(matrix);

        //We parse every single word of the random dictionary
        for (String word : dictionary) {
            randomNumber = generateRandomNumber(0, 1);
            //We insert the word in the matrix randomly
            char type = ' ';
            if (randomNumber == 0) {
                type = 'R';
            } else if (randomNumber == 1) {
                type = 'C';
            }
            try {
                insertInAMatrix(matrix, word, type, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return matrix;
    }

    private static void insertInAMatrix(char[][] matrix, String word, char type, int errorCount) throws Exception {
        if (errorCount >= 2) {
            throw new Exception("No es posible insertar más palabras en la matriz, prueba con otra matriz más grande\n + Fallo al insertar : " + word);
        }

        boolean needReversed = generateRandomBoolean();
        //We reverse the word
        if (needReversed) {
            word = reverseString(word);
        }
        if (type == 'R') {
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
                        String columnWord = convertColumnToString(matrix, columnIndex);
                        int[] emptyPositionsList = getEmptySpacesPositionsFromAString(columnWord);
                        int lastEmptyPosition = emptyPositionsList.length - 1;
                        int rowIndex = emptyPositionsList[lastEmptyPosition];
                        matrix[rowIndex][columnIndex] = word.charAt(wordsCharsCount);
                        wordsCharsCount++;
                    }
                    columnIndex++;
                }
            } else {
                //Call the same method but this time we know that it´s impossible to insert the word in a row
                //So, we insert the word in a column
                errorCount++;
                insertInAMatrix(matrix, word, 'C', errorCount);
            }

        } else if (type == 'C') {
            boolean isPossibleToInsert = false;
            int columnIndex = 0;
            int index = columnIndex;
            while (index < matrix[0].length) {
                boolean possibleInsertLetterToColumn = isPossibleInsertWordToColumn(matrix, index, word.length());
                if (possibleInsertLetterToColumn) {
                    isPossibleToInsert = true;
                    columnIndex = index;
                    index = matrix[0].length - 1;
                }
                index++;
            }
            if (isPossibleToInsert) {
                int wordsCharsCount = 0;
                while (wordsCharsCount < word.length()) {
                    String columnWord = convertColumnToString(matrix, columnIndex);
                    int[] emptyPositionsList = getEmptySpacesPositionsFromAString(columnWord);
                    int rowIndex = emptyPositionsList[0];
                    matrix[rowIndex][columnIndex] = word.charAt(wordsCharsCount);
                    wordsCharsCount++;
                }
            } else {
                //Call the same method but this time we know that it´s impossible to insert the word in a column
                //So, we insert the word in a row
                errorCount++;
                insertInAMatrix(matrix, word, 'R', errorCount);
            }
        }

    }


    private static void gravity(char[][] matrix) {
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            String columnString = convertColumnToString(matrix, columnIndex);
            int[] emptySpacesPositionsList = getEmptySpacesPositionsFromAString(columnString);
            if (emptySpacesPositionsList.length > 0) {
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
    private static void translation(char[][] matrix) {
        int matrixColumns = matrix[0].length;
        //List of columns not emptys
        int totalNumberOfNoEmptyColumns = getTotalNumberOfNoEmptyColums(matrix);
        int[] columnsNotEmptyArray = new int[totalNumberOfNoEmptyColumns];

        int columnsNotEmptyArrayCount = 0;
        //Iterate all columns
        for (int columnIndex = 0; columnIndex < matrixColumns; columnIndex++) {
            //Check if the column is not empty
            if (isNotEmptyColumn(matrix, columnIndex)) {
                //Add to the array
                columnsNotEmptyArray[columnsNotEmptyArrayCount] = columnIndex;
                columnsNotEmptyArrayCount++;
            }
        }

        //Get the size of the list
        int shouldEndColumnIndex = columnsNotEmptyArray.length;

        columnsNotEmptyArrayCount = 0;

        //Iterate from 0 to number of columns of the matrix
        for (int columnIndex = 0; columnIndex < matrixColumns; columnIndex++) {
            //Check if the columnIndex is less than the size of the list
            if (columnIndex < shouldEndColumnIndex) {
                //Get the column not empty index
                int columnWordNotEmptyIndex = columnsNotEmptyArray[columnsNotEmptyArrayCount];
                //Check if the column index is not equals to the column not empty index
                if (columnIndex != columnWordNotEmptyIndex) {
                    //Convert the column not empty to a String
                    String columnWordNotEmpty = convertColumnToString(matrix, columnWordNotEmptyIndex);
                    //Insert the column not empty word to the iterate columnIndex
                    insertColumnWordFromPosition(matrix, 0, columnIndex, columnWordNotEmpty);
                }
                //Update the list index
                columnsNotEmptyArrayCount++;
            } else {
                //Check if the column is not empty
                if (isNotEmptyColumn(matrix, columnIndex)) {
                    //String with the total number of columns of the matrix empty spaces
                    String tenEmptySpacesString = generateEmptySpacesString(matrixColumns);
                    //Insert the empty spaces word to the iterate columnIndex
                    insertColumnWordFromPosition(matrix, 0, columnIndex, tenEmptySpacesString);
                }
            }
        }
    }


    /**
     * Method that gets the total number of no empty columns
     *
     * @param matrix the matrix given
     * @return the total number of no empty columns
     */
    private static int getTotalNumberOfNoEmptyColums(char[][] matrix) {
        int isNotEmptyCount = 0;
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            boolean isNotEmptyColumn = isNotEmptyColumn(matrix, columnIndex);
            if (isNotEmptyColumn) {
                isNotEmptyCount++;
            }
        }
        return isNotEmptyCount;
    }

    /**
     * Check if the column is empty
     *
     * @param matrix      the matrix given
     * @param columnIndex the column index which we wanna check
     * @return true if the colum is empty, otherwise false
     */
    private static boolean isNotEmptyColumn(char[][] matrix, int columnIndex) {
        boolean result = false;
        String columnWord = convertColumnToString(matrix, columnIndex);
        int emptySpaces = getNumberOfSpacesOfAString(columnWord);
        if (emptySpaces == matrix[0].length) result = true;
        return !result;
    }

    /**
     * Method that checks if the {@code dictionary} contains a word
     *
     * @param dictionary the String array which we wanna check
     * @param word       the word
     * @return true if the String array contains the word, otherwise false
     */
    public static boolean containsInDictionary(String[] dictionary, String word) {
        boolean result = false;
        if (dictionary.length > 0) {
            int index = 0;
            while (index < dictionary.length) {
                String dictionaryWord = dictionary[index];
                if (dictionaryWord != null) {
                    if (dictionaryWord.equals(word)) {
                        result = true;
                        index = dictionary.length - 1;
                    }
                } else {
                    index = dictionary.length - 1;
                }
                index++;
            }
        }
        return result;
    }

    /**
     * Method that obtains all readable words in the matrix
     *
     * @param matrix     the matrix given
     * @param dictionary the dictionary of the matrix
     * @return the String array containing all the readable words with them position
     */
    private static String[] readableWordsList(char[][] matrix, String[] dictionary) {
        //Create a String Array containing the position of each readable word
        String[] readableWordsArray = new String[dictionary.length];

        for (int elementIndex = 0; elementIndex < readableWordsArray.length; elementIndex++) {
            readableWordsArray[elementIndex] = "";
        }

        int readableWordsArrayCount = 0;
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            String rowWord = convertRowToString(matrix, rowIndex);
            for (int elementIndex = 0; elementIndex < dictionary.length; elementIndex++) {
                String element = dictionary[elementIndex];
                String reversedWord = reverseString(element);
                int columnIndex;
                if (rowWord.contains(element)) {
                    columnIndex = rowWord.indexOf(element);
                    readableWordsArray[readableWordsArrayCount] += "(" + rowIndex + "," + columnIndex + ")" + element;
                    readableWordsArrayCount++;
                } else if (rowWord.contains(reversedWord)) {
                    columnIndex = rowWord.indexOf(reversedWord) + reversedWord.length() - 1;
                    readableWordsArray[readableWordsArrayCount] += "(" + rowIndex + "," + columnIndex + ")" + element;
                    readableWordsArrayCount++;
                }
            }
        }
        for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
            String columnWord = convertColumnToString(matrix, columnIndex);
            for (String word : dictionary) {
                String reversedWord = reverseString(word);
                int rowIndex = 0;
                if (columnWord.contains(word)) {
                    rowIndex = columnWord.indexOf(word);
                    readableWordsArray[readableWordsArrayCount] += "(" + rowIndex + "," + columnIndex + ")" + word;
                    readableWordsArrayCount++;
                } else if (columnWord.contains(reversedWord)) {
                    rowIndex = columnWord.indexOf(reversedWord) + reversedWord.length() - 1;
                    readableWordsArray[readableWordsArrayCount] += "(" + rowIndex + "," + columnIndex + ")" + word;
                    readableWordsArrayCount++;
                }
            }
        }
        readableWordsArray = resizeAndOrderStringArray(readableWordsArray);
        return readableWordsArray;
    }


    private static String[] resizeAndOrderStringArray(String[] oldArray) {
        int count = 0;
        for (int elementIndex = 0; elementIndex < oldArray.length; elementIndex++) {
            String element = oldArray[elementIndex];
            if (element != null && !element.isEmpty()) {
                count++;
            }
        }
        String[] newArray = new String[count];
        int cachedIndex = 0;
        int elementIndex = 0;
        while (elementIndex < count) {
            String element = oldArray[cachedIndex];
            if (element != null && !element.isEmpty()) {
                newArray[elementIndex] = oldArray[cachedIndex];
                elementIndex++;
            }
            cachedIndex++;
        }
        return newArray;
    }


    private static boolean isEqualsToOne(char letter, char[] letters) {
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

    private static boolean isEqualsToOne(String word, String[] words) {
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

    private static boolean isCorrect(boolean[] bools) {
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
    private static boolean isCorrectOperationWord(String operationWord) {
        boolean result = false;
        //Operations must be formed by 3 o 4 letters
        if (operationWord.length() == 3) {
            String[] correctClueOperations = new String[]{"LET", "POS", "PAL"};
            result = isEqualsToOne(operationWord, correctClueOperations);
        } else if (operationWord.length() == 4) {
            char rowIndexLetter = operationWord.charAt(0);
            char columnIndexLetter = operationWord.charAt(1);
            char directionLetter = operationWord.charAt(2);
            char lenghtLetter = operationWord.charAt(3);

            //PS : 0 in ASCII corresponds to 48
            //PS : 9 in ASCII corresponds to 57
            int beforeZeroAsciiIndex = 47;
            int afterNineAsciiIndex = 58;

            char[] correctDirectionLetters = new char[]{'N', 'S', 'O', 'E'};

            boolean[] multipleConditions = new boolean[]{
                    rowIndexLetter > beforeZeroAsciiIndex && rowIndexLetter < afterNineAsciiIndex,
                    columnIndexLetter > beforeZeroAsciiIndex && columnIndexLetter < afterNineAsciiIndex,
                    isEqualsToOne(directionLetter, correctDirectionLetters),
                    lenghtLetter > beforeZeroAsciiIndex && lenghtLetter < afterNineAsciiIndex};

            //Check multiples conditions
            result = isCorrect(multipleConditions);

            if (result) {
                int rowIndex = getNumericValue(rowIndexLetter);
                int columnIndex = getNumericValue(columnIndexLetter);
                int lenght = getNumericValue(lenghtLetter);

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

    public static String requestOperation(Scanner scanner) {
        System.out.println("Introduzca las coordenadas");
        System.out.println("Si desea obtener una pista, introduzca LET, POS O PAL");
        String operationWord = scanner.nextLine();

        while (!isCorrectOperationWord(operationWord)) {
            System.out.println("Introduzca las coordenadas correctas");
            System.out.println("Si desea obtener una pista, introduzca LET, POS O PAL");
            operationWord = scanner.nextLine();
        }

        return operationWord;
    }

    //TODO NEED OPTIMISATION
    public static Object[] getOperationDataArray(char[][] matrix, int startRowIndex, int startColumnIndex, char type, int lenght) {
        String dataWord = "";
        int startIndex = 0;
        int finalIndex = 0;
        switch (type) {
            case 'N':
                dataWord = convertColumnToString(matrix, startColumnIndex);
                startIndex = startRowIndex - lenght + 1;
                finalIndex = startRowIndex + 1;
                break;
            case 'S':
                dataWord = convertColumnToString(matrix, startColumnIndex);
                startIndex = startRowIndex;
                finalIndex = startRowIndex + lenght;
                break;
            case 'E':
                dataWord = convertRowToString(matrix, startRowIndex);
                startIndex = startColumnIndex;
                finalIndex = startColumnIndex + lenght;
                break;
            case 'O':
                dataWord = convertRowToString(matrix, startRowIndex);
                startIndex = startColumnIndex - lenght + 1;
                finalIndex = startColumnIndex + 1;
                break;
        }
        String selectedWord = dataWord.substring(startIndex, finalIndex);
        if (type == 'O' || type == 'N') selectedWord = reverseString(selectedWord);

        return new Object[]{startIndex, selectedWord};
    }

    /**
     * Method that returns a clue based of different {@code clue} word
     *
     * @param typeClue           the type of clue we want
     * @param record             the user record
     * @param readableWordsArray the array of readable words
     * @return a clue that will be a {@link String} object
     */

    public static String getAClue(String typeClue, int record, String[] readableWordsArray) {
        System.out.println(Arrays.toString(readableWordsArray));
        String clueWord = "NO";
        switch (typeClue) {
            case "LET":
                if (record >= 1) {
                    removeSpecificStringOfArray(readableWordsArray);
                    int arrayLenght = readableWordsArray.length;
                    int lastPosition = arrayLenght - 1;
                    //Get a random clue
                    int randomPosition = generateRandomNumber(0, lastPosition);
                    clueWord = readableWordsArray[randomPosition];
                    int firstLetterIndex = 0;
                    int secondLetterIndex = 1;
                    clueWord = clueWord.substring(firstLetterIndex, secondLetterIndex);
                }
                break;
            case "POS":
                if (record >= 2) {
                    int arrayLenght = readableWordsArray.length;
                    int lastPosition = arrayLenght - 1;
                    //Get a random clue
                    int randomPosition = generateRandomNumber(0, lastPosition);
                    clueWord = readableWordsArray[randomPosition];
                    int startIndex = clueWord.indexOf("(");
                    int finalIndex = clueWord.indexOf(")") + 1;
                    clueWord = clueWord.substring(startIndex, finalIndex);
                }
                break;
            case "PAL":
                removeSpecificStringOfArray(readableWordsArray);
                boolean hasEnoughRecordPoints = hasLenghtElements(readableWordsArray, record);
                if (hasEnoughRecordPoints) {
                    int elementIndex = 0;
                    int arrayLenght = readableWordsArray.length;
                    int lastPosition = arrayLenght - 1;
                    while (elementIndex < arrayLenght) {
                        String element = readableWordsArray[elementIndex];
                        if (element != null && element.length() <= record) {
                            clueWord = element;
                            elementIndex = lastPosition;
                        }
                        elementIndex++;
                    }
                }
                break;
        }
        return clueWord;
    }


    private static void removeSpecificStringOfArray(String[] array) {
        for (int elementIndex = 0; elementIndex < array.length; elementIndex++) {
            String element = array[elementIndex];
            int startIndex = element.indexOf(")") + 1;
            int finalIndex = element.length();
            array[elementIndex] = element.substring(startIndex, finalIndex);
        }
    }

    /**
     * Method that checks if the String Array contains a element of {@code lenght} element
     *
     * @param array  the String array to be checked
     * @param lenght the desired lenght
     * @return true if has a element, otherwise false
     */
    private static boolean hasLenghtElements(String[] array, int lenght) {
        boolean result = false;
        int elementIndex = 0;
        while (elementIndex < array.length) {
            String element = array[elementIndex];
            if (element != null && element.length() <= lenght) {
                result = true;
                elementIndex = array.length - 1;
            }
            elementIndex++;
        }
        return result;
    }

    public static char[][] doOperation(char[][] matrix, int startRowIndex, int startColumnIndex, char type, int lenght) {
        Object[] operationDataArray = getOperationDataArray(matrix, startRowIndex, startColumnIndex, type, lenght);
        int startIndex = (int) operationDataArray[0];
        String selectedWord = (String) operationDataArray[1];
        String spacesString = generateEmptySpacesString(selectedWord.length());
        switch (type) {
            case 'N':
            case 'S':
                insertColumnWordFromPosition(matrix, startIndex, startColumnIndex, spacesString);
                break;
            case 'E':
            case 'O':
                insertRowWordFromPosition(matrix, startRowIndex, startIndex, spacesString);
                break;
        }
        gravity(matrix);
        translation(matrix);
        return matrix;
    }


    private static void insertRowWordFromPosition(char[][] matrix, int rowIndex, int startColumnIndex, String word) {
        for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            matrix[rowIndex][startColumnIndex] = word.charAt(letterIndex);
            startColumnIndex++;
        }
    }


    private static void insertColumnWordFromPosition(char[][] matrix, int startRowIndex, int columnIndex, String word) {
        for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            matrix[startRowIndex][columnIndex] = word.charAt(letterIndex);
            startRowIndex++;
        }
    }

    private static boolean isPossibleInsertWordToColumn(char[][] matrix, int columnIndex, int spacesNeeded) {
        boolean result = false;
        String columnWord = convertColumnToString(matrix, columnIndex);
        if (getNumberOfSpacesOfAString(columnWord) >= spacesNeeded) {
            result = true;
        }
        return result;
    }


    private static int[] getEmptySpacesPositionsFromAString(String word) {
        int emptySpaces = getNumberOfSpacesOfAString(word);
        int[] emptySpacesPositionArray = new int[0];
        if (emptySpaces > 0) {
            emptySpacesPositionArray = new int[emptySpaces];
            int emptySpaceIndex = word.indexOf(" ");
            int lastIndex = emptySpaceIndex;
            int arrayCount = 0;
            while ((emptySpaceIndex = word.indexOf(" ", lastIndex)) != -1) {
                lastIndex = emptySpaceIndex + 1;
                emptySpacesPositionArray[arrayCount] = emptySpaceIndex;
                arrayCount++;
            }
        }
        return emptySpacesPositionArray;
    }


    @SuppressWarnings("StringConcatenationInLoop")
    private static String convertColumnToString(char[][] matrix, int columIndex) {
        String columnWord = "";

        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            char letter = matrix[rowIndex][columIndex];
            columnWord += letter;

        }
        return columnWord;
    }


    /**
     * Method that converts a row of the matrix to String
     *
     * @param rowIndex the row index given
     * @return the entire row in String
     */
    @SuppressWarnings("StringConcatenationInLoop")
    private static String convertRowToString(char[][] matrix, int rowIndex) {
        String rowWord = "";
        char[] chars = matrix[rowIndex];
        for (int i = 0; i < chars.length; i++) {
            char letter = chars[i];
            rowWord += letter;
        }
        return rowWord;
    }


    public static boolean isEmptyMatrix(char[][] matrix) {
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
    private static void fillMatrixWithEmptySpaces(char[][] matrix) {
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
    public static void printMatrix(char[][] matrix, int record) {

        System.out.println();
        System.out.println("Record : " + record);
        System.out.println();

        char[][] clonedMatrix = getMatrixWithCoordinates(matrix);

        for (int rowIndex = 0; rowIndex < clonedMatrix.length; rowIndex++) {
            char[] chars = clonedMatrix[rowIndex];
            for (int columnIndex = 0; columnIndex < chars.length; columnIndex++) {
                char aChar = chars[columnIndex];
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
    private static char[][] getMatrixWithCoordinates(char[][] matrix) {
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
    @SuppressWarnings("StringConcatenationInLoop")
    private static String reverseString(String word) {
        String reversedString = "";
        for (int letterIndex = word.length() - 1; letterIndex >= 0; letterIndex--) {
            char letter = word.charAt(letterIndex);
            reversedString += letter;
        }
        return reversedString;
    }

    @SuppressWarnings("StringConcatenationInLoop")
    private static String generateEmptySpacesString(int lenght) {
        char emptyChar = ' ';
        String emptyString = "";
        for (int startIndex = 0; startIndex < lenght; startIndex++) {
            emptyString += emptyChar;
        }
        return emptyString;
    }

    private static int getNumberOfSpacesOfAString(String data) {
        int whiteSpacesCount = 0;
        char[] charArray = data.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char lettre = charArray[i];
            if (Character.isWhitespace(lettre)) whiteSpacesCount++;

        }
        return whiteSpacesCount;
    }

    /**
     * Method that converts char into int
     *
     * @param number the number given in char, must be in range of '0' to '9'
     * @return the numeric value in int
     */
    private static int getNumericValue(char number) {
        char zero = '0';
        return number - zero;
    }

}
