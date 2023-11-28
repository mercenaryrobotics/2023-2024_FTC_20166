package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SubSystemHopperIntakeLift {
    // Instantiate the drivetrain motor variables
    private DcMotorEx hopperIntakeLift;


    public SubSystemHopperIntakeLift(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        hopperIntakeLift = hardwareMap.get(DcMotorEx.class, "intakeLift");      //Sets the names of the hardware on the hardware map
        hopperIntakeLift.setPower(0);
        resetLiftEncoder();
    }

    private void resetLiftEncoder(){
        //Stop the motors and reset the encoders to zero
        hopperIntakeLift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //Make sure we re-enable the use of encoders
        hopperIntakeLift.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        hopperIntakeLift.setTargetPosition(0);
        hopperIntakeLift.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public int getLiftEncoders(){
        return hopperIntakeLift.getCurrentPosition();
    }

    public void setHopperIntakeLiftPower(double speed) {
        hopperIntakeLift.setPower(speed);
    }

}