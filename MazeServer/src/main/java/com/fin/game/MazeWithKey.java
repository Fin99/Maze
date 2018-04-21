package com.fin.game;

public class MazeWithKey extends MazeImpl implements Maze {
    int keyX;
    int keyY;

    {
        keyX = 7;
        keyY = 0;
    }

    @Override
    public String go(String a) {
        String out = super.go(a);
        if (a.equals("s") && personX == size - 1 && personY == size - 1 && keyX != -1 && keyY != -1)
            return out.trim() + ". Сначала вы должны взять ключ!";
        return out;
    }

    @Override
    String display() {
        if (keyY == personY && keyX == personX) {
            keyY = -1;
            keyX = -1;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < size; i++) {
            // draw the north edge
            for (int j = 0; j < size; j++) {

                if (j == 0 && i == 0) {
                    out.append("    ");
                } else {
                    out.append((maze[j][i] & 1) == 0 ? "+---" : "+   ");
                }

            }
            out.append("+\n");
            // draw the west edge
            for (int j = 0; j < size; j++) {
                if (j == personX && i == personY) {
                    out.append((maze[j][i] & 8) == 0 ? "| @ " : "  @ ");
                } else if (j == keyX && i == keyY) {
                    out.append((maze[j][i] & 8) == 0 ? "| ¶ " : "  ¶ ");
                } else {
                    out.append((maze[j][i] & 8) == 0 ? "|   " : "    ");
                }
            }
            out.append("|\n");
        }
        // draw the bottom line
        for (int j = 0; j < size - 1; j++) {
            out.append("+---");
        }
        out.append("+");
        return out.toString();
    }

    @Override
    String isWin() {
        System.out.println(personX + " " + personY + ": " + size + ": " + keyX + " " + keyY);
        if (personX == size - 1 && personY == size - 1 && keyX == -1 && keyY == -1)
            return "\nВы выиграли!                                    ";
        return "\n                                      ";
    }
}
