package org.firstinspires.ftc.teamcode;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.SubSystemDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemLift;

@TeleOp

//@Disabled
public class CenterstageMain extends LinearOpMode {
    private ElapsedTime runtime     = new ElapsedTime();
    private enum ALLIANCE_COLOR {RED, BLUE}
    private enum START_POSITION {LEFT, RIGHT}
    private ALLIANCE_COLOR alianceColor = ALLIANCE_COLOR.RED;
    private START_POSITION startPosition = START_POSITION.LEFT;

    private SubSystemDrivetrain drivetrain=null;
    private SubSystemLift lift = null;

    private double joystickTranslateX = 0.0;
    private double joystickTranslateY = 0.0;
    private double joystickRotate = 0.0;
    private double liftControl = 0.0;

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
        lift = new SubSystemLift(hardwareMap);
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
            telemetry.update();
            idle();
        }
    }

    /**
     * Disable all hardware
     */
    private void disableHardware() {

        drivetrain.disableDrivetrainMotors();
        lift.setLift(0);
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
        joystickTranslateX = gamepad1.left_stick_x - joystick1LeftXOffset;
        joystickTranslateY = gamepad1.left_stick_y - joystick1LeftYOffset;
        joystickRotate     = gamepad1.right_stick_x - joystick1RightXOffset;
        liftControl        = gamepad1.left_trigger - gamepad1.right_trigger;
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
        telemetry.addData("Lift position", lift.getLiftEncoders());
        telemetry.addData("Lift control", liftControl);
        telemetry.update();
    }

    private void liftUpdate()
    {
        lift.setLift(liftControl);
        if (gamepad1.left_bumper)
        {
            lift.setLiftPosition(lift.liftMaxHeight);
        }
        else if (gamepad1.right_bumper)
        {
            lift.setLiftPosition(0);
        }
    }

    private void doTeleop()
    {
        while(opModeIsActive())
        {
            //Update the joystick reading
            gamepadsUpdate();
            //Process the joysticks for drivebase motion
            drivebaseUpdate();
            liftUpdate();
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

