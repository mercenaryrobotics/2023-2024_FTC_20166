package org.firstinspires.ftc.teamcode;

import static java.lang.Boolean.TRUE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.SubSystemDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHangLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntakeLift;

@TeleOp

//@Disabled
public class CenterstageMain extends LinearOpMode {
    private ElapsedTime runtime     = new ElapsedTime();
    private enum ALLIANCE_COLOR {RED, BLUE}
    private enum START_POSITION {LEFT, RIGHT}
    private ALLIANCE_COLOR alianceColor = ALLIANCE_COLOR.RED;
    private START_POSITION startPosition = START_POSITION.LEFT;

    private SubSystemDrivetrain drivetrain=null;
    private SubSystemIntakeLift intakeLift = null;
    private SubSystemHangLift hangLift = null;

    private double joystickTranslateX = 0.0;
    private double joystickTranslateY = 0.0;
    private double joystickRotate = 0.0;
    private boolean intakeLiftGoTop = false;
    private boolean intakeLiftGoMId = false;
    private boolean intakeLiftGoBottom = false;
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
        intakeLift = new SubSystemIntakeLift(hardwareMap);
        hangLift   = new SubSystemHangLift(hardwareMap);

        gamepadsReset();
    }

    private void waitStart(){
        // Wait for the game to start (driver presses PLAY)
        //Use this time to run the vision code to detect team token position
        // Abort this loop if started or stopped.
        while (!(isStarted() || isStopRequested())) {
            if (gamepad1.a){alianceColor = ALLIANCE_COLOR.RED;}
            if (gamepad1.b){alianceColor = ALLIANCE_COLOR.BLUE;}

            if (gamepad1.left_bumper) {startPosition = START_POSITION.LEFT;}
            if (gamepad1.right_bumper) {startPosition = START_POSITION.RIGHT;}

            telemetry.addData("Alliance Color", alianceColor);
            telemetry.addData("Start position", startPosition);

            telemetry.addData("Iniyann is the greatest programmer in the world.", alianceColor);

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
        intakeLiftGoTop = gamepad1.dpad_up;
        intakeLiftGoMId = gamepad1.dpad_left;
        intakeLiftGoBottom = gamepad1.dpad_down;

        hangLiftControl  = gamepad1.left_trigger - gamepad1.right_trigger;
     }
    private void drivebaseUpdate()
    {
        double translateSpeed = Math.hypot(joystickTranslateX, joystickTranslateY);
        //Heading 0 = forward, -ve right, +ve left
        double heading = Math.atan2(-joystickTranslateX, -joystickTranslateY);

        drivetrain.doMecanumDrive(translateSpeed, heading, joystickRotate, TRUE);
    }

    private void telemetryUpdate()
    {
        telemetry.update();
    }

    private void intakeLiftUpdate()
    {
        if (intakeLiftGoTop)
        {
            intakeLift.setLiftPosition(intakeLift.liftMaxHeight);
        }
        else if (intakeLiftGoBottom)
        {
            intakeLift.setLiftPosition(0);
        }
    }

    private void hangLiftUpdate()
    {
        hangLift.setLift(hangLiftControl);
    }

    private void doTeleop()
    {
        while(opModeIsActive())
        {
            //Update the joystick reading
            gamepadsUpdate();
            //Process the joysticks for drivebase motion
            drivebaseUpdate();
            //Update the intake lift
            intakeLiftUpdate();
            //Update the hang lift
            hangLiftUpdate();
            //Update telemetry
            telemetryUpdate();
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

