import java.util.ArrayList;

public class Snake {
    private int length = 5;
    private ArrayList<GridSpace> body = new ArrayList<>();
    private ArrayList<Integer> queue = new ArrayList<>();
    private boolean growing;

    public Snake() {
            body.add(new GridSpace(7, 7, 2, 5));//head
            body.add(new GridSpace(7, 6, 1, 4));//body segments
            body.add(new GridSpace(7, 5, 1, 3));
            body.add(new GridSpace(7, 4, 1, 2));
            body.add(new GridSpace(7, 3, 1, 1));
            for (int i = 0; i < 5; i++)
                queue.add(3);
    }

    public GridSpace getHead() {
        for (GridSpace g : body)
            if (g.isHead())
                return g;
        return null;
    }

    public void setGrow(boolean growing) {
        this.growing = growing;
    }

    public boolean isGrowing() {
        return growing;
    }

    public GridSpace getTail() {
        return body.get(body.size()-1);
    }

    public void grow(int r, int c) {
        for (GridSpace g : body)
            g.age();
        queue.add(0, queue.get(0));
        body.add(new GridSpace(r,c,1,1));
    }

    public int getLength() {
        return body.size();
    }

    public boolean spaceIsFull(int r, int c) {
        for (GridSpace g : body)
            if (r == g.getRow() && c == g.getCol())
                return true;
        return false;
    }

    public boolean lost() {
        for (int i=1; i<body.size(); i++)
            if (body.get(0).getRow() == body.get(i).getRow() && body.get(0).getCol() == body.get(i).getCol()) //collision detection
                return true;
        if (body.get(0).getRow()<0 || body.get(0).getRow()>15 || body.get(0).getCol()<0 || body.get(0).getCol()>23) //creates walls
            return true;
        return false;
    }

    public boolean won() {
        return body.size() == 384;
    }

    public void addQueue(int input) {
        queue.add(input);
        while (queue.size()>body.size())
            queue.remove(0);
        System.out.println(queue);
    }

    public int direction() {
        return queue.get(queue.size()-1);
    }

    public void useQueue() {
        int growR = getTail().getRow();
        int growC = getTail().getCol();
        for (int i=0; i< queue.size(); i++) {
            GridSpace g = body.get(i);
            if (queue.get(g.getLifespan()-1) == 0)
                g.goUp();
            else if (queue.get(g.getLifespan()-1) == 1)
                g.goDown();
            else if (queue.get(g.getLifespan()-1) == 2)
                g.goLeft();
            else if (queue.get(g.getLifespan()-1) == 3)
                g.goRight();
            //else if (queue.get(0) == 4)
            else
                System.exit(34);
        }
    }

    public ArrayList<Integer> getQueue() {
        return queue;
    }

    public String toString() {
        return "" + body;
    }
}