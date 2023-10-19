package org.firstinspires.ftc.teamcode;

import static java.lang.Boolean.TRUE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.SubSystemDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHangLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntakeLift;

import java.lang.reflect.Field;

@TeleOp

//@Disabled
public class CenterstageMainTeleOp extends LinearOpMode {
    private ElapsedTime runtime     = new ElapsedTime();
    private boolean hangLiftHang = false;
    private boolean hangLiftDrop = true;
    private int intakeLiftPosition = 1;
    private boolean clawOpen = false;

    private enum ALLIANCE_COLOR {RED, BLUE}
    private enum START_POSITION {LEFT, RIGHT}
    private ALLIANCE_COLOR alianceColor = ALLIANCE_COLOR.RED;
    private START_POSITION startPosition = START_POSITION.LEFT;

    private boolean FieldCentric = true;
    private boolean lastButtonState = false;
    private boolean driveModeChangeButton = false;

    private SubSystemDrivetrain drivetrain=null;
    private SubSystemIntakeLift intakeLift = null;
    private SubSystemHangLift hangLift = null;

    private double joystickTranslateX = 0.0;
    private double joystickTranslateY = 0.0;
    private double joystickRotate = 0.0;
    private boolean intakeLiftGoTop = false;
    private boolean intakeLiftGoMId = false;
    private boolean intakeLiftGoBottom = false;

    private boolean hangRelease = false;
    private double hangLiftControl = 0.0;

    private double joystick1LeftXOffset = 0.0;
    private double joystick1LeftYOffset = 0.0;
    private double joystick1RightXOffset = 0.0;
    private double joystick1RightYOffset = 0.0;
    private double joystick2LeftXOffset = 0.0;
    private double joystick2LeftYOffset = 0.0;
    private double joystick2RightXOffset = 0.0;
    private double joystick2RightYOffset = 0.0;

    public void initHardware() throws InterruptedException {
        drivetrain = new SubSystemDrivetrain(hardwareMap);
        drivetrain.resetGyro();
        intakeLift = new SubSystemIntakeLift(hardwareMap);
        hangLift   = new SubSystemHangLift(hardwareMap);

        gamepadsReset();
    }
    private void waitStart(){
        // Wait for the game to start (driver presses PLAY)
        //Use this time to run the vision code to detect team token position
        // Abort this loop if started or stopped.
        while (!(isStarted() || isStopRequested())) {
            if (gamepad1.b){alianceColor = ALLIANCE_COLOR.RED;}
            if (gamepad1.x){alianceColor = ALLIANCE_COLOR.BLUE;}

            if (gamepad1.left_bumper) {startPosition = START_POSITION.LEFT;}
            if (gamepad1.right_bumper) {startPosition = START_POSITION.RIGHT;}

            telemetry.addData("Alliance Color", alianceColor);
            telemetry.addData("Start position", startPosition);

            telemetry.addData("Iniyann is the greatest programmer ", "and the best driver alive");

            telemetry.update();
            idle();


        }
    }

    /**
     * Disable all hardware
     */
    private void disableHardware() {

        drivetrain.disableDrivetrainMotors();
        intakeLift.setLift(0);
        hangLift.setLift(0);
    }

    private void gamepadsReset()
    {
        //Measure 'at rest' joystick positions
        joystick1LeftXOffset = gamepad1.left_stick_x;
        joystick1RightXOffset = gamepad1.right_stick_x;

        joystick1LeftYOffset = gamepad1.left_stick_y;
        joystick1RightYOffset = gamepad1.right_stick_y;


        joystick2LeftXOffset = gamepad2.left_stick_x;
        joystick2RightXOffset = gamepad2.right_stick_x;

        joystick2LeftYOffset = gamepad2.left_stick_y;
        joystick2RightYOffset = gamepad2.right_stick_y;
    }
    private void gamepadsUpdate()
    {
        //Drive base motion controls
        joystickTranslateX = gamepad1.left_stick_x - joystick1LeftXOffset;
        joystickTranslateY = gamepad1.left_stick_y - joystick1LeftYOffset;
        joystickRotate     = gamepad1.right_stick_x - joystick1RightXOffset;
        //intake lift controls
        intakeLiftGoTop = gamepad2.dpad_up;
        intakeLiftGoMId = gamepad2.dpad_left;
        intakeLiftGoBottom = gamepad2.dpad_down;
        //Servo controls
        if(gamepad1.b) {
            hangRelease = true;
        }

        //hangLiftControl  = gamepad2.left_trigger - gamepad2.right_trigger;

        if (gamepad1.y) {
            hangLiftHang = true;
            hangLiftDrop = false;
        }
        if(gamepad1.a) {
            hangLiftHang = false;
            hangLiftDrop = true;
        }

        if(gamepad2.dpad_down) {
            intakeLiftPosition = 1;
        }
        if(gamepad2.dpad_left) {
            intakeLiftPosition = 2;
        }
        if(gamepad2.dpad_right) {
            intakeLiftPosition = 3;
        }
        if(gamepad2.dpad_up) {
            intakeLiftPosition = 4;
        }

        if(gamepad2.left_bumper) {
            clawOpen = true;
        }
        if(gamepad2.right_bumper) {
            clawOpen = false;
        }

        driveModeChangeButton = gamepad1.x;
     }
    private void drivebaseUpdate()
    {
        double translateSpeed = Math.hypot(joystickTranslateX, joystickTranslateY);
        //Heading 0 = forward, -ve right, +ve left
        double heading = Math.atan2(-joystickTranslateX, -joystickTranslateY);

        drivetrain.doMecanumDrive(translateSpeed, heading, joystickRotate, FieldCentric);
    }

    private void telemetryUpdate()
    {
        telemetry.addData("Hang State = ", hangRelease);
        telemetry.addData("Field Centric = ", FieldCentric);
        telemetry.addData("Gyro Value = ", drivetrain.getCurrentHeading());
        telemetry.update();
    }


    private void intakeLiftUpdate() {
        if(intakeLiftPosition == 1) {
            intakeLift.setLiftPosition(intakeLift.LIFT_POS_1);
        }
        else if(intakeLiftPosition == 2) {
            intakeLift.setLiftPosition(intakeLift.LIFT_POS_2);
        }
        else if(intakeLiftPosition == 3) {
            intakeLift.setLiftPosition(intakeLift.LIFT_POS_3);
        }
        else if(intakeLiftPosition == 4) {
            intakeLift.setLiftPosition(intakeLift.LIFT_POS_4);
        }
    }

    private void hangLiftUpdate()
    {
        hangLift.setLift(hangLiftControl);

        hangLift.SubSystemHangState(hangRelease);
    }

    private void doTeleop()
    {
        while(opModeIsActive())
        {
            //Update the joystick reading
            gamepadsUpdate();
            //Process the joysticks for drivebase motion
            drivebaseUpdate();
            //Update the intake
            intakeLiftUpdate();
            //Update the hang lift
            hangLiftUpdate();
            //Update telemetry
            telemetryUpdate();

            if(driveModeChangeButton && !lastButtonState) {
                FieldCentric = !FieldCentric;
            }

            lastButtonState = driveModeChangeButton;
        }
    }

    /**
     * This is the main op mode and should call all the initialization, wait for start,
     * execute your desired auto/tele-op, then stop everything
     */
    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        waitStart();
        runtime.reset();

        doTeleop();

        //Done so turn everything off now
        disableHardware();
    }

}

