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
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopper;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopperLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntake;
//import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntakeLift;

@TeleOp
@Config
//@Disabled
public class CenterstageMainTeleOp extends LinearOpMode {
    private static final double TURN_SPEED = 0.2;
    private static final double DRIVE_SPEED = 0.4;
    public static double SPEED_MULTIPLIER = 1.2;
    private ElapsedTime pauseTimer = new ElapsedTime();
    private boolean hangLiftHang = false;
    private int intakeLiftPosition = 0;
    private boolean clawClosed = false;
    private boolean togglePressed;
    private boolean lastTogglePressed;
    private boolean hangLiftDrop;
    private boolean okayDoEndGame = !SubSystemVariables.protectEndgame;

    public FtcDashboard dashboard;
    private IMU imu;
    private SubSystemHopper hopper;
    private SubSystemHopperLift hopperLift;
    private boolean doAutoDropPixel = false;
    private double targetHeading = 0.0;
    private double pauseTimerDelay = 0;
    private boolean hopperOpenManual = false;
    private boolean hopperOpenAuto = false;
    private boolean hopperExtendManual = false;
    private boolean hopperExtendAuto = false;
    private int hopperLiftPosition;
    private double hopperLiftSpeed = 1;
    private boolean hopperLiftDown;
    private boolean hopperLiftUp;
    private int deltaMultiplier = 1;

    private enum BACKDROP_ASSIST_STATE {ASSIST_WAIT, ASSIST_ROTATE, ASSIST_APPROACH, ASSIST_RAISE, ASSIST_DROP, ASSIST_RETRACT, ASSIST_PAUSE, HOPPER_OUT, ASSIST_DONE}
    private BACKDROP_ASSIST_STATE backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_WAIT;
    private BACKDROP_ASSIST_STATE backdropAssistStateReturn = BACKDROP_ASSIST_STATE.ASSIST_WAIT;


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
        intake     = new SubSystemIntake(hardwareMap);
        hopper     = new SubSystemHopper(hardwareMap);
        hopperLift = new SubSystemHopperLift(hardwareMap);

        clawClosed = false;
        okayDoEndGame = !SubSystemVariables.protectEndgame;
        hangLiftHang = false;
        FieldCentric = true;
        lastButtonState = false;
        driveModeChangeButton = false;
        hangRelease = false;
        droneLaunchState = false;
        hopperLiftPosition = 0;

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

        if(SubSystemVariables.currentBot == 1) {
            if (gamepad2.dpad_up) {
                intakeLiftPosition = 0;
            }
            if (gamepad2.dpad_down) {
                intakeLiftPosition = 1;
            }
            if (gamepad2.dpad_left) {
                intakeLiftPosition = 2;
            }
        } else {
            /*
            if(gamepad2.dpad_up) {
                hopperLiftUp = true;
            } else {
                hopperLiftUp = false;
            }

            if(gamepad2.dpad_down) {
                hopperLiftDown = true;
            } else {
                hopperLiftDown = false;
            }
             */

            if(gamepad2.dpad_down) {
                deltaMultiplier = 1;
            }
            if(gamepad2.dpad_left) {
                deltaMultiplier = 2;
            }
            if(gamepad2.dpad_right) {
                deltaMultiplier = 3;
            }
            if(gamepad2.dpad_up) {
                deltaMultiplier = 4;
            }
        }

        if(gamepad1.dpad_down) {
            deltaMultiplier = 1;
        }
        if(gamepad1.dpad_left) {
            deltaMultiplier = 2;
        }
        if(gamepad1.dpad_right) {
            deltaMultiplier = 3;
        }
        if(gamepad1.dpad_up) {
            deltaMultiplier = 4;
        }


