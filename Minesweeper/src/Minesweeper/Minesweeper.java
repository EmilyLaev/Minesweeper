package Minesweeper;

import java.util.Scanner;
import java.util.Random;


public class Minesweeper {

    public static void main(String[] args) {
        int[] info = {9, 9, 0}; //default
        info = askInfo(info);
        Map map = new Map(info);
        displayMap(map);
        playGame(map);
    }

    //method AskInfo
    public static int[] askInfo(int[] info) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter dimensions of field:");
        int h = scan.nextInt();
        int w = scan.nextInt();
        System.out.println("Enter number of mines:");
        int m = scan.nextInt();
        info[0] = h;
        info[1] = w;
        info[2] = m;
        return info;
    }

    //method displayMap
    public static void displayMap(Map map) {
        System.out.println();
        System.out.println(" | 1 2 3 4 5 6 7 8 9 |");
        System.out.println("-| - - - - - - - - - |");
        int h = 0;
        for (int i = 0; i < map.info[0]; i++) {
            h++;
            System.out.print(h + "| ");
            for (int j = 0; j < map.info[1]; j++) {
                if (map.field3[i][j]) {
                    System.out.print("* ");
                } else if (!map.field2[i][j]) {
                    System.out.print(". ");
                } else if (map.field[i][j] == 0) {
                    System.out.print("/ ");
                } else if (map.field[i][j] == 9) {
                    System.out.print("x ");
                } else {
                    System.out.print(map.field[i][j] + " ");
                }
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("-| - - - - - - - - - |");
    }

    //method addMines
    public static void addMines(Map map) {
        Random random = new Random();
        int m = map.info[2];
        map.mines[0][0] = random.nextInt(map.info[0]);
        map.mines[0][1] = random.nextInt(map.info[1]);
        map.field[map.mines[0][0]][map.mines[0][1]] = 9;
        int i = 1;
        while (i < m) {
            map.mines[i][0] = random.nextInt(map.info[0]);
            map.mines[i][1] = random.nextInt(map.info[1]);
            //System.out.println(map.mines[i][0] + ", " + map.mines[i][1]);
            for (int j = 0; j < i; j++) {
                if (map.mines[i][0] == map.mines[j][0] && map.mines[i][1] == map.mines[j][1]) {
                    break;
                } else if (i - 1 == j) {
                    map.field[map.mines[i][0]][map.mines[i][1]] = 9;
                    i++;
                    break;
                }
            }
        }
    }

    //method checkForMines
    public static void checkForMines(Map map) {
        for (int i = 0; i < map.height; i++) {
            for (int j = 0; j < map.width; j++) {
                if (map.field[i][j] != 9) {
                    int numMines = minesAtPoint(map, i, j);
                    map.field[i][j] = numMines;
                }
            }
        }
    }

    //method minesAtPoint
    public static int minesAtPoint(Map map, int t, int s) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                try {
                    int x = t + i - 1;
                    int y = s + j - 1;
                    if (map.field[x][y] == 9) {
                        count++;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return count;
    }

    //method playGame
    public static void playGame(Map map) {
        Scanner scan = new Scanner(System.in);
        addMines(map);
        boolean gameOn = true;
        while (gameOn) {
            System.out.println("Set/delete mine marks or claim a cell as free (x and y coordinates): ");
            int x = scan.nextInt() - 1;
            int y = scan.nextInt() - 1;
            String action = scan.next();
            if (action.equals("free")) {
                checkForMines((map));
                isFree(map, y, x);
            } else if (action.equals("mine")) {
                markMine(map, y, x);
            }
            displayMap(map);
            gameOn = mineTriggered(map);
            gameOn = winConditions(gameOn, map);
        }
    }

    //method exploreMap
    public static void isFree(Map map, int h, int w) {
        if (!map.field3[h][w]) {
            map.field2[h][w] = true;
            if (map.field[h][w] == 0) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        try {
                            int x = h + i - 1;
                            int y = w + j - 1;
                            map.field3[x][y] = false;
                            if (!map.field2[x][y] && map.field[x][y] == 0) {
                                isFree(map, x, y);
                            }
                            map.field2[x][y] = true;
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
    }

    public static void markMine(Map map, int h, int w) {
        map.field3[h][w] = !map.field3[h][w];
    }

    //method winConditions
    public static boolean winConditions(boolean gameOn, Map map) {
        if (gameOn) {
            int count3 = 0;
            int count2 = 0;
            for (int i = 0; i < map.info[0]; i++) {
                for (int j = 0; j < map.info[1]; j++) {
                    if (map.field2[i][j] && !map.field3[i][j]) {
                        count2++;
                    }
                    if (map.field3[i][j]) {
                        count3++;
                    }
                }
            }
            if (map.info[0] * map.info[1] - count2 == map.info[2]) {
                System.out.println("Congratulations! You found all the Mines!");
                return false;
            }
            if (count3 == map.info[2]) {
                for (int i = 0; i < map.info[2]; i++) {
                    if (!map.field3[map.mines[i][0]][map.mines[i][1]]) {
                        return true;
                    }
                }
                System.out.println("Congratulations! You found all the mines!");
                return false;
            }
            return true;
        } else {
            System.out.println("Code got here");
            return false;
        }
    }

    public static boolean mineTriggered(Map map) {
        for (int i = 0; i < map.info[2]; i++) {
            if ((map.field2[map.mines[i][0]][map.mines[i][1]] && !map.field3[map.mines[i][0]][map.mines[i][1]])) {
                System.out.println("You stepped on a mine and failed! at " + map.mines[i][0] + " " + map.mines[i][1]);
                return false;
            }
        }
        return true;
    }

    static class Map {
        final int[] info;
        int[][] field;
        boolean[][] field2;
        boolean[][] field3;
        int[][] mines;
        int height;
        int width;


        public Map(int[] info) {
            this.info = info;
            this.field = new int[info[0]][info[1]];
            this.mines = new int[info[2]][2];
            this.height = info[0];
            this.width = info[1];
            this.field2 = new boolean[info[0]][info[1]];
            this.field3 = new boolean[info[0]][info[1]];
        }
    }
}