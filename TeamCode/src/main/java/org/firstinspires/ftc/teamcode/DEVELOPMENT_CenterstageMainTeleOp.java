package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.SubSystemDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemDroneLaunch;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHangLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopper;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopperLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntakeLift;

@TeleOp

//@Disabled
public class DEVELOPMENT_CenterstageMainTeleOp extends LinearOpMode {
    private boolean endgame = false;//Set true to eliminate endgame protection
    private static double SPEED_MULTIPLIER = 1.2;
    private ElapsedTime pauseTimer = new ElapsedTime();
    private double pauseTimerDelay = 0;
    private ElapsedTime runtime     = new ElapsedTime();
    private boolean hangLiftHang = false;
    private boolean hangLiftDrop = false;

    public FtcDashboard dashboard;
    private boolean hopperOpenManual = false;
    private boolean hopperOpenAuto = false;

    private int hopperLiftPosition = 1;
    private double hopperLiftSpeed = 0;
    private boolean hopperExtendManual = false;
    private boolean hopperExtendAuto = false;
    private SubSystemIntakeLift intakeLift;

    private enum BACKDROP_ASSIST_STATE {ASSIST_WAIT, ASSIST_ROTATE, ASSIST_APPROACH, ASSIST_RAISE, ASSIST_DROP, ASSIST_RETRACT, ASSIST_PAUSE, ASSIST_DONE}
    private  BACKDROP_ASSIST_STATE backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_WAIT;
    private  BACKDROP_ASSIST_STATE backdropAssistStateReturn = BACKDROP_ASSIST_STATE.ASSIST_WAIT;
    private boolean doAutoDropPixel = false;
    static final double TURN_SPEED = 0.2;     // Max Turn speed to limit turn rate
    static final double DRIVE_SPEED = 0.4;     // Max driving speed for better distance accuracy.
    private double targetHeading = 0.0;
    private boolean FieldCentric = true;
    private SubSystemDrivetrain drivetrain=null;
    //private SubSystemIntake intake = null;
    private SubSystemHangLift hangLift = null;
    private SubSystemDroneLaunch drone = null;
    private SubSystemHopper hopper = null;
    private SubSystemHopperLift hopperLift = null;
    private double joystickTranslateX = 0.0;
    private double joystickTranslateY = 0.0;
    private double joystickRotate = 0.0;

    private boolean hangRelease = false;
    private boolean intakeRunning = true;
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
        drivetrain       = new SubSystemDrivetrain(hardwareMap,SubSystemVariables.currentBot);
        //intake           = new SubSystemIntake(hardwareMap);
        hangLift         = new SubSystemHangLift(hardwareMap);
        hopper           = new SubSystemHopper(hardwareMap);
        drone            = new SubSystemDroneLaunch(hardwareMap);
        hopperLift       = new SubSystemHopperLift(hardwareMap);
        intakeLift       = new SubSystemIntakeLift(hardwareMap);

