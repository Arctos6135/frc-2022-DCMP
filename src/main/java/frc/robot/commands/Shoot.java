package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.ShooterFeederSubsystem;

public class Shoot extends CommandBase {
    
    private static final double VELOCITY_TOLERANCE = 100; // TODO: will probably change 
    
    private final Shooter shooter; 
    private final ShooterFeederSubsystem shooterFeederSubsystem; 

    private double targetVelocity = 0; 
    private boolean velocityReached = false; 
    private boolean lowerHub; 

    private boolean finished = false; 

    public Shoot(Shooter shooter, ShooterFeederSubsystem shooterFeederSubsystem, boolean lowerHub) {
        this.shooter = shooter; 
        this.shooterFeederSubsystem = shooterFeederSubsystem; 
        shooterFeederSubsystem.setRollDirection(true);
        this.lowerHub = lowerHub;
        this.targetVelocity = this.shooter.getVelocitySetpoint(); 

        addRequirements(shooter, shooterFeederSubsystem);
    }

    @Override 
    public void initialize() {
        finished = false; 
        velocityReached = false; 

        if (!shooter.getOverheatShutoffOverride() && shooter.getMonitorGroup().getOverheatShutoff()) {
            finished = true; 
            RobotContainer.getLogger().logError("Shooter is overheating, cannot shoot."); 
        }

        if (lowerHub) {
            shooter.setVelocity(Constants.LOW_HUB_RPM); 
        } else {
            shooter.setVelocity(Constants.HIGH_HUB_RPM); 
        }
    }

    @Override 
    public void execute() {
        if (Math.abs(shooter.getActualVelocity() - targetVelocity) < VELOCITY_TOLERANCE && shooterFeederSubsystem.getBallInShotPosition()) {
            // Shoot the ball 
            velocityReached = true; 
            shooterFeederSubsystem.startRoller();

            /* if (lowerHub) {
                try {
                    shooter.fire(false); 
                } catch (Shooter.PowerException exception) {
                    RobotContainer.getLogger().logError("The shooter motor cannot support the lower hub shot!");
                } 
                
            } else {
                try {
                    shooter.fire(true); 
                } catch (Shooter.PowerException exception) {
                    RobotContainer.getLogger().logError("The shooter motor cannot support the upper hub shot!"); 
                } 
            } */

        } else {
            shooterFeederSubsystem.stopRoller(); 

            if (velocityReached) {
                shooterFeederSubsystem.decrementBallCount(); 
            }
        }
    }

    @Override 
    public void end(boolean interrupted) {
        // Stop the feeder 
        shooter.setVelocity(0);
        shooterFeederSubsystem.stopRoller(); 
    }

    @Override 
    public boolean isFinished() {
        return finished; 
    }
}
