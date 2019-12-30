//Hay que borrar esta linea si desea ejecutarlo en un terminal.
package com.wei.fpro.practicas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Se han considerado los siguientes aspectos conforme a la asignatura de FPRO en la creación de este programa:
 * No se permite el uso de métodos, clases, objectos o estructuras no vistas en clase.
 * No se permite la creación de nuevas clases.
 * No se permite la iteración for each.
 * No se permite la llamada a un método recursivo dentro del "main" que realiza la función del juego. En otras palabras,
 * la lógica del juego debe estar en el "main".
 * <p>
 * Como consecuencia :
 * El código es mucho más largo y tedioso.
 * Puede (casi seguro) que en eficiencia no sea comparable con otro realizado sin la limitación de los aspectos anteriores.
 * (Se ha intentado optimizar a lo máximo dentro de nuestras capacidades)
 *
 * <p>
 * Para otra versión de Java ver : https://github.com/firework0512/FPRO/blob/master/src/com/wei/fpro/practicas/WordStacks.java
 *
 * @author Weihua Weng
 * @author Pablo Varela Vázquez
 */

public class PalabrasApiladas {
    /***
     * Método principal del juego y de la clase.
     * @param args argumentos
     */
    public static void main(String[] args) {
        System.out.println("¿Desea empezar el juego en modo de pruebas?");
        System.out.println("Si es el caso pulse p o P, en otro caso pulse otra tecla.");
        Scanner teclado = new Scanner(System.in);
        String respuesta = teclado.nextLine();
        String[] diccionarioOriginal = seleccionarDiccionario(respuesta);
        boolean debeContinuar = true;
        //La longitud máxima de palabras del diccionario generado
        int longitudPalabras = 10;
        //Archivo donde se guarda la puntuación del usuario
        final File archivoPuntuacion = new File("data.txt");
        //La puntuación guardada
        int puntuacion = obtenerPuntuacion(archivoPuntuacion);
        int cero = 0;
        int uno = 1;
        while (debeContinuar) {
            //El diccionario aleatorio de palabras
            String[] diccionarioAleatorio = diccionarioOriginal;
            //Comprobamos que la longitud del diccionario original sea mayor que la longitud de palabras requerida
            if (diccionarioOriginal.length > longitudPalabras) {
                //Generamos el diccionario aleatorio
                diccionarioAleatorio = obtenerDiccionarioAleatorio(diccionarioOriginal, longitudPalabras);
            }
            //Palabras no encontradas
            String[] noEncontradas;
            //Palabras encontradas del diccionario original pero no del generado
            String[] encontradasOriginal = new String[diccionarioOriginal.length];
            int encontradasOriginalIndice = 0;
            //Palabras encontradas del diccionario generado
            String[] encontradasAleatorio = new String[longitudPalabras];
            int encontradasAleatorioIndice = 0;

            //Empezamos el juego y generamos la matriz
            char[][] matrix = empezarJuego(longitudPalabras, longitudPalabras, diccionarioAleatorio);
            //Palabras legibles de la matriz
            String[] palabrasLegibles = obtenerPalabrasLegibles(matrix, diccionarioAleatorio, encontradasAleatorio);

            while (!esMatrizVacia(matrix)) {
                //Comprobamos si no hay palabras legibles
                if (palabrasLegibles.length == 0) {
                    System.out.println();
                    System.out.println("No hemos podido encontrar palabras legibles en el tablero");
                    System.out.println("Vamos a generar otro tablero con las palabras restantes");
                    //Obtenemos las no encontradas comparando los dos Arrays
                    noEncontradas = obtenerDiferencias(diccionarioAleatorio, encontradasAleatorio);
                    System.out.println(Arrays.toString(noEncontradas));
                    //Empezamos el juego de nuevo con las palabras restantes
                    matrix = empezarJuego(longitudPalabras, longitudPalabras, noEncontradas);
                }
                //Imprimimos la matriz del juego con la puntuacion del usuario
                imprimirMatriz(matrix, puntuacion);
                //Pedimos al usuario la operacion (datos)
                String operacionUsuario = pedirOperacion(teclado);
                //Comprobamos la longitud de la operacion, si es 3 es uno de "LET", "POS", "PAL"
                if (operacionUsuario.length() == 3) {
                    //Obtenemos la respuesta del tipo de pista
                    String pista = obtenerPista(operacionUsuario, puntuacion, palabrasLegibles);
                    System.out.println();
                    //Si la respuesta anterior no es un "NO", entonces mostramos al usuario su pista y le quitamos los puntos.
                    if (!pista.equals("NO")) {
                        switch (operacionUsuario) {
                            case "LET":
                                puntuacion--;
                                System.out.println("La primera letra legible de una palabra legible del tablero es : " + pista);
                                break;
                            case "POS":
                                puntuacion -= 2;
                                System.out.println("La primera posición de una palabra legible del tablero es : " + pista);
                                break;
                            case "PAL":
                                puntuacion -= pista.length();
                                System.out.println("Una palabra legible del tablero es : " + pista);
                                break;
                        }
                    } else {
                        System.out.println("No tienes puntos suficientes!!!");
                        System.out.println("Venga, que esto es muy fácil.");
                    }
                } else if (operacionUsuario.length() == 4) {
                    //Si la longitud de la operacion es 4, entonces el usuario intenta eliminar una palabra de la matriz
                    char filaLetra = operacionUsuario.charAt(0);
                    char columnaLetra = operacionUsuario.charAt(1);
                    char direccionLetra = operacionUsuario.charAt(2);
                    char longitudSeleccionadoLetra = operacionUsuario.charAt(3);

                    //Obtenemos los valores numericos de las letras
                    int fila = obtenerNumero(filaLetra);
                    int columna = obtenerNumero(columnaLetra);
                    int longitudSeleccionado = obtenerNumero(longitudSeleccionadoLetra);
                    //Obtenemos las coordenadas correctas segun la direccion
                    int[] coordenadasCorrectas = obtenerCoordenadasCorrectas(fila, columna, direccionLetra, longitudSeleccionado);
                    int inicio = coordenadasCorrectas[cero];
                    int fin = coordenadasCorrectas[uno];
                    //Obtenemos la palabra seleccionada de la matriz por el usuario
                    String palabraSeleccionada = obtenerPalabraSeleccionada(matrix, fila, columna, inicio, fin, direccionLetra);
                    //Comprobamos que la palabra seleccionada está en el diccionario original y no en el aleatorio
                    if (contieneEnArray(diccionarioOriginal, palabraSeleccionada) && !contieneEnArray(diccionarioAleatorio, palabraSeleccionada)) {
                        //Comprobamos que la palabra seleccionada no está en el diccionario de encontradas
                        if (!contieneEnArray(encontradasOriginal, palabraSeleccionada)) {
                            System.out.println(palabraSeleccionada + " está en el diccionario principal pero no en el diccionario que generó la matriz");
                            puntuacion++;
                            encontradasOriginal[encontradasOriginalIndice] = palabraSeleccionada;
                            encontradasOriginalIndice++;
                        } else {
                            System.out.println("Oye, que ya lo has encontrado");
                            System.out.println("No seas avaricioso");
                        }
                        //Comprobamos que la palabra seleccionada pertenece al diccionario aleatorio y no le hemos encontrado
                    } else if (contieneEnArray(diccionarioAleatorio, palabraSeleccionada) && !contieneEnArray(encontradasAleatorio, palabraSeleccionada)) {
                        System.out.println(palabraSeleccionada + " está en el diccionario que generó la matriz");
                        puntuacion += palabraSeleccionada.length();
                        encontradasAleatorio[encontradasAleatorioIndice] = palabraSeleccionada;
                        encontradasAleatorioIndice++;
                        //Realizamos la operacion del usuario
                        matrix = realizarOperacion(matrix, fila, columna, inicio, direccionLetra, palabraSeleccionada);
                    }
                }
                palabrasLegibles = obtenerPalabrasLegibles(matrix, diccionarioAleatorio, encontradasAleatorio);
            }
            //Escribimos la puntuacion del usuario al archivo
            escribirPuntuacion(archivoPuntuacion, puntuacion);
            System.out.println();
            System.out.println("Has conseguido vaciar el tablero!!!");
            System.out.println("¿Quieres volver a jugar? Si es el caso, introduzca si");
            respuesta = teclado.nextLine();
            //Terminamos el juego si la respuesta del usuario no es un si
            if (!respuesta.toLowerCase().equals("si")) {
                debeContinuar = false;
            }
        }
    }

