package frc.robot.commands.driving;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Drivetrain;

// TODO: check if we need to use a Limelight for any reason (may need to align to hub)
public class TeleopDrive extends CommandBase {
    private final Drivetrain drivetrain;
    private final GenericHID controller; // a USB Human Interface Device

    // X-Box Controller related variables
    private final int X_AXIS;
    private final int Y_AXIS;

    // Steering and Movement
    private static boolean reverseDrive = false;
    private static boolean precisionDrive = false;
   
    // Percentage of steering input when driving in precision drive mode
    private static double precisionFactor = 0.5;
   
    // Ramping
    private static double rampingRate = 0.375; // time (seconds) it takes to go from 0 to max speed.

    public TeleopDrive(Drivetrain drivetrain, GenericHID controller, int fwdRevAxis, int leftRightAxis) {
        this.drivetrain = drivetrain;
        this.controller = controller;

        this.Y_AXIS = fwdRevAxis;
        this.X_AXIS = leftRightAxis;

        addRequirements(drivetrain);
    }

    /**
     * Gets whether the drive is reversed.
     *
     * @return if the drive is reversed.
     */
    public static boolean isReversed() {
        return reverseDrive;
    }
   
    /**
     * Set whether the drive should be reversed.
     *
     * @param reverseDrive whether the drive is reversed.
     */
    public static void setReverseDrive(boolean reverseDrive) {
        TeleopDrive.reverseDrive = reverseDrive;
    }
   
    /**
     * Toggle reverse drive.
     */
    public static void toggleReverseDrive() {
        reverseDrive = !reverseDrive;
    }
   
    /**
     * Gets whether the drive is precision drive.
     *
     * @return whether the drive is precision drive.
     */
    public static boolean isPrecisionDrive() {
        return precisionDrive;
    }
   
    /**
     * Turn precision drive on or off.
     *
     * During precision drive, motor outputs are scaled down to focus on
     * accurate driving.
     *
     * @param precisionDrive whether the drive is in precision mode.
     */
    public static void setPrecisionDrive(boolean precisionDrive) {
        TeleopDrive.precisionDrive = precisionDrive;
    }
   
    /**
     * Toggle precision drive.
     */
    public static void togglePrecisionDrive() {
        precisionDrive = !precisionDrive;
    }
   
    /**
     * Set the drive output multiplier in precision mode.
     *
     * @param factor the precision mode multiplier.
     */
    public static void setPrecisionFactor(double factor) {
        precisionFactor = factor;
    }
   
    /**
     * Get the drive output multiplier in precision mode.
     *
     * @return the precision mode mulitplier.
     */
    public static double getPrecisionFactor() {
        return precisionFactor;
    }
   
    /**
     * Set the ramping rate.
     *
     * @param rampingRate number of seconds it takes to go from 0 to max speed.
     */
    public static void setRampingRate(double rampingRate) {
        TeleopDrive.rampingRate = rampingRate;
    }

    /**
     * Get the ramping rate.
     *
     * @return number of seconds it takes to go from 0 to max speed.
     */
    public static double getRampingRate() {
        return rampingRate;
    }
   
    public static double applyDeadband(double x, double deadband) {
        if (Math.abs(x) <= deadband) {
            return 0;
        }

        return Math.copySign(Math.abs(x) - deadband, x) / (1.0 - deadband);
    }
   
    @Override
    public void initialize() {
        drivetrain.setRamping(rampingRate);
    }

    @Override
    public void execute() {
        // Pushing a joystick forward is a negative Y value (if the drive is not reversed).
        double y = applyDeadband(reverseDrive ? controller.getRawAxis(Y_AXIS) : -controller.getRawAxis(Y_AXIS),
                Constants.CONTROLLER_DEADZONE);

        // Increase control by squaring input values. Negative values will, however, stay negative.
        y = Math.copySign(y * y, y);

        // TODO: If a limelight is needed, use AUTO_ALIGN
        double x = applyDeadband(controller.getRawAxis(X_AXIS), Constants.CONTROLLER_DEADZONE);
        x = Math.copySign(x * x, x);

        drivetrain.arcadeDrive(y, x, precisionDrive ? precisionFactor : 1.0);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.setRamping(0);
        drivetrain.setMotors(0, 0);
    }
   
    @Override
    public boolean isFinished() {
        return false;
    }

}
