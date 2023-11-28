package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.SubSystemClaw;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemClawArm;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemDroneLaunch;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHangLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopper;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopperIntakeLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopperLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntakeLift;

@TeleOp

//@Disabled
public class DEVELOPMENT_CenterstageMainTeleOp extends LinearOpMode {
    private ElapsedTime runtime     = new ElapsedTime();
    private boolean hangLiftHang = false;
    private int intakeLiftPosition = 0;
    private boolean clawClosed = false;
    private boolean togglePressed;
    private boolean lastTogglePressed;
    private String hopperState = "down";
    private boolean hopperOpen;

    private int hopperLiftPosition = 1;
    private boolean hangLiftDrop;

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
    private SubSystemClaw claw = null;
    private SubSystemDroneLaunch drone = null;
    private SubSystemClawArm clawArm = null;
    private SubSystemHopper hopper = null;
    private SubSystemHopperLift hopperLift = null;
    private SubSystemHopperIntakeLift hopperIntakeLift = null;

    private double joystickTranslateX = 0.0;
    private double joystickTranslateY = 0.0;
    private double joystickRotate = 0.0;

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

    private int liftpos;
    private boolean droneLaunchState = false;

    public void initHardware() throws InterruptedException {
        drivetrain       = new       SubSystemDrivetrain(hardwareMap);
        intakeLift       = new       SubSystemIntakeLift(hardwareMap);
        hangLift         = new         SubSystemHangLift(hardwareMap);
        clawArm          = new          SubSystemClawArm(hardwareMap);
        claw             = new             SubSystemClaw(hardwareMap);
        hopper           = new           SubSystemHopper(hardwareMap);
        drone            = new      SubSystemDroneLaunch(hardwareMap);
        hopperLift       = new       SubSystemHopperLift(hardwareMap);
        hopperIntakeLift = new SubSystemHopperIntakeLift(hardwareMap);

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

            telemetry.addLine("Iniyann is the greatest programmer and driver alive");

            telemetry.update();
            idle();
        }
    }

    /**
     * Disable all hardware
     */
    private void disableHardware() {

        drivetrain.disableDrivetrainMotors();
        intakeLift.setIntakeLiftPower(SubSystemVariables.INTAKE_LIFT_POWER);
        hangLift.setHangLiftPower(0);
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

        /*
        if (gamepad1.y && hangRelease) {
            hangLiftHang = true;
        } else {
            hangLiftHang = false;
        }

        if(gamepad1.a) {
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
         */

        if(gamepad2.y) {
            hopperIntakeLift.setHopperIntakeLiftPower(-.5);
            telemetry.addLine("HopperLift is running now");
            telemetry.update();
        } else {
            hopperIntakeLift.setHopperIntakeLiftPower(0);
            telemetry.addLine("HopperLift is not running now");
            telemetry.update();
        }

        if(hopperLift.getLiftEncoders() < SubSystemVariables.HOPPER_LIFT_POS_MIN && hopperLift.getLiftEncoders() > SubSystemVariables.HOPPER_LIFT_POS_MAX) {
            hopperLift.setHopperLiftPower(gamepad2.left_trigger - gamepad2.right_trigger);
        }

        if(gamepad2.a) {
            hopper.openGate(true);
        } else {
            hopper.openGate(false);
        }

        if(gamepad2.dpad_up) {
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_3);
        } else {
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_1);
        }


        /*
        if(gamepad1.left_trigger > 0.1) { // -3200 is max
            hopperLift.setHopperLiftPower(SubSystemVariables.HOPPER_LIFT_POWER);
        } else if (gamepad1.right_trigger > 0.1 ) {
            hopperLift.setHopperLiftPower(SubSystemVariables.HOPPER_LIFT_POWER);
        } else {
            hopperLift.setHopperLiftPower(0);
        }
        */

        //driveModeChangeButton = gamepad1.x;
    }

    private void hopperUpdate() {
        //hopper.openGate(hopperOpen);
    }

    private void hopperLiftUpdate() {
        if(hopperLiftPosition == 1) {
            hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_1);
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_1);
        }

        if(hopperLiftPosition == 2) {
            hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_2);
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_2);
        }

        if(hopperLiftPosition == 3) {
            hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_3);
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_3);
        }

        if(hopperLiftPosition == 4) {
            hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_4);
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_4);
        }
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
        telemetry.addLine("Iniyann is the greatest programmer and driving in the whole world");
        telemetry.addLine("Ashwin is so slay xoxo");
        telemetry.addLine("Ashwin is the coolest and best monke ever xoxo");
        telemetry.addData("motor pos: ", hopperLift.getLiftEncoders());
        telemetry.update();
    }


    private void intakeLiftUpdate() {
        if(intakeLiftPosition == 0) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_0);
            intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_0);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_1;
            intakeLift.setIntakeLiftPower(1);
        }
        else if(intakeLiftPosition == 1) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_1);
            intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_1);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_1;
            intakeLift.setIntakeLiftPower(1);
        }
        else if(intakeLiftPosition == 2) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_2);
            intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_2);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_2;
            intakeLift.setIntakeLiftPower(1);
        }
        else if(intakeLiftPosition == 3) {
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_3);
            intakeLift.setLiftPosition(SubSystemVariables.INTAKE_LIFT_POS_3);
            liftpos = SubSystemVariables.INTAKE_LIFT_POS_3;
            intakeLift.setIntakeLiftPower(1);
        }
    }

    private void clawUpdate() {
        claw.closeClaw(clawClosed);
    }

    private void hangLiftUpdate()
    {
        if(hangLiftHang) {
            //Move the intake lift out of the way
             /*
             intakeLiftPosition = 2;
            hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
            hangLift.setHangLiftPos(SubSystemVariables.HANG_LIFT_POS_HANG);
              */
            hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
            hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() - 150);
        } else if (hangLiftDrop) {
            hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
            hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() + 150);
        } else {
            //hangLift.setHangLiftPos(hangLift.getHangLiftEncoder());
        }
        hangLift.SubSystemHangState(hangRelease);
    }

    private void droneUpdate()
    {
        if (droneLaunchState)
            drone.launchDrone();
    }

    private void updateSubSystems() {
        //Update the intake
        //intakeLiftUpdate();
        //Update the hang lift
        hangLiftUpdate();
        //Update the drone launch
        droneUpdate();
        //Updates the claw servo
        clawUpdate();
        //Updates the hopper gate and hopper position
        hopperUpdate();
        //Updates the hopper lift position
        //hopperLiftUpdate();
    }

    private void doTeleop()
    {
        while(opModeIsActive())
        {
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

