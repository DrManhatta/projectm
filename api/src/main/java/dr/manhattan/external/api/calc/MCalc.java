package dr.manhattan.external.api.calc;

import net.runelite.api.Point;

import java.util.concurrent.ThreadLocalRandom;

public class MCalc {
    public static int nextInt(int min, int max)
    {
        //return (int) ((Math.random() * ((max - min) + 1)) + min); //This does not allow return of negative values
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static Point randomize(Point p, int amount){
        if(amount <= 0) return p;
        return new Point(nextInt(p.getX() - amount / 2, p.getX() + amount/2), nextInt(p.getY() - amount / 2, p.getY() + amount/2));
    }
}
