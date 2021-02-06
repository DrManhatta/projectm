package dr.manhattan.external.api.mouse;

import dr.manhattan.external.api.M;
import dr.manhattan.external.api.calc.MCalc;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dr.manhattan.external.api.calc.MCalc.nextInt;
import static java.awt.event.MouseEvent.*;
import static java.lang.Thread.sleep;

@Slf4j
public class MMouse {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void delayMouseClick(Point point, long delay) {
        executorService.submit(() ->
        {
            try {
                sleep(delay);
                handleMouseClick(point);
            } catch (RuntimeException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void delayMouseClick(Rectangle rectangle, long delay) {
        Point point = getClickPoint(rectangle);
        delayMouseClick(point, delay);
    }

    public static void delayMouseClick(Shape shape, long delay) {
        Point point = getClickPoint(shape);
        delayMouseClick(point, delay);
    }

    public static Point getClickPoint(Rectangle rect) {
        final int x = (int) (rect.getX() + nextInt((int) rect.getWidth() / 6 * -1, (int) rect.getWidth() / 6) + rect.getWidth() / 2);
        final int y = (int) (rect.getY() + nextInt((int) rect.getHeight() / 6 * -1, (int) rect.getHeight() / 6) + rect.getHeight() / 2);

        return new Point(x, y);
    }

    public static Point getClickPoint(Shape shape) {
        int x = -1, y = -1;

        Rectangle bounds = shape.getBounds();
        int minX = (int) bounds.getMinX();
        int maxX = (int) bounds.getMaxX();
        int minY = (int) bounds.getMinY();
        int maxY = (int) bounds.getMaxY();

        while (!shape.contains(x, y)) {
            x = MCalc.nextInt(minX, maxX);
            y = MCalc.nextInt(minY, maxY);
        }
        return new Point(x, y);
    }

    public static boolean handleMouseClick(Point point) {

        if(pointOnMinimap(point)) log.info("POINT ON MINIMAP?!");
        if(pointInViewport(point)) log.info("Point in viewport.");
        log.info("Clicking at Point: {}", point);
        executorService.submit(() -> click(point));

        return true;
    }

    private static boolean pointInViewport(Point p){
        Client client = M.getInstance().getClient();
        int viewportHeight = client.getViewportHeight();
        int viewportWidth = client.getViewportWidth();
        Rectangle viewport = new Rectangle(0, 0, viewportWidth, viewportHeight);

        return viewport.contains(new java.awt.Point(p.getX(), p.getY()));
    }

    private static boolean pointOnMinimap(Point p){
        Client client = M.getInstance().getClient();
        Widget minimapWidget = client.getWidget(164, 20);
        return (minimapWidget != null && minimapWidget.getBounds().contains(p.getX(), p.getY()));
    }

    private static void mouseEvent(int id, Point point) {
        if(point == null) return;
        Client client = M.getInstance().getClient();
        MouseEvent e = new MouseEvent(
                client.getCanvas(), id,
                System.currentTimeMillis(),
                0, point.getX(), point.getY(),
                1, false, 1
        );

        client.getCanvas().dispatchEvent(e);
    }

    private static Point adjustForStretched(Point p){
        Client client = M.getInstance().getClient();

        if (!client.isStretchedEnabled()) return p;
            Dimension stretched = client.getStretchedDimensions();
            Dimension real = client.getRealDimensions();
            double width = (stretched.width / real.getWidth());
            double height = (stretched.height / real.getHeight());
            return new Point((int) (p.getX() * width), (int) (p.getY() * height));
    }


    public static void click(Point p) {
        p = adjustForStretched(p);
        try {
            mouseEvent(MOUSE_MOVED, p);
            Thread.sleep(nextInt(50, 500));
            mouseEvent(MOUSE_PRESSED, p);
            Thread.sleep(nextInt(5, 50));
            mouseEvent(MOUSE_RELEASED, p);
            mouseEvent(MOUSE_CLICKED, p);

        } catch (InterruptedException e) {
            log.error("Mouse", e);
        }
    }
}
