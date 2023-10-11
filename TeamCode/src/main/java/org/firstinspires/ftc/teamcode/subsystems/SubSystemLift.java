package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Thread.sleep;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class SubSystemLift {
    // Instantiate the drivetrain motor variables
    private DcMotorEx lift;
    public int liftMaxHeight = 3300;

    public SubSystemLift(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        lift  = hardwareMap.get(DcMotorEx.class, "lift");      //Sets the names of the hardware on the hardware map

        resetLiftEncoder();
    }

    private void resetLiftEncoder(){
        //Stop the motors and reset the encoders to zero
        lift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //Make sure we re-enable the use of encoders
        lift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        lift.setTargetPosition(0);
        lift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    public int getLiftEncoders(){

        return lift.getCurrentPosition();
    }

    public void setLift(double speed){

        lift.setPower(speed);
    }

    public void setLiftPosition(int position)
    {
        lift.setTargetPosition(position);
    }

}