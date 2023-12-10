package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class SubSystemIntake {
    // Instantiate the drivetrain motor variables
    private DcMotorEx intake;
    private Servo intakeRiserServo;

    public static double riserUpPosition = .5;
    public static double riserDownPosition = .7;

    public SubSystemIntake(HardwareMap hardwareMap) throws InterruptedException {                 // Motor Mapping
        intake = hardwareMap.get(DcMotorEx.class, "intake");      //Sets the names of the hardware on the hardware map
        intake.setPower(0);

        intakeRiserServo = hardwareMap.get(Servo.class, "intakeRiserServo");
        intakeRiserServo.setPosition(riserUpPosition);
    }

    public void setIntakePower(double speed) {
        intake.setPower(speed);
    }

    public void setIntakeRiserPositionUp(boolean upDown)
    {
        if (upDown)
            intakeRiserServo.setPosition(riserUpPosition);
        else
            intakeRiserServo.setPosition(riserDownPosition);
    }

}