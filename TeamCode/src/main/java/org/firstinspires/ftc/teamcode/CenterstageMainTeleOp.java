package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.subsystems.SubSystemClaw;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemClawArm;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemDroneLaunch;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHangLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntake;
//import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntakeLift;

@TeleOp
@Config
//@Disabled
public class CenterstageMainTeleOp extends LinearOpMode {
    public static double SPEED_MULTIPLIER = 1.2;
    private ElapsedTime runtime     = new ElapsedTime();
    private boolean hangLiftHang = false;
    private int intakeLiftPosition = 0;
    private boolean clawClosed = false;
    private boolean togglePressed;
    private boolean lastTogglePressed;
    private boolean hangLiftDrop;
    private boolean okayDoEndGame = !SubSystemVariables.protectEndgame;

    public FtcDashboard dashboard;
    private IMU imu;


    private enum ALLIANCE_COLOR {RED, BLUE}
    private enum START_POSITION {LEFT, RIGHT}
    private ALLIANCE_COLOR alianceColor = ALLIANCE_COLOR.RED;
    private START_POSITION startPosition = START_POSITION.LEFT;

    private boolean FieldCentric = true;
    private boolean lastButtonState = false;
    private boolean driveModeChangeButton = false;

    private SubSystemDrivetrain drivetrain=null;
 //   private SubSystemIntakeLift intakeLift = null;
    private SubSystemHangLift hangLift = null;
    private SubSystemIntake intake = null;
    private SubSystemClaw claw = null;
    private SubSystemDroneLaunch drone = null;

    private SubSystemClawArm clawArm = null;

    private double joystickTranslateX = 0.0;
    private double joystickTranslateY = 0.0;
    private double joystickRotate = 0.0;

    private boolean hangRelease = false;
    private double hangLiftControl = 0.0;
    private int intakeDirection = 0;

    private double joystick1LeftXOffset = 0.0;
    private double joystick1LeftYOffset = 0.0;
    private double joystick1RightXOffset = 0.0;
    private double joystick1RightYOffset = 0.0;
    private double joystick2LeftXOffset = 0.0;
    private double joystick2LeftYOffset = 0.0;
    private double joystick2RightXOffset = 0.0;
    private double joystick2RightYOffset = 0.0;

    private int liftpos;

    public static boolean droneLaunchState = false;

    public void initHardware() throws InterruptedException {
        drivetrain = new SubSystemDrivetrain(hardwareMap, SubSystemVariables.currentBot);
        //drivetrain.resetGyro();
//        intakeLift = new SubSystemIntakeLift(hardwareMap);
        hangLift   = new SubSystemHangLift(hardwareMap);
        clawArm    = new SubSystemClawArm(hardwareMap);
        claw       = new SubSystemClaw(hardwareMap);
        drone      = new SubSystemDroneLaunch(hardwareMap);
        intake = new SubSystemIntake(hardwareMap);

        clawClosed = false;
        okayDoEndGame = !SubSystemVariables.protectEndgame;
        hangLiftHang = false;
        FieldCentric = true;
        lastButtonState = false;
        driveModeChangeButton = false;
        hangRelease = false;
        droneLaunchState = false;

        gamepadsReset();
    }
    private void waitStart(){
        // Wait for the game to start (driver presses PLAY)
        //Use this time to run the vision code to detect team token position
        // Abort this loop if started or stopped.
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        while (!(isStarted() || isStopRequested())) {
            if(gamepad1.right_bumper && gamepad1.right_trigger > 0.5 && gamepad1.start && gamepad2.right_bumper && gamepad2.right_trigger > 0.5 && gamepad2.start) {
                drivetrain.resetGyro();
            }
            if (gamepad1.x && gamepad1.y && gamepad1.a && gamepad1.b)
            {
                drivetrain.resetGyro();
            }
            idle();
        }
    }

    /**
     * Disable all hardware
     */
    private void disableHardware() {

        drivetrain.disableDrivetrainMotors();
        //intakeLift.setIntakeLiftPower(SubSystemVariables.INTAKE_LIFT_POWER);
        hangLift.setHangLiftPower(0);
    }

    private void gamepadsReset()
    {
        //Measure 'at rest' joystick positions
        joystick1LeftXOffset = 0;//gamepad1.left_stick_x;
        joystick1RightXOffset = 0;//gamepad1.right_stick_x;

        joystick1LeftYOffset = 0;//gamepad1.left_stick_y;
        joystick1RightYOffset = 0;//gamepad1.right_stick_y;


        joystick2LeftXOffset = 0;//gamepad2.left_stick_x;
        joystick2RightXOffset = 0;//gamepad2.right_stick_x;

        joystick2LeftYOffset = 0;//gamepad2.left_stick_y;
        joystick2RightYOffset = 0;//gamepad2.right_stick_y;
    }

