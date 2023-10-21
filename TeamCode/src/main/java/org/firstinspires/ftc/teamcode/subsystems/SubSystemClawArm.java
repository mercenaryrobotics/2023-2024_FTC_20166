package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.SubSystemVariables;

public class SubSystemClawArm {
    // Instantiate the drivetrain motor variables
    private DcMotorEx clawArm;


    public SubSystemClawArm(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        // Initialize the motor hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        clawArm = hardwareMap.get(DcMotorEx.class, "clawArm");      //Sets the names of the hardware on the hardware map

        resetClawArmEncoder();
        setClawArmSpeed(SubSystemVariables.CLAW_ARM_POWER);
    }

    private void resetClawArmEncoder(){
        //Stop the motors and reset the encoders to zero
        clawArm.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //Make sure we re-enable the use of encoders
        clawArm.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        clawArm.setTargetPosition(0);
        clawArm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    public int getClawArmEncoders(){
        return clawArm.getCurrentPosition();
    }

    public void setClawArmSpeed(double speed) {
        clawArm.setPower(speed);
    }

    public void setClawArmPosition(int position) {clawArm.setTargetPosition(position); }

}