package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.SubSystemVariables;

public class SubSystemClaw {
    // Instantiate the drivetrain motor variables
    private Servo claw;


    public SubSystemClaw(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        claw = hardwareMap.get(Servo.class, "claw");      //Sets the names of the hardware on the hardware map
    }

    public void changeClawPosition() {
        if(!SubSystemVariables.CLAW_OPEN) {
            claw.setPosition(.5); //To open
        } else {
            claw.setPosition(0); // To close
        }
    }

}