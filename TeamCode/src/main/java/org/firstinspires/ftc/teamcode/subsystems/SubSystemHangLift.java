package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SubSystemHangLift {
    // Instantiate the drivetrain motor variables
    private DcMotorEx hangLift;

    public SubSystemHangLift(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the driver hub).
        hangLift = hardwareMap.get(DcMotorEx.class, "hangLift");      //Sets the names of the hardware on the hardware map
        resetLiftEncoder();
    }

    private void resetLiftEncoder(){
        //Stop the motors and reset the encoders to zero
        hangLift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //Make sure we re-enable the use of encoders
        hangLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    public void setLift(double speed){

        hangLift.setPower(speed);
    }

}