        if(gamepad2.x) {
           hopperOpenManual = true;
        } else {
            hopperOpenManual = false;
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

        //driveModeChangeButton = gamepad1.x;

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

        if(gamepad2.dpad_right) {
            hopperExtendManual = true;
        } else {
            hopperExtendManual = false;
        }

 //       if(!gamepad1.left_bumper && !gamepad1.right_bumper) {
 //           SPEED_MULTIPLIER = 1;
 //       }

        if(gamepad1.right_bumper && gamepad1.right_trigger > 0.5 && gamepad1.start && gamepad2.right_bumper && gamepad2.right_trigger > 0.5 && gamepad2.start) {
            imu.resetYaw();
        }
        if ((gamepad1.x && gamepad1.y && gamepad1.a && gamepad1.b)) {
            drivetrain.resetGyro();
        }


        if( (gamepad2.start) && (SubSystemVariables.currentBot == 0))
            doAutoDropPixel = true;
        else
            doAutoDropPixel = false;
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
        /*
        telemetry.addData("Timer: ", pauseTimer.seconds() );
        telemetry.addData("Hang release state: ", hangRelease);
        telemetry.addData("Hang lift position", hangLift.getHangLiftEncoder());
        telemetry.addData("Drone launch state", droneLaunchState);
        telemetry.addData("front left power ", SubSystemDrivetrain.FLP);
        telemetry.addData("front right power ", SubSystemDrivetrain.FRP);
        telemetry.addData("back left power ", SubSystemDrivetrain.BLP);
        telemetry.addData("back right power ", SubSystemDrivetrain.BRP);
         */
        if (SubSystemVariables.currentBot == 0) {
            telemetry.addData("Front distance sensor val: ", drivetrain.getFrontDistanceSensor());
            telemetry.addData("Hopper lift pos: ", hopperLift.getLiftPosition());
            telemetry.addData("State: ", backdropAssistState);
            telemetry.addData("robot facing backdrop: ", robotIsFacingBackdrop(20));
            telemetry.addData("do auto drop pixel: ", doAutoDropPixel);
            telemetry.addData("pause timer > 2.0: ", (pauseTimer.time() > 2.0));
        }
        telemetry.update();
    }

    private boolean robotIsFacingBackdrop(double margin){
        //Is the robot facing the backdrop (ish)?
        //Within the specified error angle of the backdrop?
        //Make read and blue the same. Technically this would also allow the bot to face away
        //from the backdrop, but good enough for the moment
        double currentAbsHeading = Math.abs(drivetrain.getCurrentHeading(false));//Result in degrees
        //Robot heading is 90 degrees rotated to the 'field' 0 heading
        if (Math.abs((currentAbsHeading - 90)) < margin)
            return true;
        else
            return false;
    }
    private void processDropAssistWait(){
        //Waiting for the robot to be in the 'safe' place and the driver to say "go"
        //If yes then move to the rotate state
        if (robotIsFacingBackdrop(40) && doAutoDropPixel && (pauseTimer.time() > 2.0)) {
            backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_ROTATE;
            if (drivetrain.getCurrentHeading(false) > 0) //Positive implies we were blue and rotated CCW
                targetHeading = 90;//Note, at the moment the heading is robot centric
            else
                targetHeading = -90;//Note, at the moment the heading is robot centric
        }
    }

    private void processDropAssistRotate() {
        //If still pressing the auto assist then keep rotating until facing the backdrop
        if (doAutoDropPixel){
            if (robotIsFacingBackdrop(3)){//Within 3 degrees?
                //Facing the backdrop so move to approach state
                drivetrain.disableDrivetrainMotors();
                backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_RAISE;
                //ToDo : Should we move the 'lift' and 'extend" here too to speed things up?
                //We could start a timer so we know how long we have allowed the lift and servo to be active since can use run to position
            }
            else {
                //Not aligned so keep rotating
                drivetrain.turnHeading(TURN_SPEED, targetHeading);
            }
        }
        else {
            //Not holding "auto assist" button so exit
            //ToDo : Is there anything else we should do, or just drop out?
            backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_WAIT;
        }
    }

    private void processDropAssistApproach(){
        //If still pressing the auto assist then keep approaching the backdrop
        if (doAutoDropPixel) {
            double backdropDistance = drivetrain.getFrontDistanceSensor();
            if (backdropDistance > 200)//ToDo : Make this drive proportional to the distance away?
                drivetrain.driveHeading(-DRIVE_SPEED / 2.0, TURN_SPEED, targetHeading);
            else {
                if(backdropDistance > 80) {
                    drivetrain.driveHeading(-DRIVE_SPEED + 0.3 /* -0.1 */, TURN_SPEED, targetHeading);
                } else {
                    drivetrain.driveHeading(0, 0, targetHeading);
                    backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_DROP;
                }
            }
        }
        else
            //Not holding "auto assist" button so exit
            //ToDo : Is there anything else we should do, or just drop out?
            backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_WAIT;
    }

    private void processDropAssistRaise() {
        //Start the lift going up, extending the hopper and a timer so we can ensure things are done before opening the gate
        //ToDo : Dynamic height
        hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_1 + (SubSystemVariables.HOPPER_LIFT_POS_DELTA * deltaMultiplier));

