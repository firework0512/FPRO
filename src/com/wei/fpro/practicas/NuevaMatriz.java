package com.wei.fpro.practicas;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class NuevaMatriz {
    public NuevaMatriz() {
    }

    public static char[][] nuevaMatriz(int nFILAS, int nCOLUMNAS, String[] listaPalabras) {
        char[][] matriz = new char[nFILAS][nCOLUMNAS];

        int indicePal;
        for(indicePal = 0; indicePal < matriz.length; ++indicePal) {
            for(int j = 0; j < matriz[0].length; ++j) {
                matriz[indicePal][j] = ' ';
            }
        }

        do {
            for(indicePal = 0; indicePal < listaPalabras.length; ++indicePal) {
                String palabra = listaPalabras[indicePal];
                int dir = (int)(Math.random() * 4.0D);
                String palAInsertar = palabra;
                int columnaCapaz;
                int n;
                switch(dir) {
                    case 0:
                        palAInsertar = reflejada(palAInsertar);
                    case 1:
                        int filaCapaz = filaCapazAleatoria(matriz, palAInsertar.length());
                        if (filaCapaz >= 0) {
                            columnaCapaz = (int)(Math.random() * (double)(espaciosFila(matriz, filaCapaz) - palAInsertar.length()));
                            n = blancoEnPosFila(matriz, filaCapaz, columnaCapaz);
                            colocar(palAInsertar, matriz, 'E', filaCapaz, n);
                        } else {
                            colocarEnLosHuecos(palAInsertar, matriz);
                        }
                        break;
                    case 2:
                        palAInsertar = reflejada(palAInsertar);
                    case 3:
                        columnaCapaz = columnaCapazAleatoria(matriz, palAInsertar.length());
                        if (columnaCapaz >= 0) {
                            n = (int)(Math.random() * (double)(espaciosColumna(matriz, columnaCapaz) - palAInsertar.length()));
                            int filaInicial = blancoEnPosColumna(matriz, columnaCapaz, n);
                            colocar(palAInsertar, matriz, 'N', filaInicial, columnaCapaz);
                        } else {
                            colocarEnLosHuecos(palAInsertar, matriz);
                        }
                }
            }

            gravedad(matriz);
            encoger(matriz);
        } while(!hayPalabraEnTablero(matriz, listaPalabras));

        return matriz;
    }

    private static boolean hayPalabraEnTablero(char[][] matriz, String[] listaPalabras) {
        int j;
        String tira;
        int p;
        for(j = 0; j < matriz.length; ++j) {
            tira = "";

            for(p = 0; p < matriz[0].length; ++p) {
                tira = tira + matriz[j][p];
            }

            for(p = 0; p < listaPalabras.length; ++p) {
                if (tira.indexOf(listaPalabras[p]) >= 0) {
                    return true;
                }

                if (tira.indexOf(reflejada(listaPalabras[p])) >= 0) {
                    return true;
                }
            }
        }

        for(j = 0; j < matriz[0].length; ++j) {
            tira = "";

            for(p = 0; p < matriz.length; ++p) {
                tira = tira + matriz[p][j];
            }

            for(p = 0; p < listaPalabras.length; ++p) {
                if (tira.indexOf(listaPalabras[p]) >= 0) {
                    return true;
                }

                if (tira.indexOf(reflejada(listaPalabras[p])) >= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void encoger(char[][] m) {
        int ultimaFila = m.length - 1;
        int primeraColumnaNovacia = -1;
        int numColumnasNoVacias = 0;

        int columna;
        for(columna = m[0].length - 1; columna >= 0; --columna) {
            if (m[ultimaFila][columna] != ' ') {
                primeraColumnaNovacia = columna;
                ++numColumnasNoVacias;
            }
        }

        if (primeraColumnaNovacia != -1) {
            columna = primeraColumnaNovacia + 1;

            for(int cont = 2; cont <= numColumnasNoVacias; ++cont) {
                if (m[ultimaFila][columna] == ' ') {
                    int prim = primeraColumnaNoVacia(m, columna + 1);

                    for(int i = 0; i < m.length; ++i) {
                        m[i][columna] = m[i][prim];
                        m[i][prim] = ' ';
                    }
                }

                ++columna;
            }
        }

    }

    private static int primeraColumnaNoVacia(char[][] m, int columna) {
        int ultimaFila = m.length - 1;

        for(int j = columna; j < m[0].length; ++j) {
            if (m[ultimaFila][j] != ' ') {
                return j;
            }
        }

        return -1;
    }

    private static void gravedad(char[][] m) {
        for(int c = 0; c < m[0].length; ++c) {
            int pos = m.length - 1;
            boolean terminado = false;

            while(!terminado) {
                if (m[pos][c] == ' ') {
                    int posCaracter = posNoBlanco(m, c, pos - 1);
                    if (posCaracter == -1) {
                        terminado = true;
                    } else {
                        m[pos][c] = m[posCaracter][c];
                        m[posCaracter][c] = ' ';
                    }
                }

                --pos;
                if (pos == 0) {
                    terminado = true;
                }
            }
        }

    }

    private static int posNoBlanco(char[][] m, int c, int n) {
        for(int j = n; j >= 0; --j) {
            if (m[j][c] != ' ') {
                return j;
            }
        }

        return -1;
    }

    private static int blancoEnPosColumna(char[][] m, int columna, int n) {
        int p;
        for(p = 0; m[p][columna] != ' '; ++p) {
        }

        for(int i = 1; i < n; ++i) {
            ++p;

            while(m[p][columna] != ' ') {
                ++p;
            }
        }

        return p;
    }

    private static int columnaCapazAleatoria(char[][] m, int length) {
        int c = 0;

        int nespacios;
        for(nespacios = espaciosColumna(m, c); nespacios < length && c < m[0].length - 1; nespacios = espaciosColumna(m, c)) {
            ++c;
        }

        return nespacios >= length ? c : -1;
    }

    private static int espaciosColumna(char[][] m, int c) {
        int n = 0;

        for(int i = 0; i < m.length; ++i) {
            if (m[i][c] == ' ') {
                ++n;
            }
        }

        return n;
    }

    private static void colocarEnLosHuecos(String palAInsertar, char[][] m) {
        int i = 0;
        int j = 0;

        for(int p = 0; p < palAInsertar.length(); ++p) {
            while(m[i][j] != ' ') {
                ++j;
                if (j == m[0].length) {
                    j = 0;
                    ++i;
                }
            }

            m[i][j] = palAInsertar.charAt(p);
        }

    }

    private static void colocar(String palAInsertar, char[][] m, char dir, int fila, int columna) {
        int c;
        int i;
        if (dir == 'E') {
            c = columna;

            for(i = 0; i < palAInsertar.length(); ++i) {
                while(m[fila][c] != ' ') {
                    ++c;
                }

                m[fila][c] = palAInsertar.charAt(i);
                ++c;
            }
        } else {
            c = fila;

            for(i = 0; i < palAInsertar.length(); ++i) {
                while(m[c][columna] != ' ') {
                    ++c;
                }

                m[c][columna] = palAInsertar.charAt(i);
                ++c;
            }
        }

    }

    private static int blancoEnPosFila(char[][] m, int fila, int n) {
        int p;
        for(p = 0; m[fila][p] != ' '; ++p) {
        }

        for(int i = 1; i <= n; ++i) {
            ++p;

            while(m[fila][p] != ' ') {
                ++p;
            }
        }

        return p;
    }

    private static int filaCapazAleatoria(char[][] m, int length) {
        int f = 0;

        int nespacios;
        for(nespacios = espaciosFila(m, f); nespacios < length && f < m.length - 1; nespacios = espaciosFila(m, f)) {
            ++f;
        }

        return nespacios >= length ? f : -1;
    }

    private static int espaciosFila(char[][] m, int f) {
        int n = 0;

        for(int j = 0; j < m[0].length; ++j) {
            if (m[f][j] == ' ') {
                ++n;
            }
        }

        return n;
    }

    private static String reflejada(String s) {
        String ref = "";

        for(int i = 0; i < s.length(); ++i) {
            ref = s.charAt(i) + ref;
        }

        return ref;
    }
}
