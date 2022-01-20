public class GridSpace {
    private int row,col,state,lifespan;

    public GridSpace(int row, int col, int state, int lifespan) {
        this.row = row;
        this.col = col;
        this.state = state;
        this.lifespan = lifespan;
    }

    public boolean isHead() {
        return state == 2;
    }

    public boolean isBody() {
        return state == 1;
    }

    public boolean isApple() {
        return state == 0;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void goUp() {
        row--;
    }

    public void goDown() {
        row++;
    }

    public void goRight() {
        col++;
    }

    public void goLeft() {
        col--;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setPos(int r, int c) {
        row = r;
        col = c;
    }

    public void age() {
        lifespan++;
    }

    public String toString() {
        String out;
        out = "(" + row + ", " + col + ") ";
        if (state == 0)
            out += "EMPTY";
        else if (state == 1)
            out += "ALIVE: ";
        else if (state == 2)
            out += "HEAD: ";
        if (lifespan>0)
            out += "" + lifespan;
        return out;
    }
}