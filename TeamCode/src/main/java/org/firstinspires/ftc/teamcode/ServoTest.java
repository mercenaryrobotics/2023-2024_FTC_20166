package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoTest {
    // Instantiate the drivetrain motor variables
    private Servo testServo;


    public ServoTest(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        testServo = hardwareMap.get(Servo.class, "claw");      //Sets the names of the hardware on the hardware map
        //Make sure close at the start
        //closeClaw(true);
    }
    public void changeServo(boolean servoTester) {
        if (servoTester) {
            testServo.setPosition(.5);

        } else {
            testServo.setPosition(0);
        }
    }

    public void runOpMode()  {

    }
}