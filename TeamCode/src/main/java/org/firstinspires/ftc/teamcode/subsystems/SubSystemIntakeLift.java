package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.SubSystemVariables;

public class SubSystemIntakeLift {
    // Instantiate the drivetrain motor variables
    private DcMotorEx intakeLift;


    public SubSystemIntakeLift(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        intakeLift  = hardwareMap.get(DcMotorEx.class, "intakeLift");      //Sets the names of the hardware on the hardware map

        resetLiftEncoder();
        setIntakeLiftPower(SubSystemVariables.INTAKE_LIFT_POWER);
    }

    private void resetLiftEncoder(){
        //Stop the motors and reset the encoders to zero
        intakeLift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //Make sure we re-enable the use of encoders
        intakeLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        intakeLift.setTargetPosition(0);
        intakeLift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    public int getLiftEncoders(){
        return intakeLift.getCurrentPosition();
    }

    public void setIntakeLiftPower(double speed) {
        intakeLift.setPower(speed);
    }

    public void setLiftPosition(int position)
    {
        intakeLift.setTargetPosition(position);
    }

}