package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.CenterstageMainTeleOp;
import org.firstinspires.ftc.teamcode.SubSystemVariables;

@Config
public class SubSystemDroneLaunch {
    // Instantiate the drivetrain motor variables
    private Servo drone;



    public SubSystemDroneLaunch(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        drone = hardwareMap.get(Servo.class, "drone");      //Sets the names of the hardware on the hardware map
        drone.setPosition(.65);
    }

    public void launchDrone(boolean doLaunch) {
        if(doLaunch) {
            drone.setPosition(SubSystemVariables.droneLaunchVal);
        } else {
            drone.setPosition(SubSystemVariables.droneNoLaunchVal);
        }
    }

}