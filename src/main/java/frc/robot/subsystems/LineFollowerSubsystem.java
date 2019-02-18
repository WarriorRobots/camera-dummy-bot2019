package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Contains methods used for reading three line followers; two on the outside of
 * a 2-inch line and one inside.
 * <p>
 * When properly lined up, the middle line follower should read true, and the
 * other two should read false.
 */
public class LineFollowerSubsystem extends Subsystem {

    private static final int LEFT_PORT = 7;
    private static final int MIDDLE_PORT = 8;
    private static final int RIGHT_PORT = 9;

    private DigitalInput leftFollower, middleFollower, rightFollower;

    /** Each permutation of the line followers, Xs being on the line and Os being off */
    public static enum lineScenarios {
        OOO,OOX,OXO,OXX,XOO,XOX,XXO,XXX;
    }
    /** lineScenarios with index for use within subsystem 
     * @see lineScenarios
     */
    private static lineScenarios[] scenarioList = {
        lineScenarios.OOO,lineScenarios.OOX,
        lineScenarios.OXO,lineScenarios.OXX,
        lineScenarios.XOO,lineScenarios.XOX,
        lineScenarios.XXO,lineScenarios.XXX,
    };

    /**
     * Instantiates new subsystem; make ONLY ONE.
     * <p>
     * <code> public static final LineFollowerSubsystem lineFollowers = new
     * LineFollowerSubsystem();
     */
    public LineFollowerSubsystem() {
        leftFollower = new DigitalInput(LEFT_PORT);
        middleFollower = new DigitalInput(MIDDLE_PORT);
        rightFollower = new DigitalInput(RIGHT_PORT);
    }

    /** Return the current enum permutation of the sensors
     * @return a lineScenarios showing current permutation
     * @see lineScenarios
     */
    public lineScenarios getScenario(){
        int num=0;
        if(rightFollower.get()) num+=1;
        if(middleFollower.get()) num+=2;
        if(leftFollower.get()) num+=4;
        return scenarioList[num];
    }

    /**
     * Returns true if left and right followers don't see line, and middle follower
     * does; false otherwise;
     * @return True if on line with middle sensor.
     */
    public boolean onLine() {
        return ( lineScenarios.OXO==getScenario() );
    }

    /**
     * Returns true if there is no contact with a line.
     * <b> It is not the opposite of {@link #onLine}!</b>
     * @return True if there is no contact with a line. 
     */
    public boolean offLine() {
        return ( lineScenarios.OOO==getScenario() );
    }

    @Deprecated
    public boolean onLeftOfLine() {
        return rightFollower.get();
    }

    @Deprecated
    public boolean onRightOfLine() {
        return leftFollower.get();
    }

    // TODO determine if this needs a command
    @Override
    protected void initDefaultCommand() {
    }

}