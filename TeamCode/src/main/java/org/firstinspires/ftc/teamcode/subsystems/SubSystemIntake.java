package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SubSystemIntake {
    // Instantiate the drivetrain motor variables
    private DcMotorEx intake;

    public SubSystemIntake(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        intake = hardwareMap.get(DcMotorEx.class, "intake");      //Sets the names of the hardware on the hardware map
        intake.setPower(0);
    }

    public void setIntakePower(double speed) {
        intake.setPower(speed);
    }

}