        gamepadsReset();
    }
    private void waitStart(){
        // Wait for the game to start (driver presses PLAY)
        //Use this time to run the vision code to detect team token position
        // Abort this loop if started or stopped.
        while (!(isStarted() || isStopRequested())) {
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

    public void initializeDashboard() {
        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

    }

    private void gamepadsUpdate()
    {
        //////////////////////////////////////////////////////
        //Driver controls
        //////////////////////////////////////////////////////
        //Drive base motion controls
        joystickTranslateX = gamepad1.left_stick_x - joystick1LeftXOffset;
        joystickTranslateY = gamepad1.left_stick_y - joystick1LeftYOffset;
        joystickRotate     = gamepad1.right_stick_x - joystick1RightXOffset;

        if (gamepad1.y && hangRelease)
            hangLiftHang = true;
        else
            hangLiftHang = false;

        if(gamepad1.a)
            hangLiftDrop = true;
        else
            hangLiftDrop = false;

        if (gamepad1.b && gamepad1.dpad_right && endgame)
            hangRelease = true;

        if (gamepad1.start)
            doAutoDropPixel = true;
        else
            doAutoDropPixel = false;

        //////////////////////////////////////////////////////
        //Operator controls
        //////////////////////////////////////////////////////
        if(gamepad2.y)
            intakeRunning = true;
        else
            intakeRunning = false;

        hopperLiftSpeed = gamepad2.left_trigger - gamepad2.right_trigger;

        if(gamepad2.a)
            hopperOpenManual = true;
        else
            hopperOpenManual = false;

        if(gamepad2.dpad_up)
            hopperExtendManual = true;
        else
            hopperExtendManual = false;


        if(gamepad2.b && gamepad2.left_stick_button && endgame)
            droneLaunchState = true;

        if(gamepad1.left_bumper) {
            SPEED_MULTIPLIER = 1.7;
        }
        else if(gamepad1.right_bumper) {
            SPEED_MULTIPLIER = 0.6;
        }
        else {
            SPEED_MULTIPLIER = 1.2;
        }
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

    private void intakeUpdate()
    {
        if (intakeRunning) {
            intakeLift.setIntakeLiftPower(-0.5);
            telemetry.addLine("Intake running");
        }
        else
        {
            intakeLift.setIntakeLiftPower(-0.5);
            telemetry.addLine("Intake stopped");
        }
        telemetry.update();
    }

    private void hopperLiftUpdate() {
        /*
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
         */

        //For the moment just use the triggers to move the hopper lift up/down
        if ((hopperLiftSpeed > 0.1) && (hopperLift.getLiftPosition() < SubSystemVariables.HOPPER_LIFT_POS_MAX)){
            hopperLift.setHopperLiftPower(hopperLiftSpeed);
            hopperLift.setHopperLiftPosition(hopperLift.getLiftPosition() + 150);
        }
        else if ((hopperLiftSpeed < -0.1) && (hopperLift.getLiftPosition() > SubSystemVariables.HOPPER_LIFT_POS_MIN)){
            hopperLift.setHopperLiftPower(hopperLiftSpeed);
            hopperLift.setHopperLiftPosition(hopperLift.getLiftPosition() - 150);
        }
        else {
            hopperLift.setHopperLiftPower(.5);//Hold the current position at half power
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
        telemetry.addData("motor pos: ", hopperLift.getLiftPosition());
        telemetry.update();
    }

    private void hangLiftUpdate()
    {
        if(hangLiftHang && (hangLift.getHangLiftEncoder() > -5000)) {
            hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
            hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() - 150);
        } else if (hangLiftDrop && (hangLift.getHangLiftEncoder() < 0)) {
            hangLift.setHangLiftPower(SubSystemVariables.HANG_LIFT_HANG_POWER);
            hangLift.setHangLiftPos(hangLift.getHangLiftEncoder() + 150);
        }
        hangLift.SubSystemHangState(hangRelease);
    }

    private void droneUpdate()
    {
        if (droneLaunchState)
            drone.launchDrone(droneLaunchState);
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
        if (robotIsFacingBackdrop(20) && doAutoDropPixel && (pauseTimer.time() > 2.0)) {
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
                backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_APPROACH;
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
            if (backdropDistance > 20)//ToDo : Make this drive proportional to the distance away?
                drivetrain.driveHeading(DRIVE_SPEED, TURN_SPEED, targetHeading);
            else
                backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_RAISE;
        }
        else
            //Not holding "auto assist" button so exit
            //ToDo : Is there anything else we should do, or just drop out?
            backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_WAIT;
    }

    private void processDropAssistRaise(){
        //Start the lift going up, extending the hopper and a timer so we can ensure things are done before opening the gate
        //ToDo : Dynamic height
        hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_1);
        hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_UP);
        //Set a timer
        pauseTimer.reset();
        pauseTimerDelay = 0.2;
        backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_PAUSE;
        backdropAssistStateReturn = BACKDROP_ASSIST_STATE.ASSIST_DROP;
    }

    private void processDropAssistPause(){
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
        if (doAutoDropPixel) {
            double backdropDistance = drivetrain.getFrontDistanceSensor();
            if (backdropDistance < 50)
                drivetrain.driveHeading(DRIVE_SPEED, TURN_SPEED, targetHeading);
            else
                backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_DONE;
        }
        else
            //Not holding "auto assist" button so exit, cleaning up as necessary
            backdropAssistState = BACKDROP_ASSIST_STATE.ASSIST_DONE;
    }

    private void processDropAssistDone(){
        hopper.openGate(false);
        hopper.setHopperPosition(SubSystemVariables.HOPPER_POS_DOWN);
        hopperLift.setHopperLiftPosition(SubSystemVariables.HOPPER_LIFT_POS_DOWN);
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

    private void updateSubSystems() {
        //Update the hang lift
        hangLiftUpdate();
        //Update the drone launch
        droneUpdate();
        if (!doAutoDropPixel) {//Only process the driver/operator joystick actions if NOT running auto assist
            //Updates the hopper gate and hopper positions
            hopperUpdate();
            //Update
            intakeUpdate();
            //Updates the hopper lift position
            hopperLiftUpdate();
        }
    }

    private void doTeleop()
    {
        while(opModeIsActive())
        {
            if(runtime.seconds() > 90) {
                endgame = true;
            }

            //Update the joystick reading
            gamepadsUpdate();
            if (!doAutoDropPixel)//Only process the driver joystick actions if NOT running auto assist
                //Process the joysticks for drivebase motion
                drivebaseUpdate();
            //Update assist state machine
            pixelDropAssistUpdate();
            //Update lift, hopper etc...
            updateSubSystems();
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
        //Initalizes FTC Dashboard
        initializeDashboard();
        //Need to call this at least once to set the servos etc...
        updateSubSystems();
        waitStart();
        runtime.reset();

        doTeleop();

        //Done so turn everything off now
        disableHardware();
    }

}

