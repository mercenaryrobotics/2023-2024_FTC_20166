package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.SubSystemVariables;

public class SubSystemHopperLift {
    // Instantiate the drivetrain motor variables
    private DcMotorEx hopperLift;


    public SubSystemHopperLift(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        hopperLift = hardwareMap.get(DcMotorEx.class, "hopperLift");      //Sets the names of the hardware on the hardware map

        resetLiftEncoder();
        setHopperLiftPower(SubSystemVariables.HOPPER_LIFT_POWER);
    }

    private void resetLiftEncoder(){
        //Stop the motors and reset the encoders to zero
        hopperLift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //Make sure we re-enable the use of encoders
        hopperLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        hopperLift.setTargetPosition(0);
        hopperLift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    public int getLiftPosition(){
        return hopperLift.getCurrentPosition();
    }

    public void setHopperLiftPower(double speed) {
        hopperLift.setPower(speed);
    }

    public void setHopperLiftPosition(int position)
    {
        hopperLift.setTargetPosition(position);
    }

}