/*
 * IJA 2023/24: Úloha 2
 * Testovací třída.
 */
package ija.ija2023.homework2;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

//--- Importy z implementovaneho reseni ukolu
import ija.ija2023.homework2.common.Environment;
import ija.ija2023.homework2.common.Robot;
import ija.ija2023.homework2.room.ControlledRobot;
import ija.ija2023.homework2.room.Room;
//---

//--- Importy z baliku dodaneho nastroje
import ija.ija2023.homework2.tool.EnvTester;
import ija.ija2023.homework2.tool.common.Position;
import ija.ija2023.homework2.tool.common.ToolEnvironment;
import ija.ija2023.homework2.tool.common.ToolRobot;
import java.util.function.Function;


/**
 * Testovací třída pro druhý úkol z předmětu IJA 2023/24.
 * @author Radek Kočí
 */
public class Homework2Test {
    
    private ToolEnvironment room;
    private Robot r1, r2;
       
    /**
     * Vytvoří prostředí, nad kterým se provádějí testy.
     */
    @Before
    public void setUp() {
        Environment room = Room.create(5, 8);
        
        room.createObstacleAt(1, 2);
        room.createObstacleAt(1, 4);
        room.createObstacleAt(1, 5);
        room.createObstacleAt(2, 5);

        Position p1 = new Position(4,2);
        r1 = ControlledRobot.create(room, p1);        
        Position p2 = new Position(4,4);
        r2 = ControlledRobot.create(room, p2);

        this.room = room;
    }

    /**
     * Test existence dvou robotu v prostredi.
     * 1 bod
     */    
    @Test
    public void testEnvironment() {
        List<ToolRobot> robots = room.robots();
        Assert.assertEquals("Prostredi obsahuje 2 roboty", 2, robots.size());
        robots.remove(0);
        Assert.assertEquals("Prostredi obsahuje 2 roboty", 2, room.robots().size());
    }
 
    /**
     * Test správného chování při setkání svou robotů.
     * 1 bod
     */
    @Test
    public void testRobotMeetsRobot() {      
        r1.turn(2);
        Assert.assertTrue("Presun r1 na [4,3] uspeny.", r1.move());
        Assert.assertEquals("Pozice r1 = [4,3].", new Position(4,3), r1.getPosition());

        Assert.assertFalse("Presun r1 na [4,4] neuspeny.", r1.move());
        Assert.assertEquals("Pozice r1 = [4,3].", new Position(4,3), r1.getPosition());

        r1.turn(6);
        Assert.assertTrue("Presun r1 na [3,3] uspeny.", r1.move());
        Assert.assertEquals("Pozice r1 = [3,3].", new Position(3,3), r1.getPosition());
    }

    /**
     * Testování notifikací při přesunu objektu.
     * 3 body
     */
    @Test
    public void testNotificationGhostMoving() {
        EnvTester tester = new EnvTester(room);        

        /* Testy, kdy se presun podari.
         * Pri posunu nebo otoceni bude robot notifikovat zavisly objekt prave jednou.
         * Ostatni objekty budou bez notifikace.
         */
        testNotificationGhostMoving(tester, true, r1, (r) -> r.move());
        testNotificationGhostMoving(tester, true, r1, (r) -> r.move());
        testNotificationGhostMoving(tester, false, r1, (r) -> r.move());
        testNotificationGhostMoving(tester, true, r1, (r) -> {r.turn(2); return true;});
    }

    /**
     * Pomocná metoda pro testování notifikací při přesunu objektu.
     * @param tester Tester nad prostředím, který provádí vyhodnocení notifikací.
     * @param success Zda se má akce podařit nebo ne
     * @param robot objekt zpracovávaný akcí
     * @param action akce nad objektem
     */
    private void testNotificationGhostMoving(EnvTester tester, boolean success, Robot robot, Function<Robot, Boolean> action) {
        StringBuilder msg;
        boolean res;
        List<ToolRobot> notified;

        // Zadna notifikace zatim neexistuje
        notified = tester.checkEmptyNotification();
        Assert.assertTrue("Nema byt zadna notifikace, presto je: " + notified, notified.isEmpty());
        
        // Pokud se ma akce podarit
        if (success) {
            Assert.assertTrue("Akce nad " + r1 + " ma byt uspesna.", action.apply(r1));
            msg = new StringBuilder();
            // Overeni spravnych notifikaci
            res = tester.checkNotification(msg, robot);
            Assert.assertTrue("Test notifikace: " + msg, res);
        } 
        // Pokud se nema akce podarit
        else {
            Assert.assertFalse("Akce nad " + r1 + " ma byt neuspesna.", action.apply(r1));
            // Zadne notifikace nebyly zaslany
            notified = tester.checkEmptyNotification();
            Assert.assertTrue("Nema byt zadna notifikace, presto je: " + notified, notified.isEmpty());
        }
    }
    
}