        backdropAssistState = BACKDROP_ASSIST_STATE.HOPPER_OUT;
    }

    private void proccessDropAssistHopperOut() {
        hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_UP);

        pauseTimer.reset();
        pauseTimerDelay = 0.5;

        backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_PAUSE;
        backdropAssistStateReturn = BACKDROP_ASSIST_STATE.ASSIST_APPROACH;
    }

    private void processDropAssistPause() {
        if (pauseTimer.time() > pauseTimerDelay)
            //Pause has expired so return to desired state
            backdropAssistState = backdropAssistStateReturn;
    }

    private void processDropAssistDrop(){
        hopper.openGate(true);
        pauseTimer.reset();
        pauseTimerDelay = 0.2;
        backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_PAUSE;
        backdropAssistStateReturn = BACKDROP_ASSIST_STATE.ASSIST_RETRACT;
    }

    private void processDropAssistRetract(){
        //If still pressing the auto assist then keep backing away from the backdrop
            double backdropDistance = drivetrain.getFrontDistanceSensor();
            if ((backdropDistance < 250) || (doAutoDropPixel))
                drivetrain.driveHeading(DRIVE_SPEED, TURN_SPEED, targetHeading);
            else
                backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_DONE;

    }

    private void processDropAssistDone(){
        hopper.openGate(false);
        hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_DOWN);
        hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_DOWN);
        drivetrain.driveHeading(0, 0, targetHeading);
        //Use the timer to make sure that we don't immediately start dropping a pixel again if we don't let go of the button !!
        //Don't call the pause state though so that we can manually move around now
        pauseTimer.reset();
        backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_WAIT;
    }

    private void pixelDropAssistUpdate(){
        switch (backdropAssistState){
            case ASSIST_WAIT:{
                processDropAssistWait();
                break;
            }
            case ASSIST_ROTATE:{
                processDropAssistRotate();
                break;
            }
            case ASSIST_APPROACH: {
                processDropAssistApproach();
                break;
            }
            case ASSIST_RAISE:{
                processDropAssistRaise();
                break;
            }
            case ASSIST_PAUSE:{
                processDropAssistPause();
                break;
            }

            case HOPPER_OUT: {
                proccessDropAssistHopperOut();
                break;
            }
            case ASSIST_DROP:{
                processDropAssistDrop();
                break;
            }
            case ASSIST_RETRACT:{
                processDropAssistRetract();
                break;
            }
            case ASSIST_DONE:{
                processDropAssistDone();
            }
        }
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
            if (hangLiftHang && (hangLift.getHangLiftEncoder() < 5000)) {
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

        if (!doAutoDropPixel) {//Only process the driver/operator joystick actions if NOT running auto assist
            //Updates the hopper gate and hopper positions
            intakeUpdate();
            //Updates the hopper lift position
            hopperUpdate();

            hopperLiftUpdate();

        }

    }

    private void hopperLiftUpdate() {
        hopperLift.setHopperLiftPower(hopperLiftSpeed);

        //For the moment just use the triggers to move the hopper lift up/down
        if (hopperLiftDown && (hopperLift.getLiftPosition() < SubSystemVariables.HOPPER_LIFT_POS_MIN)){
            hopperLift.setHopperLiftPosition(hopperLift.getLiftPosition() + 150);
        }
        else if (hopperLiftUp && (hopperLift.getLiftPosition() > SubSystemVariables.HOPPER_LIFT_POS_MAX)){
            hopperLift.setHopperLiftPosition(hopperLift.getLiftPosition() - 150);
        }
        else {
            hopperLift.setHopperLiftPower(.5);//Hold the current position at half power
        }

        //hopperLift.setHopperLiftPower(hopperLiftSpeed);
        //hopperLift.setHopperLiftPosition(hopperLiftPosition);
    }

    private void hopperUpdate() {
        //Pixel gate control
        hopper.openGate(hopperOpenManual || hopperOpenAuto);

        //Hopper extend position
        if (hopperExtendManual || hopperExtendAuto){
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_3);
        }
        else {
            hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_1);
        }
    }

    private void doTeleop()
    {
        SubSystemIntake.intakeRiserServo.setPosition(SubSystemIntake.riserUpPosition);
        while(opModeIsActive())
        {
            if(pauseTimer.seconds() > 90) {
                okayDoEndGame = true;
            }

            //Update the joystick reading
            gamepadsUpdate();
            //Update lift, claw etc...
            updateSubSystems();
            //Update telemetry
            telemetryUpdate();

            if(driveModeChangeButton && !lastButtonState) {
     //           FieldCentric = !FieldCentric;
            }
            if (SubSystemVariables.currentBot == 0)
                pixelDropAssistUpdate();
            if (!doAutoDropPixel)//Only process the driver joystick actions if NOT running auto assist
                //Process the joysticks for drivebase motion
                drivebaseUpdate();

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
        pauseTimer.reset();

        doTeleop();

        //Done so turn everything off now
        disableHardware();
    }

}