    public void initializeDashboard() {
        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

    }
    private void gamepadsUpdate()
    {
        //Drive base motion controls
        joystickTranslateX = gamepad1.left_stick_x - joystick1LeftXOffset;
        joystickTranslateY = gamepad1.left_stick_y - joystick1LeftYOffset;
        joystickRotate     = gamepad1.right_stick_x - joystick1RightXOffset;

        if (gamepad1.y && hangRelease) {
            hangLiftHang = true;
        } else {
            hangLiftHang = false;
        }

        if(gamepad1.a && hangRelease) {
            hangLiftDrop = true;
        } else {
            hangLiftDrop = false;
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
            intakeLiftPosition = 0;
        }


        if(gamepad2.left_bumper) {
            clawClosed = false;
        }

        if(gamepad2.right_bumper) {
            clawClosed = true;
        }

        if((gamepad2.right_trigger > 0.5) || (gamepad2.left_trigger > 0.5)) {
            togglePressed = true;
        } else {
            togglePressed = false;
        }

        if(togglePressed && !lastTogglePressed) {
            clawClosed = !clawClosed;
        }

        lastTogglePressed = togglePressed;

        if(gamepad2.b && gamepad2.left_stick_button && okayDoEndGame) {
            droneLaunchState = true;
        }

        if (gamepad2.a)
            intakeDirection = 1;
        else if (gamepad2.y)
            intakeDirection = -1;
        else
            intakeDirection = 0;

        driveModeChangeButton = gamepad1.x;

        if (gamepad1.b && gamepad1.dpad_right && okayDoEndGame)
            hangRelease = true;

        if(gamepad1.left_bumper) {
            SPEED_MULTIPLIER = 1.7;
        }
        else if(gamepad1.right_bumper) {
            SPEED_MULTIPLIER = 0.6;
        }
        else {
            SPEED_MULTIPLIER = 1.0;
        }

 //       if(!gamepad1.left_bumper && !gamepad1.right_bumper) {
 //           SPEED_MULTIPLIER = 1;
 //       }

        if(gamepad1.right_bumper && gamepad1.right_trigger > 0.5 && gamepad1.start && gamepad2.right_bumper && gamepad2.right_trigger > 0.5 && gamepad2.start) {
            imu.resetYaw();
        }
     }
    private void drivebaseUpdate()
    {
        double translateSpeed = Math.hypot(joystickTranslateX, joystickTranslateY) * SPEED_MULTIPLIER;
        //Heading 0 = forward, -ve right, +ve left
        double heading = Math.atan2(-joystickTranslateX, -joystickTranslateY);

        drivetrain.doMecanumDrive(translateSpeed, heading, joystickRotate, FieldCentric);
    }

    private void telemetryUpdate()
    {
        telemetry.addData("Timer: ", runtime.seconds() );
        telemetry.addData("Hang release state: ", hangRelease);
        telemetry.addData("Hang lift position", hangLift.getHangLiftEncoder());
        telemetry.addData("Drone launch state", droneLaunchState);
        telemetry.addData("front left power ", SubSystemDrivetrain.FLP);
        telemetry.addData("front right power ", SubSystemDrivetrain.FRP);
        telemetry.addData("back left power ", SubSystemDrivetrain.BLP);
        telemetry.addData("back right power ", SubSystemDrivetrain.BRP);
        telemetry.update();
    }


    private void intakeLiftUpdate() {
        if(intakeLiftPosition == 0) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_0);
            //intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_0);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_1;
            //intakeLift.setIntakeLiftPower(1);
        }
        else if(intakeLiftPosition == 1) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_1);
            //intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_1);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_1;
            //intakeLift.setIntakeLiftPower(1);
        }
        else if(intakeLiftPosition == 2) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_2);
            //intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_2);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_2;
            //intakeLift.setIntakeLiftPower(1);
        }
        else if(intakeLiftPosition == 3) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_3);
            //intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_3);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_3;
            //intakeLift.setIntakeLiftPower(1);
        }
    }

    private void clawUpdate() {
        claw.closeClaw(clawClosed);
    }

    private void hangLiftUpdate()
    {
        if (SubSystemVariables.currentBot == 1) //Original robot
        {
            if (hangLiftHang && (hangLift.getHangLiftEncoder() > -5000)) {
                hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
                hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() - 150);
            } else if (hangLiftDrop && (hangLift.getHangLiftEncoder() < 0)) {
                hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
                hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() + 150);
            }
        }
        else//New robot
        {
            if (hangLiftHang && (hangLift.getHangLiftEncoder() < 3500)) {
                hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
                hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() + 150);
            } else if (hangLiftDrop && (hangLift.getHangLiftEncoder() > 0)) {
                hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_DROP_POWER);
                hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() - 150);
            }
        }
         hangLift.SubSystemHangState(hangRelease);
    }

    private void droneUpdate()
    {
        drone.launchDrone(droneLaunchState);
    }

    private void intakeUpdate()
    {
        if (intakeDirection == 1)
        {
            intake.setIntakeRiserPositionUp(false);
            intake.setIntakePower(SubSystemVariables.INTAKE_INTAKE_SPEED);
        }
        else if (intakeDirection == -1)
        {
            intake.setIntakeRiserPositionUp(true);
            intake.setIntakePower(SubSystemVariables.INTAKE_OUTTAKE_SPEED);
        }
        else
        {
            intake.setIntakeRiserPositionUp(true);
            intake.setIntakePower(0);
        }
    }
    private void updateSubSystems()
    {
        //Update the intake
        intakeLiftUpdate();
        //Update the hang lift
        hangLiftUpdate();
        //Update the drone launch
        droneUpdate();
        //Updates the claw servo
        clawUpdate();
        intakeUpdate();
    }

    private void doTeleop()
    {
        while(opModeIsActive())
        {
            if(runtime.seconds() > 90) {
                okayDoEndGame = true;
            }

            //Update the joystick reading
            gamepadsUpdate();
            //Process the joysticks for drivebase motion
            drivebaseUpdate();
            //Update lift, claw etc...
            updateSubSystems();
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
        //Initalizes FTC Dashboard
        initializeDashboard();
        //Grab the pixel before moving anything else
        clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_0);
        //sleep(200);
        updateSubSystems();
        waitStart();
        runtime.reset();

        doTeleop();

        //Done so turn everything off now
        disableHardware();
    }

}