    /**
     * Método que obtiene las diferencias del {@param primerArray} en {@param segundoArray}
     *
     * @param primerArray  array de datos
     * @param segundoArray array donde se va a comprobar
     * @return un nuevo array con las diferencias
     */
    public static String[] obtenerDiferencias(String[] primerArray, String[] segundoArray) {
        //Longitud maxima del nuevo diccionario
        int longitudMaxima = Math.max(primerArray.length, segundoArray.length);
        //El nuevo diccionario
        String[] nuevo = new String[longitudMaxima];
        int nuevoIndice = 0;
        //Bucle que obtiene las diferencias de los dos arrays y los pone en el nuevo
        for (int elementoIndice = 0; elementoIndice < primerArray.length; elementoIndice++) {
            //Contenido del primer array en una posicion
            String elemento = primerArray[elementoIndice];
            //Si el segundo array no contiene este elemento, lo ponemos el en nuevo
            if (!contieneEnArray(segundoArray, elemento)) {
                nuevo[nuevoIndice] = elemento;
                nuevoIndice++;
            }
        }
        //Obtenemos el nuevo array con su verdadera longitud
        nuevo = obtenerArrayCorregido(nuevo);
        return nuevo;
    }

    /**
     * Método que obtiene el diccionario del juego
     *
     * @param respuesta la respuesta del usuario
     * @return el diccionario
     */
    public static String[] seleccionarDiccionario(String respuesta) {
        final String[] pruebaDiccionario = {
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

        final String[] autorDiccionario = {
                "ACARADO",
                "ACARAR",
                "AGUDOS",
                "ATRABANCO",
                "AZUL",
                "AZULEJO",
                "BANCO",
                "BARRER",
                "BARRERA",
                "BIKINI",
                "BUFEO",
                "CAFETERA",
                "CARA",
                "CARACAS",
                "CARAMELO",
                "CARRERAS",
                "COMENTAR",
                "DICIEMBRE",
                "DOS",
                "DUPLICADO",
                "FAX",
                "FEO",
                "FREGAR",
                "GUITARRA",
                "HOJA",
                "HUIR",
                "IMPORTAR",
                "LADRILLO",
                "LARINGE",
                "MANEJAR",
                "MAR",
                "MAREA",
                "MENTA",
                "NEGATIVO",
                "ONDA",
                "PAN",
                "PANERA",
                "SAL",
                "SALADO",
                "SEGUNDOS",
                "SENTENCIA",
                "VACA",
                "WIFI"
        };
        //Si la respuesta es un "P" o "p" entonces estamos en el modo prueba
        if (respuesta.toLowerCase().equals("p")) {
            return pruebaDiccionario;
        }
        return autorDiccionario;
    }

    /**
     * Metodo que genera un diccionario aleatorio de n = {@param longitud} palabras apartir de una dada
     *
     * @param diccionarioOriginal el diccionario original donde vamos a seleccionar las palabras
     * @param longitud            la longitud de diccionario generado
     * @return el diccionario aleatorio generado
     */
    public static String[] obtenerDiccionarioAleatorio(String[] diccionarioOriginal, int longitud) {
        //El diccionario aleatorio
        String[] diccionarioAleatorio = new String[longitud];
        int indice = 0;
        while (indice < longitud) {
            //Obtenemos un numero aleatorio entre 0 y longitud del diccionario original
            int positionAleatorio = obtenerNumeroAleatorio(0, diccionarioOriginal.length - 1);
            //Obtenemos la palabra correspondiente
            String palabra = diccionarioOriginal[positionAleatorio];
            //Comprobamos que en el diccionario aleatorio no contenga la palabra
            if (!contieneEnArray(diccionarioAleatorio, palabra)) {
                //Ponemos la palabra en el diccionario aleatorio
                diccionarioAleatorio[indice] = palabra;
                indice++;
            }
        }
        return diccionarioAleatorio;
    }

    /**
     * Metodo que genera un número aleatorio entre {@param origin} y {@param bound} inclusives
     *
     * @param origin el numero inicial inclusive
     * @param bound  the numero final inclusive
     * @return el numero aleatorio
     */
    public static int obtenerNumeroAleatorio(int origin, int bound) {
        int uno = 1;
        return (int) (Math.random() * (bound - origin + uno)) + origin;
    }


    /**
     * Método que genera un Boolean aleatorio
     *
     * @return el Boolean aleatorio
     */
    public static boolean obtenerBooleanAleatorio() {
        int zero = 0;
        int uno = 1;
        return obtenerNumeroAleatorio(zero, uno) == zero;
    }

    /**
     * Metodo que escribe la puntuacion del usuario en un archivo local
     *
     * @param archivo    el archivo donde se va a escribir
     * @param puntuacion la puntuacion alcanzada
     */
    public static void escribirPuntuacion(File archivo, int puntuacion) {
        PrintWriter escritura;
        try {
            //Abrimos escritura
            escritura = new PrintWriter(archivo);
            //Escribimos la puntuacion
            escritura.write("Última puntuación : " + puntuacion);
            escritura.flush();
            escritura.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al escribir la puntuacion : " + e.getMessage());
        }
    }

    /**
     * Metodo que lee la ultima puntuacion almacenada localmente
     *
     * @param archivo el archivo donde se va a leer
     * @return la puntuacion del usuario
     */
    public static int obtenerPuntuacion(File archivo) {
        //Puntuacion por defecto
        int puntos = 10;
        //Comprobamos que existe el archivo
        if (archivo.exists())
            try {
                //Abrimos la lectura del archivo
                Scanner scanner = new Scanner(archivo);
                //Usamos el delimitador
                scanner.useDelimiter(":");
                //Empezamos la lectura
                while (scanner.hasNext()) {
                    String palabra = scanner.next();
                    palabra = palabra.trim();
                    //Comprobamos que "palabra" esta formada por numeros con la primera letra
                    char primeraLetra = palabra.charAt(0);
                    int antesCeroAscii = 47;
                    int despuesNueveAscii = 58;
                    if (primeraLetra > antesCeroAscii && primeraLetra < despuesNueveAscii) {
                        puntos = Integer.parseInt(palabra);
                    }
                }

            } catch (FileNotFoundException e) {
                //Esta excepcion en teoria no se va a producir nunca ya que previamente hemos comprobado la existencia del archivo
                e.printStackTrace();
                System.out.println("Error al escribir en el archivo : archivo no encontrado");
            }
        return puntos;
    }


    /**
     * Metodo que resetea la puntuacion del usuario al defecto
     *
     * @param file el {@link File} archivo donde hemos guardado previamente la puntuacion
     * @return true si conseguimos eliminar el archivo, false si no.
     */
    public static boolean resetearPuntuacion(File file) {
        return file.delete();
    }

    /**
     * Metodo que empieza el juego
     *
     * @param filas       numero de filas de la matriz
     * @param columnas    numero de columnas de la matriz
     * @param diccionario el diccionario de la matriz
     * @return la matriz
     */
    public static char[][] empezarJuego(int filas, int columnas, String[] diccionario) {
        //Generamos la matriz del juego
        char[][] matriz = generarMatriz(filas, columnas, diccionario);
        //Hacemos la gravedad
        gravedad(matriz);
        return matriz;
    }

    /**
     * Método que genera la matriz del juego
     *
     * @param filas       filas de la matriz
     * @param columnas    columnas de la matriz
     * @param diccionario el diccionario de la matriz
     * @return la matriz generada
     */
    public static char[][] generarMatriz(int filas, int columnas, String[] diccionario) {
        //La matriz del juego
        char[][] matriz = new char[filas][columnas];
        int cero = 0;
        boolean booleanAleatorio;
        //Rellenamos la matriz con espacios
        rellenarEspaciosMatriz(matriz);
        //Bucle que inserta cada palabra del diccionario
        for (int i = 0; i < diccionario.length; i++) {
            String element = diccionario[i];
            //Obtenemos un boolean aleatorio
            booleanAleatorio = obtenerBooleanAleatorio();
            char tipo;
            if (booleanAleatorio) {
                //lo insertamos por filas
                tipo = 'F';
            } else {
                //Lo insertamos por columnas
                tipo = 'C';
            }
            insertarEnMatriz(matriz, element, tipo, cero);
        }
        return matriz;
    }

    /**
     * Metodo que inserta una palabra en la matriz
     *
     * @param matriz      matriz donde vamos a insertar
     * @param palabra     palabra que vamos a insertar
     * @param tipo        puede ser "F" por filas o "C" por columnas
     * @param cuentaError cuenta de errores
     */
    public static void insertarEnMatriz(char[][] matriz, String palabra, char tipo, int cuentaError) {
        int cero = 0;
        int uno = 1;
        if (cuentaError == 2) {
            //No es posible insertar la palabra completa horizontalmente ni verticalmente
            //Comprobamos que si es posible insertar la palabra por trozos verticalmente

            //Cuenta de espacios
            int espacios = 0;
            //Bucle que comprueba que hay espacios suficientes
            for (int columnaIndice = 0; columnaIndice < matriz[0].length; columnaIndice++) {
                String palabraColumna = obtenerPalabraColumna(matriz, columnaIndice);
                int[] posicionesEspacios = obtenerPosicionEspaciosString(palabraColumna);
                if (posicionesEspacios.length != 0) {
                    espacios += posicionesEspacios.length;
                }
            }
            //Contador de la posicion de letras de la palabra al insertar
            int letraIndice = cero;

            //Comprobamos que hay espacios suficientes
            if (espacios >= palabra.length()) {
                for (int columnaIndice = 0; columnaIndice < matriz[0].length; columnaIndice++) {
                    //Palabra de la columna
                    String palabraColumna = obtenerPalabraColumna(matriz, columnaIndice);
                    //Array de posiciones en blanco
                    int[] posicionesEspacios = obtenerPosicionEspaciosString(palabraColumna);
                    //Tamaño del array
                    int longitud = posicionesEspacios.length;

                    if (longitud != cero) {
                        //Bucle que escribe la letra correspondiente en la matriz
                        for (int posicionIndice = cero; posicionIndice < longitud; posicionIndice++) {
                            int posicionEspacio = posicionesEspacios[posicionIndice];
                            matriz[posicionEspacio][columnaIndice] = palabra.charAt(letraIndice);
                            letraIndice++;
                        }
                    }
                }
            } else {
                cuentaError++;
                insertarEnMatriz(matriz, palabra, tipo, cuentaError);
            }
        } else if (cuentaError > 2) {
            System.out.println("No es posible insertar más palabras en la matriz, prueba con otra matriz más grande\n + Fallo al insertar : " + palabra);
        }
        //Obtenemos un boolean aleatorio
        boolean necesitaInvertir = obtenerBooleanAleatorio();
        //Si el boolean es true invertimos la palabra
        if (necesitaInvertir) {
            palabra = invertirString(palabra);
        }
        //Insertamos horizontalmente (por fila)
        if (tipo == 'F') {
            int espacios = 0;
            //Bucle que comprueba que hay espacios suficientes
            for (int columnaIndice = 0; columnaIndice < matriz[0].length; columnaIndice++) {
                boolean posibleInsertar = esPosibleInsertarPalabraColumna(matriz, columnaIndice, uno);
                if (posibleInsertar) {
                    espacios++;
                }
            }
            //Comprobamos que hay espacios suficientes
            if (espacios >= palabra.length()) {
                int columnaIndice = 0;
                int indicePalabra = 0;
                while (indicePalabra < palabra.length()) {
                    boolean posibleInsertar = esPosibleInsertarPalabraColumna(matriz, columnaIndice, uno);
                    //Comprobamos que es posible insertar en la columna
                    if (posibleInsertar) {
                        //Palabra de la columna
                        String palabraColumna = obtenerPalabraColumna(matriz, columnaIndice);
                        //Posiciones de espacios en blanco
                        int[] posicionesEspacios = obtenerPosicionEspaciosString(palabraColumna);
                        int ultimoIndice = posicionesEspacios.length - 1;
                        //Posicion en blanco == fila
                        int fila = posicionesEspacios[ultimoIndice];
                        //Escribimos la letra correspondiente en la matriz
                        matriz[fila][columnaIndice] = palabra.charAt(indicePalabra);
                        indicePalabra++;
                    }
                    columnaIndice++;
                }
            } else {
                //No es posible insertar horizontalmente, lo probamos verticalmente
                //Incrementamos tambien el contador de errores
                cuentaError++;
                insertarEnMatriz(matriz, palabra, 'C', cuentaError);
            }

        } else if (tipo == 'C') {
            boolean posibleInsertar = false;
            int columnaIndice = 0;
            int indice = columnaIndice;
            //Bucle que comprueba que es posible insertar la palabra en uno de las columnas
            while (indice < matriz[0].length) {
                posibleInsertar = esPosibleInsertarPalabraColumna(matriz, indice, palabra.length());
                if (posibleInsertar) {
                    //Indice de columna donde es posible insertar la palabra
                    columnaIndice = indice;
                    //Salimos del bucle manualmente
                    indice = matriz[0].length;
                }
                indice++;
            }
            if (posibleInsertar) {
                int letraIndice = 0;
                while (letraIndice < palabra.length()) {
                    //Palabra de la columna
                    String palabraColumna = obtenerPalabraColumna(matriz, columnaIndice);
                    //Posiciones de espacios en blanco
                    int[] posicionesEspacios = obtenerPosicionEspaciosString(palabraColumna);
                    //Posicion en blanco == fila
                    int fila = posicionesEspacios[0];
                    //Escribimos la letra correspondiente en la matriz
                    matriz[fila][columnaIndice] = palabra.charAt(letraIndice);
                    letraIndice++;
                }
            } else {
                //No es posible insertar verticalmente, lo probamos horizontalmente
                //Incrementamos tambien el contador de errores
                cuentaError++;
                insertarEnMatriz(matriz, palabra, 'R', cuentaError);
            }
        }
    }

    /**
     * Metodo que la operacion de gravedad a una matriz dada
     *
     * @param matriz matriz donde vamos a realizar la gravedad
     */
    public static void gravedad(char[][] matriz) {
        int cero = 0;
        for (int columnaIndice = 0; columnaIndice < matriz[0].length; columnaIndice++) {
            //Palabra de la columna
            String palabraColumna = obtenerPalabraColumna(matriz, columnaIndice);
            //Posiciones de espacios en blanco
            int[] posicionesEspacios = obtenerPosicionEspaciosString(palabraColumna);
            if (posicionesEspacios.length > cero) {
                //Palabra de la columna sin espacios
                String palabraColumnaSinEspacios = palabraColumna.replace(" ", "");
                //Posicion en el que debe empezar la palabra
                int posicionDebeEmpezar = matriz[cero].length - palabraColumnaSinEspacios.length();
                //Palabra con espacios
                String palabraEspacios = generarStringEspacios(posicionDebeEmpezar);
                //Rellenamos con espacios desde la posicion inicial = 0 hasta posicionDebeEmpezar
                insertarPalabraColumnaDesdePosicion(matriz, cero, columnaIndice, palabraEspacios);
                //Rellenamos la palabra de la columna sin espacios desde posicionDebeEmpezar
                insertarPalabraColumnaDesdePosicion(matriz, posicionDebeEmpezar, columnaIndice, palabraColumnaSinEspacios);
            }
        }
    }

    /**
     * Metodo que compacta la matriz
     *
     * @param matriz la matriz al que vamos a compactar
     */
    public static void compactar(char[][] matriz) {
        int cero = 0;
        int columnas = matriz[cero].length;
        int totalColumnasNoVacias = obtenerTotalColumnasNoVacias(matriz);
        //Columnas no vacias de la matriz
        int[] columnasNoVacias = new int[totalColumnasNoVacias];
        //Contador del array
        int contador = cero;
        //Bucle que obtiene las columnas no vacias de la matriz
        for (int indiceColumna = cero; indiceColumna < columnas; indiceColumna++) {
            //Comprobamos que la columna no sea vacia
            if (esColumnaNoVacia(matriz, indiceColumna)) {
                //Lo añadimos al array
                columnasNoVacias[contador] = indiceColumna;
                contador++;
            }
        }
        int longitud = columnasNoVacias.length;
        //Hacemos que el contador sea 0 de nuevo
        contador = cero;
        //Bucle que compacta la matriz
        for (int columnaIndice = 0; columnaIndice < columnas; columnaIndice++) {
            //Comprobamos que el indice del bucle sea inferior a la longitud del array
            if (columnaIndice < longitud) {
                //Obtenemos el indice de una columna no vacia
                int indiceNoVacia = columnasNoVacias[contador];
                //Comprobamos que el indice de la columna no vacia no sea el del bucle
                if (columnaIndice != indiceNoVacia) {
                    //Palabra de la columna
                    String palabraColumna = obtenerPalabraColumna(matriz, indiceNoVacia);
                    //Insertamos la palabra de la columna al indice de columna correspondiente
                    insertarPalabraColumnaDesdePosicion(matriz, cero, columnaIndice, palabraColumna);
                }
                contador++;
            } else {
                //Comprobamos que la columna no sea vacia
                if (esColumnaNoVacia(matriz, columnaIndice)) {
                    //Palabra con tantos espacios como columnas
                    String palabraEspacios = generarStringEspacios(columnas);
                    //Insertamos la palabra de la columna al indice de columna correspondiente
                    insertarPalabraColumnaDesdePosicion(matriz, cero, columnaIndice, palabraEspacios);
                }
            }
        }
    }

    /**
     * Metodo que obtiene el numero total de columnas no vacias de una matriz
     *
     * @param matrix la matriz al que vamos a comprobar
     * @return el numero total de columnas no vacias
     */
    public static int obtenerTotalColumnasNoVacias(char[][] matrix) {
        int cero = 0;
        int contador = 0;
        for (int columnIndex = 0; columnIndex < matrix[cero].length; columnIndex++) {
            boolean noEsVacia = esColumnaNoVacia(matrix, columnIndex);
            if (noEsVacia) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Metodo que comprueba si no es vacia la {@param columnaIndice} columna de la matriz
     *
     * @param matriz        la matriz al que vamos a comprobar
     * @param columnaIndice el indice de la columna
     * @return true si la columna esta vacia (solo espacios), otro caso false
     */
    public static boolean esColumnaNoVacia(char[][] matriz, int columnaIndice) {
        int cero = 0;
        boolean esNoVacia = true;
        String palabraColumna = obtenerPalabraColumna(matriz, columnaIndice);
        int espacios = obtenerTotalEspacios(palabraColumna);
        if (espacios == matriz[cero].length) esNoVacia = false;
        return esNoVacia;
    }

    /**
     * Metodo que comprueba si un String Array {@param array} contains un determinado String
     *
     * @param array   el array al que vamos a comprobar
     * @param palabra el string determinado
     * @return true si el array contiene el string, otro caso false
     */
    public static boolean contieneEnArray(String[] array, String palabra) {
        boolean resultado = false;
        int cero = 0;
        //Comprobamos que tamaño del array sea mayor que cero
        if (array.length > cero) {
            //Contador del array
            int contador = cero;
            while (contador < array.length) {
                //Elemento del array en una posicion determinada
                String elemento = array[contador];
                //Comprobamos que el elemento no sea null
                if (elemento != null) {
                    //Comprobamos que el elemento sea igual a la palabra
                    if (elemento.equals(palabra)) {
                        resultado = true;
                        //Salimos del bucle manualmente
                        contador = array.length;
                    }
                }
                contador++;
            }
        }
        return resultado;
    }

    /**
     * Metodo que obtiene todas las palabras legibles de una matriz
     *
     * @param matriz      la matriz al que vamos a comprobar
     * @param diccionario el diccionario de la matriz
     * @param encontrados array de palabras encontradas
     * @return array de palabras legibles de la matriz con sus respectivas posiciones
     */
    public static String[] obtenerPalabrasLegibles(char[][] matriz, String[] diccionario, String[] encontrados) {
        //Array de Strings que va a contener las palabras legibles de la matriz
        //Su dimensión será el doble del tamaño del diccionario.
        int longitudMaxima = diccionario.length * 2;
        //Array de palabras legibles
        String[] palabrasLegibles = new String[longitudMaxima];
        //Rellenamos el array con strings para que no sea nula
        for (int i = 0; i < palabrasLegibles.length; i++) {
            palabrasLegibles[i] = "";
        }
        int cero = 0;
        int uno = 1;
        //Contador de palabras legibles
        int contador = 0;
        //Bucle que obtienes las palabras legibles por filas
        for (int filaIndice = 0; filaIndice < matriz.length; filaIndice++) {
            String palabraFila = obtenerPalabraFila(matriz, filaIndice);
            //Bucle que comprueba si hay una palabra del diccionario legible
            for (int elementoIndice = 0; elementoIndice < diccionario.length; elementoIndice++) {
                //El elemento del array
                String elemento = diccionario[elementoIndice];
                //Elemento pero inversa
                String inversa = invertirString(elemento);
                //La palabra completa (el elemento con sus respectivas posiciones)
                String palabra;
                int columnaIndice;
                //Comprobamos si hay una palabra legible del diccionario en la fila de la matriz
                //Y que el usuario no lo haya encontrado
                if (palabraFila.contains(elemento) && !contieneEnArray(encontrados, elemento)) {
                    //Obtenemos la posicion correcta
                    columnaIndice = palabraFila.indexOf(elemento);
                    palabra = "(" + filaIndice + "," + columnaIndice + ")" + elemento;
                    //Comprobamos que esta palabra legible no lo hayamos añadido antes
                    if (!contieneEnArray(palabrasLegibles, palabra)) {
                        palabrasLegibles[contador] += palabra;
                        contador++;
                    }
                    //Comprobamos si hay una palabra legible del diccionario de forma inversa en la fila de la matriz
                    //Y que el usuario no lo haya encontrado
                } else if (palabraFila.contains(inversa) && !contieneEnArray(encontrados, elemento)) {
                    //Obtenemos la posicion correcta
                    columnaIndice = palabraFila.indexOf(inversa) + inversa.length() - uno;
                    palabra = "(" + filaIndice + "," + columnaIndice + ")" + elemento;
                    //Comprobamos que esta palabra legible no lo hayamos añadido antes
                    if (!contieneEnArray(palabrasLegibles, palabra)) {
                        palabrasLegibles[contador] += palabra;
                        contador++;
                    }
                }
            }
        }
        //Bucle que obtienes las palabras legibles por columnas
        for (int columnaIndice = 0; columnaIndice < matriz[cero].length; columnaIndice++) {
            String palabraColumna = obtenerPalabraColumna(matriz, columnaIndice);
            //Bucle que comprueba si hay una palabra del diccionario legible
            for (int i = 0; i < diccionario.length; i++) {
                //El elemento del array
                String elemento = diccionario[i];
                //Elemento pero inversa
                String inversa = invertirString(elemento);
                //La palabra completa (el elemento con sus respectivas posiciones)
                String palabra;
                int filaIndice;
                //Comprobamos si hay una palabra legible del diccionario en la columna de la matriz
                //Y que el usuario no lo haya encontrado
                if (palabraColumna.contains(elemento) && !contieneEnArray(encontrados, elemento)) {
                    //Obtenemos la posicion correcta
                    filaIndice = palabraColumna.indexOf(elemento);
                    palabra = "(" + filaIndice + "," + columnaIndice + ")" + elemento;
                    //Comprobamos que esta palabra legible no lo hayamos añadido antes
                    if (!contieneEnArray(palabrasLegibles, palabra)) {
                        palabrasLegibles[contador] += palabra;
                        contador++;
                    }
                    //Comprobamos si hay una palabra legible del diccionario de forma inversa en la columna de la matriz
                    //Y que el usuario no lo haya encontrado
                } else if (palabraColumna.contains(inversa) && !contieneEnArray(encontrados, elemento)) {
                    //Obtenemos la posicion correcta
                    filaIndice = palabraColumna.indexOf(inversa) + inversa.length() - uno;
                    palabra = "(" + filaIndice + "," + columnaIndice + ")" + elemento;
                    //Comprobamos que esta palabra legible no lo hayamos añadido antes
                    if (!contieneEnArray(palabrasLegibles, palabra)) {
                        palabrasLegibles[contador] += palabra;
                        contador++;
                    }
                }
            }
        }
        //Obtenemos un nuevo Array con el tamaño correcto y sin espacios
        palabrasLegibles = obtenerArrayCorregido(palabrasLegibles);
        return palabrasLegibles;
    }

    /**
     * Metodo que crea otro String Array basado en en antiguo con el tamaño correcto y sin espacios
     *
     * @param oldArray el array que vamos a corregir
     * @return el nuevo String Array con el tamaño correcto y sin espacios
     */
    public static String[] obtenerArrayCorregido(String[] oldArray) {
        int contador = 0;
        //Bucle que obtiene el tamaño correcto del array
        for (int i = 0; i < oldArray.length; i++) {
            String elemento = oldArray[i];
            if (elemento != null && !elemento.isEmpty()) {
                contador++;
            }
        }
        //El nuevo array
        String[] newArray = new String[contador];
        int guardadoIndice = 0;
        int elementoIndice = 0;
        //Bucle que copia los elementos no nulos y que no sean espacios en un nuevo array
        while (elementoIndice < contador) {
            String elementoViejo = oldArray[guardadoIndice];
            if (elementoViejo != null && !elementoViejo.isEmpty()) {
                newArray[elementoIndice] = oldArray[guardadoIndice];
                elementoIndice++;
            }
            guardadoIndice++;
        }
        return newArray;
    }

    /**
     * Metodo que comprueba si un caracter es igual a uno de los elementos de un char Array
     *
     * @param caracter   el caracter que deseamos comprobar
     * @param caracteres el conjunto de caracteres del que vamos a comprobar
     * @return true si es igual a uno del conjunto, en otro caso false
     */
    public static boolean esIgualAUno(char caracter, char[] caracteres) {
        boolean resultado = false;
        int contador = 0;
        while (contador < caracteres.length) {
            if (caracter == caracteres[contador]) {
                resultado = true;
                //Salimos del bucle manualmente
                contador = caracteres.length;
            }
            contador++;
        }
        return resultado;
    }

    /**
     * Metodo que comprueba si un String es igual a uno de los elementos de un String Array
     *
     * @param palabra  el String que deseamos comprobar
     * @param palabras el conjunto de Strings del que vamos a comprobar
     * @return true si es igual a uno del conjunto, en otro caso false
     */
    public static boolean esIgualAUno(String palabra, String[] palabras) {
        boolean resultado = false;
        int contador = 0;
        while (contador < palabras.length) {
            String correctWord = palabras[contador];
            if (palabra.equals(correctWord)) {
                resultado = true;
                //Salimos del bucle manualmente
                contador = palabras.length;
            }
            contador++;
        }
        return resultado;
    }

    /**
     * Metodo que comprueba si todos los elementos de un boolean Array son true
     *
     * @param booleans el boolean Array que deseamos comprobar
     * @return true si son todos true, en otro caso false
     */
    public static boolean sonCorrectas(boolean[] booleans) {
        boolean resultado = true;
        int contador = 0;
        while (contador < booleans.length) {
            boolean condition = booleans[contador];
            if (!condition) {
                resultado = false;
                //Salimos del bucle manualmente
                contador = booleans.length;
            }
            contador++;
        }
        return resultado;
    }

    /**
     * Metodo que comprueba si el usuario ha introducido una operacion correcta
     * <p>
     * El formato de una operacion correcta es la siguiente :
     * Formado por 3 letras:
     * Debe ser uno de "LET", "POS", "PAL".
     * Formado por 4 letras:
     * Primera letra : numero entre 0 y 9
     * Segunda letra : numero entre 0 y 9
     * Tercera letra : Debe ser uno de 'N', 'S', 'E', 'O'.
     * Cuarta letra : numero entre 0 y 9
     *
     * @param operacion la operacion introducida por el usuario
     * @return true si la operacion es correcta, en otro caso false.
     */
    public static boolean esCorrectaOperacion(String operacion) {
        boolean resultado = false;
        //Comprobamos que la operacion sea de 3 o 4 caracteres
        if (operacion.length() == 3) {
            String[] pistasCorrectas = new String[]{"LET", "POS", "PAL"};
            resultado = esIgualAUno(operacion, pistasCorrectas);
        } else if (operacion.length() == 4) {
            char filaLetra = operacion.charAt(0);
            char columnaLetra = operacion.charAt(1);
            char direccionLetra = operacion.charAt(2);
            char longitudLetra = operacion.charAt(3);
            //PS : 0 in ASCII corresponds to 48
            //PS : 9 in ASCII corresponds to 57
            int antesCeroAscii = 47;
            int despuesNueveAscii = 58;
            char[] direccionesCorrectas = new char[]{'N', 'S', 'O', 'E'};
            //Comprobamos todas las condiciones a la vez
            boolean[] multiplesCondiciones = new boolean[]{
                    filaLetra > antesCeroAscii && filaLetra < despuesNueveAscii,
                    columnaLetra > antesCeroAscii && columnaLetra < despuesNueveAscii,
                    esIgualAUno(direccionLetra, direccionesCorrectas),
                    longitudLetra > antesCeroAscii && longitudLetra < despuesNueveAscii};
            resultado = sonCorrectas(multiplesCondiciones);
            //Si el formato es correcto, comprobamos si el rango de la operacion sea correcta
            if (resultado) {
                int fila = obtenerNumero(filaLetra);
                int columna = obtenerNumero(columnaLetra);
                int longitud = obtenerNumero(longitudLetra);
                int uno = 1;
                int indiceMinimo = 0;
                int indiceMaximo = 9;
                //Comprobamos el rango de la operacion segun la direccion
                switch (direccionLetra) {
                    case 'N':
                        resultado = fila - longitud + uno >= indiceMinimo;
                        break;
                    case 'S':
                        resultado = fila + longitud - uno <= indiceMaximo;
                        break;
                    case 'O':
                        resultado = columna - longitud + uno >= indiceMinimo;
                        break;
                    case 'E':
                        resultado = columna + longitud - uno <= indiceMaximo;
                        break;
                }
            }
        }
        return resultado;
    }

    /**
     * Metodo que pide los datos al usuario
     *
     * @param teclado el objecto {@link Scanner}
     * @return los datos
     */
    public static String pedirOperacion(Scanner teclado) {
        System.out.println("Introduzca las coordenadas");
        System.out.println("Si desea obtener una pista, introduzca LET, POS O PAL");
        String operacion = teclado.nextLine();
        //Bucle que pide los datos hasta que sea el formato correcto
        while (!esCorrectaOperacion(operacion)) {
            System.out.println("Introduzca las coordenadas correctas");
            System.out.println("Si desea obtener una pista, introduzca LET, POS O PAL");
            operacion = teclado.nextLine();
        }
        return operacion;
    }


    /**
     * Metdo que obtiene las coordenadas correctas para eliminar posteriormente las palabras
     *
     * @param fila                 la fila seleccionada por el usuario
     * @param columna              la columna seleccionada por el usuario
     * @param direccion            la direccion seleccionada por el usuario
     * @param longitudSeleccionado la longitud seleccionada por el usuario
     * @return el int Array con la coordenadas correctas
     */
    public static int[] obtenerCoordenadasCorrectas(int fila, int columna, char direccion, int longitudSeleccionado) {
        int inicio = 0;
        int fin = 0;
        int uno = 1;
        switch (direccion) {
            case 'N':
                inicio = fila - longitudSeleccionado + uno;
                fin = fila + 1;
                break;
            case 'S':
                inicio = fila;
                fin = fila + longitudSeleccionado;
                break;
            case 'E':
                inicio = columna;
                fin = columna + longitudSeleccionado;
                break;
            case 'O':
                inicio = columna - longitudSeleccionado + uno;
                fin = columna + 1;
                break;
        }
        int[] coordenadasCorrectas = new int[]{inicio, fin};
        return coordenadasCorrectas;
    }

    /**
     * Metodo que obtiene la palabra seleccionada por el usuario de la matriz
     *
     * @param matriz    la matriz al que le vamos a extraer la palabra seleccionada
     * @param fila      la fila seleccionada por el usuario
     * @param columna   la columna seleccionada por el usuario
     * @param inicio    la coordenada correcta del inicio
     * @param fin       la coordenada correcta del final
     * @param direccion la longitud seleccionada por el usuario
     * @return la palabra seleccionada
     */
    public static String obtenerPalabraSeleccionada(char[][] matriz, int fila, int columna, int inicio, int fin, char direccion) {
        String palabraMatriz = "";
        //Obtenemos la palabra de la matriz segun direccion
        switch (direccion) {
            case 'N':
            case 'S':
                palabraMatriz = obtenerPalabraColumna(matriz, columna);
                break;
            case 'E':
            case 'O':
                palabraMatriz = obtenerPalabraFila(matriz, fila);
                break;
        }
        //Obtenemos la palabra seleccionada
        String palabraSeleccionada = palabraMatriz.substring(inicio, fin);
        //Invertimos si hace falta
        if (direccion == 'O' || direccion == 'N') palabraSeleccionada = invertirString(palabraSeleccionada);
        return palabraSeleccionada;
    }

    /**
     * Metodo que obtiene una pista
     * <p>
     * Observaciones : debe haber palabras legibles en la matriz
     *
     * @param tipoPista        el tipo de pista pedida por el usuario
     * @param puntuacion       la puntuacion del usuario
     * @param palabrasLegibles las palabras legibles de la matriz
     * @return la pista pedida
     */
    public static String obtenerPista(String tipoPista, int puntuacion, String[] palabrasLegibles) {
        String pista = "NO";
        int cero = 0;
        int uno = 1;
        switch (tipoPista) {
            case "LET":
                if (puntuacion >= 1) {
                    //Borramos las posiciones del array, solo nos quedamos con las palabras legibles
                    eliminarStringEspecifico(palabrasLegibles);
                    int longitud = palabrasLegibles.length;
                    int ultimaPosicion = longitud - uno;
                    //Obtenemos una pista aleatoria
                    int posicionAleatoria = obtenerNumeroAleatorio(cero, ultimaPosicion);
                    pista = palabrasLegibles[posicionAleatoria];
                    //Obtenemos la primera letra de la palabra legible
                    pista = String.valueOf(pista.charAt(cero));
                }
                break;
            case "POS":
                if (puntuacion >= 2) {
                    int longitud = palabrasLegibles.length;
                    int ultimaPosicion = longitud - uno;
                    //Obtenemos una pista aleatoria
                    int posicionAleatoria = obtenerNumeroAleatorio(cero, ultimaPosicion);
                    pista = palabrasLegibles[posicionAleatoria];
                    int inicio = pista.indexOf("(");
                    int fin = pista.indexOf(")") + uno;
                    //Obtenemos la posicion de la palabra legible
                    pista = pista.substring(inicio, fin);
                }
                break;
            case "PAL":
                //Borramos las posiciones del array, solo nos quedamos con las palabras legibles
                eliminarStringEspecifico(palabrasLegibles);
                //Comprobamos que haya una palabra legible y que el usuario tenga los puntos suficientes
                boolean hayPuntos = hayElementoLongitud(palabrasLegibles, puntuacion);
                if (hayPuntos) {
                    int contador = cero;
                    int longitud = palabrasLegibles.length;
                    while (contador < longitud) {
                        String elemento = palabrasLegibles[contador];
                        if (elemento != null && elemento.length() <= puntuacion) {
                            pista = elemento;
                            //Salimos del bucle manualmente
                            contador = longitud;
                        }
                        contador++;
                    }
                }
                break;
        }
        return pista;
    }


    /**
     * Metodo que obtiene las palabras legibles sin sus respectivas posiciones
     * <p>
     * Observaciones : solo funciona con el array de palabras legibles que hemos obtenido con {@link PalabrasApiladas#obtenerPalabrasLegibles}
     *
     * @param palabrasLegibles el palabrasLegibles que deseamos borrar
     */
    public static void eliminarStringEspecifico(String[] palabrasLegibles) {
        for (int i = 0; i < palabrasLegibles.length; i++) {
            String elemento = palabrasLegibles[i];
            int inicio = elemento.indexOf(")") + 1;
            int fin = elemento.length();
            palabrasLegibles[i] = elemento.substring(inicio, fin);
        }
    }

    /**
     * Metodo que comprueba si un  String Array contiene un elemento menor o igual que  n = {@param longitud} de longitud
     *
     * @param array    el String array que vamos a comprobar
     * @param longitud la longitud deseada
     * @return true si hay un elemento de tal longitud, en otro caso false
     */
    public static boolean hayElementoLongitud(String[] array, int longitud) {
        boolean resultado = false;
        int elementoIndice = 0;
        while (elementoIndice < array.length) {
            String elemento = array[elementoIndice];
            //Comprobamos que el elemento exista y que sea menor o igual a la longitud deseada
            if (elemento != null && elemento.length() <= longitud) {
                resultado = true;
                //Salimos del bucle manualmente
                elementoIndice = array.length;
            }
            elementoIndice++;
        }
        return resultado;
    }

    /**
     * Metodo que realiza la operacion introducida del usuario (eliminar la palabra seleccionada) de una matriz
     * <p>
     * Observaciones : fila y columna deben de estar en el rango de la matriz
     *
     * @param matrix              la matriz al que le vamos a borrar la palabra
     * @param fila                la fila seleccionada por el usuario
     * @param columna             la columna seleccionada por el usuario
     * @param filaCorrecta        la fila correcta corregida
     * @param direccion           la direccion seleccionada por el usuario
     * @param palabraSeleccionada la palabra seleccionada por el usuario
     * @return la matriz despues de realizar la operacion de borrado
     */
    public static char[][] realizarOperacion(char[][] matrix, int fila, int columna, int filaCorrecta, char direccion, String palabraSeleccionada) {
        //Obtenemos un String de tantos espacios como longitud de la palabra seleccionada
        String espacios = generarStringEspacios(palabraSeleccionada.length());
        switch (direccion) {
            case 'N':
            case 'S':
                insertarPalabraColumnaDesdePosicion(matrix, filaCorrecta, columna, espacios);
                break;
            case 'E':
            case 'O':
                insertarPalabraFilaDesdePosicion(matrix, fila, filaCorrecta, espacios);
                break;
        }
        gravedad(matrix);
        compactar(matrix);
        return matrix;
    }

    /**
     * Metodo que inserta una palabra desde una posicion de una fila de una matriz
     * <p>
     * Observaciones : fila y columna deben de estar en el rango de la matriz
     *
     * @param matriz  la matriz que vamos a insertar
     * @param fila    la fila de la matriz que vamos a insertar
     * @param columna la columna o posicion desde que vamos a insertar
     * @param palabra la palabra que vamos a insertar
     */
    public static void insertarPalabraFilaDesdePosicion(char[][] matriz, int fila, int columna, String palabra) {
        for (int i = 0; i < palabra.length(); i++) {
            matriz[fila][columna] = palabra.charAt(i);
            columna++;
        }
    }

    /**
     * Metodo que inserta una palabra desde una posicion de una columna de una matriz
     * <p>
     * Observaciones : fila y columna deben de estar en el rango de la matriz
     *
     * @param matriz  la matriz que vamos a insertar
     * @param fila    la fila o posicion desde que empezamos a insertar
     * @param columna la columna de la matriz que vamos a insertar
     * @param palabra la palabra que vamos a insertar
     */
    public static void insertarPalabraColumnaDesdePosicion(char[][] matriz, int fila, int columna, String palabra) {
        for (int i = 0; i < palabra.length(); i++) {
            matriz[fila][columna] = palabra.charAt(i);
            fila++;
        }
    }

    /**
     * Metodo que comprueba si es posible insertar una palabra en una columna de una matriz
     *
     * @param matriz   la matriz que vamos a comprobar
     * @param columna  la columna de la matriz donde deseamos insertar
     * @param espacios los espacios necesitados
     * @return true si es posible, en otro caso false
     */
    public static boolean esPosibleInsertarPalabraColumna(char[][] matriz, int columna, int espacios) {
        boolean resultado = false;
        String palabraColumna = obtenerPalabraColumna(matriz, columna);
        if (obtenerTotalEspacios(palabraColumna) >= espacios) {
            resultado = true;
        }
        return resultado;
    }

    /**
     * Metodo que obtiene las posiciones de espacios en blanco de un String
     *
     * @param palabra el String que deseamos obtener sus posiciones
     * @return el int Array con las posiciones de espacios
     */
    public static int[] obtenerPosicionEspaciosString(String palabra) {
        int menosUno = -1;
        int uno = 1;
        int totalEspacios = obtenerTotalEspacios(palabra);
        int[] posicionesEspacios = new int[totalEspacios];
        if (totalEspacios > 0) {
            posicionesEspacios = new int[totalEspacios];
            //Obtenemos la primera posicion del espacio
            int posicionEspacio = palabra.indexOf(" ");
            //Ultima posicion guardada
            int posicionCache = posicionEspacio;
            int contadorArray = 0;
            //Bucle que obtiene las posiciones de espacios
            while ((posicionEspacio = palabra.indexOf(" ", posicionCache)) != menosUno) {
                posicionCache = posicionEspacio + uno;
                posicionesEspacios[contadorArray] = posicionEspacio;
                contadorArray++;
            }
        }
        return posicionesEspacios;
    }

    /**
     * Metodo que convierte la columna {@param columna} de la matriz {@param matriz} a un String
     * <p>
     * Observaciones : la columna debe estar dentro del rango de la matriz
     *
     * @param columna la columna de la matriz
     * @return el String de la columna de la matriz
     */
    public static String obtenerPalabraColumna(char[][] matriz, int columna) {
        String palabraColumna = "";
        for (int filaIndice = 0; filaIndice < matriz.length; filaIndice++) {
            char letter = matriz[filaIndice][columna];
            palabraColumna += letter;
        }
        return palabraColumna;
    }

    /**
     * Metodo que convierte la fila {@param fila} de la matriz {@param matriz} a un String
     * <p>
     * Observaciones : la fila debe estar dentro del rango de la matriz
     *
     * @param fila la fila de la matriz
     * @return el String de la fila de la matriz
     */
    public static String obtenerPalabraFila(char[][] matriz, int fila) {
        String filaPalabra = "";
        char[] chars = matriz[fila];
        for (int i = 0; i < chars.length; i++) {
            char letter = chars[i];
            filaPalabra += letter;
        }
        return filaPalabra;
    }

    /**
     * Metodo que comprueba si una matriz es vacia (solo espacios)
     *
     * @param matrix la matriz que vamos a comprobar
     * @return true si la matriz esta vacia, en otro caso false
     */
    public static boolean esMatrizVacia(char[][] matrix) {
        boolean resultado = true;
        for (int filaIndice = 0; filaIndice < matrix.length; filaIndice++) {
            int columnaIndice = 0;
            int maxColumnaIndice = matrix[filaIndice].length;
            while (columnaIndice < maxColumnaIndice) {
                //Mientras que haya un caracter que no sea un espacio en blanco
                if (matrix[filaIndice][columnaIndice] != ' ') {
                    resultado = false;
                    //Salimos del bucle manualmente
                    columnaIndice = maxColumnaIndice;
                }
                columnaIndice++;
            }
        }
        return resultado;
    }


    /**
     * Metodo que rellena con espacios una matriz
     *
     * @param matriz la matriz que vamos a rellenar
     */
    public static void rellenarEspaciosMatriz(char[][] matriz) {
        int cero = 0;
        for (int filaIndice = cero; filaIndice < matriz.length; filaIndice++) {
            for (int columnaIndice = cero; columnaIndice < matriz[cero].length; columnaIndice++) {
                matriz[filaIndice][columnaIndice] = ' ';
            }
        }
    }

    /**
     * Metodo que imprime la matriz del juego con las coordenadas y la puntuacion del usuario
     * <p>
     * Observaciones : las filas y columnas de la matriz deben ser como maximo 10
     *
     * @param record la puntuacion del usuario
     */
    public static void imprimirMatriz(char[][] matrix, int record) {
        System.out.println();
        System.out.println("Puntos : " + record);
        System.out.println();
        //Matriz de coordenadas
        char[][] matrizCoordenadas = obtenerMatrizCoordenadas(matrix);
        //Imprimimos la matriz
        for (int filaIndice = 0; filaIndice < matrizCoordenadas.length; filaIndice++) {
            char[] chars = matrizCoordenadas[filaIndice];
            for (int columnaIndice = 0; columnaIndice < chars.length; columnaIndice++) {
                char aChar = chars[columnaIndice];
                System.out.print(aChar + " ");
            }
            System.out.println();
        }
    }

    /**
     * Metodo que devuelve una matriz con coordenadas apartir de una dada.
     * <p>
     * Observaciones : las filas y columnas de la matriz deben ser como maximo 10
     *
     * @return la matriz con coordenadas
     */
    public static char[][] obtenerMatrizCoordenadas(char[][] matrix) {
        int cero = 0;
        int uno = 1;
        int dos = 2;
        int contador = cero;
        //Matriz con coordenadas
        char[][] matrizCoordenadas = new char[matrix.length + dos][matrix[cero].length + dos];
        //Bucle que pone las coordenadas a la matriz
        for (int filaIndice = uno; filaIndice <= matrix.length; filaIndice++) {
            //Obtenemos el numero mediante ASCII
            char numero = (char) (contador + '0');
            //Rellenamos las coordenadas de la primera columna
            matrizCoordenadas[filaIndice][cero] = numero;
            //Rellenamos las coordenadas de la ultima columna
            matrizCoordenadas[filaIndice][matrizCoordenadas.length - uno] = numero;
            //Rellenamos las coordenadas de la primera fila
            matrizCoordenadas[cero][filaIndice] = numero;
            //Rellenamos las coordenadas de la ultima fila
            matrizCoordenadas[matrizCoordenadas.length - uno][filaIndice] = numero;
            //Bucle que copia el contenido de la antigua matriz a la nueva
            for (int columnaIndice = uno; columnaIndice <= matrix[cero].length; columnaIndice++) {
                matrizCoordenadas[filaIndice][columnaIndice] = matrix[filaIndice - uno][columnaIndice - uno];
            }
            contador++;
        }
        return matrizCoordenadas;
    }

    /**
     * Método que genera una palabra inversa de {@param word}
     * <p>
     * Observaciones : la concatenación de Strings o la llamada del método {@link String#concat(String)} provoca la creación de nuevos objectos de Strings,
     * a lo que impacta de forma negativa la eficiencia del programa.
     * Se recomienda el uso de {@link StringBuilder} en esta situación.
     *
     * @param word la palabra al que le vamos a dar la vuelta
     * @return {@param word} pero dado de vuelta
     * @see StringBuilder#reverse()
     */
    public static String invertirString(String word) {
        String inversa = "";
        int cero = 0;
        int uno = 1;
        for (int letterIndex = word.length() - uno; letterIndex >= cero; letterIndex--) {
            char letter = word.charAt(letterIndex);
            inversa += letter;
        }
        return inversa;
    }

    /**
     * Método que genera un String con n = {@param lenght} espacios en blanco
     * <p>
     * Observaciones : la concatenación de Strings o la llamada del método {@link String#concat(String)} provoca la creación de nuevos objectos de Strings,
     * a lo que impacta de forma negativa la eficiencia del programa.
     * Se recomienda el uso de {@link StringBuilder} en esta situación.
     *
     * @param lenght la longitud deseada de espacios en blanco
     * @return el String generado
     * @see StringBuilder
     */
    public static String generarStringEspacios(int lenght) {
        char emptyChar = ' ';
        String emptyString = "";
        for (int startIndex = 0; startIndex < lenght; startIndex++) {
            emptyString += emptyChar;
        }
        return emptyString;
    }

    /**
     * Método que obtiene el número total de espacios en blanco de una String
     *
     * @param data el string
     * @return el número total de espacios en blanco
     */
    public static int obtenerTotalEspacios(String data) {
        int contador = 0;
        char[] charArray = data.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char lettre = charArray[i];
            if (Character.isWhitespace(lettre)) contador++;
        }
        return contador;
    }

    /**
     * Método que convierte un número char de 0 a 9 a un número entero de tipo int
     *
     * @param number el número en char, tiene que estar en el rango [0,9]
     * @return el número en int
     */
    public static int obtenerNumero(char number) {
        char zero = '0';
        return number - zero;
    }

}
