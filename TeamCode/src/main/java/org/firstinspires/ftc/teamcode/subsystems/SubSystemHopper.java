package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.SubSystemVariables;

public class SubSystemHopper {
    // Instantiate the drivetrain motor variables
    private Servo hopperGateServo;
    private Servo hopperServo;


    public SubSystemHopper(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        hopperServo = hardwareMap.get(Servo.class, "hopperServo");      //Sets the names of the hardware on the hardware map
        hopperGateServo = hardwareMap.get(Servo.class, "hopperGateServo");      //Sets the names of the hardware on the hardware map
        //Make sure close at the start
        //closeClaw(true);
    }

    public void openGate(boolean state) {
        if(state) {
            hopperGateServo.setPosition(SubSystemVariables.HOPPER_GATE_OPEN);
        } else {
            hopperGateServo.setPosition(SubSystemVariables.HOPPER_GATE_CLOSE);
        }
    }

    public void moveHopper(String position) {
        if(position.equals("down")) {
            hopperServo.setPosition(SubSystemVariables.HOPPER_SERVO_DOWN);
        } else if (position.equals("up")) {
            hopperServo.setPosition(SubSystemVariables.HOPPER_SERVO_UP);
        }
    }
}