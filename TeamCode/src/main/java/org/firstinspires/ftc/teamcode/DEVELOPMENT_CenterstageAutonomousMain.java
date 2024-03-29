/* Copyright (c) 2022 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

//import static org.firstinspires.ftc.teamcode.SubSystemVariables.ALLIANCE_COLOR.BLUE;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.acmerobotics.dashboard.FtcDashboard;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemClaw;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemClawArm;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemDrivetrain;

/*
 *  This OpMode illustrates the concept of driving an autonomous path based on Gyro (IMU) heading and encoder counts.
 *  The code is structured as a LinearOpMode
 *
 *  The path to be followed by the robot is built from a series of drive, turn or pause steps.
 *  Each step on the path is defined by a single function call, and these can be strung together in any order.
 *
 *  The code REQUIRES that you have encoders on the drive motors, otherwise you should use: RobotAutoDriveByTime;
 *
 *  This code uses the Universal IMU interface so it will work with either the BNO055, or BHI260 IMU.
 *  To run as written, the Control/Expansion hub should be mounted horizontally on a flat part of the robot chassis.
 *  The REV Logo should be facing UP, and the USB port should be facing forward.
 *  If this is not the configuration of your REV Control Hub, then the code should be modified to reflect the correct orientation.
 *
 *  This sample requires that the drive Motors have been configured with names : left_drive and right_drive.
 *  It also requires that a positive power command moves both motors forward, and causes the encoders to count UP.
 *  So please verify that both of your motors move the robot forward on the first move.  If not, make the required correction.
 *  See the beginning of runOpMode() to set the FORWARD/REVERSE option for each motor.
 *
 *  This code uses RUN_TO_POSITION mode for driving straight, and RUN_USING_ENCODER mode for turning and holding.
 *  Note: This code implements the requirement of calling setTargetPosition() at least once before switching to RUN_TO_POSITION mode.
 *
 *  Notes:
 *
 *  All angles are referenced to the coordinate-frame that is set whenever resetHeading() is called.
 *  In this sample, the heading is reset when the Start button is touched on the Driver station.
 *  Note: It would be possible to reset the heading after each move, but this would accumulate steering errors.
 *
 *  The angle of movement/rotation is assumed to be a standardized rotation around the robot Z axis,
 *  which means that a Positive rotation is Counter Clockwise, looking down on the field.
 *  This is consistent with the FTC field coordinate conventions set out in the document:
 *  https://ftc-docs.firstinspires.org/field-coordinate-system
 *
 *  Control Approach.
 *
 *  To reach, or maintain a required heading, this code implements a basic Proportional Controller where:
 *
 *      Steering power = Heading Error * Proportional Gain.
 *
 *      "Heading Error" is calculated by taking the difference between the desired heading and the actual heading,
 *      and then "normalizing" it by converting it to a value in the +/- 180 degree range.
 *
 *      "Proportional Gain" is a constant that YOU choose to set the "strength" of the steering response.
 *
 *  Use Android Studio to Copy this Class, and Paste it into your "TeamCode" folder with a new name.
 *  Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@Autonomous
@Config
@Disabled
public class DEVELOPMENT_CenterstageAutonomousMain extends LinearOpMode {
    private static final double PIXEL_DROP_ALIGN_DISTANCE = 2.5;
    private static final double PIXEL_DROP_ADJUST_DISTANCE = 4;
    private static final double TILE_LENGTH = 24;
    private boolean isTestBot = true;
    private SubSystemClawArm clawArm = null;
    private SubSystemClaw claw = null;
    public FtcDashboard dashboard;


    /* Declare OpMode members. */
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DistanceSensor leftDistanceSensor;
    private DistanceSensor rightDistanceSensor;

    private IMU             imu         = null;      // Control/Expansion Hub IMU

    private double          headingError  = 0;

    // These variable are declared here (as class members) so they can be updated in various methods,
    // but still be displayed by sendTelemetry()
    private double  targetHeading = 0;
    private double  driveSpeed    = 0;
    private double  turnSpeed     = 0;
    private double  leftSpeed     = 0;
    private double  rightSpeed    = 0;
    private int backLeftTarget = 0;
    private int frontLeftTarget = 0;

    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.

    static final double     CORRECTION_FACTOR       = (60.0/58.0);
    static final double     CORRECTION_FACTOR_STRAFE       = (50.0/38.0);
    static final double     COUNTS_PER_MOTOR_REV    = 537.7 ;   // eg: GoBILDA 312 RPM Yellow Jacket
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ; //Possible not accurate*     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION * CORRECTION_FACTOR) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     COUNTS_PER_INCH_STRAFE         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION * CORRECTION_FACTOR_STRAFE) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    // These constants define the desired driving/control characteristics
    // They can/should be tweaked to suit the specific robot drive train.
    static final double     DRIVE_SPEED             = 0.4;     // Max driving speed for better distance accuracy.
    static final double     TURN_SPEED              = 0.2;     // Max Turn speed to limit turn rate
    static final double     HEADING_THRESHOLD       = 1.0 ;    // How close must the heading get to the target before moving to next step.
    // Requiring more accuracy (a smaller number) will often make the turn take longer to get into the final position.
    // Define the Proportional control coefficient (or GAIN) for "heading control".
    // We define one value when Turning (larger errors), and the other is used when Driving straight (smaller errors).
    // Increase these numbers if the heading does not corrects strongly enough (eg: a heavy robot or using tracks)
    // Decrease these numbers if the heading does not settle on the correct value (eg: very agile robot with omni wheels)
    static final double     P_TURN_GAIN            = 0.02;     // Larger is more responsive, but also less stable
    static final double     P_DRIVE_GAIN           = 0.03;     // Larger is more responsive, but also less stable4
    public static final double distanceDropPos1 = 29.0;
    public static final double finishDistancePos1 = 2.0;
    public static final double distanceDropPos2 = 29.0 - 4.0;
    public static final double distanceDropPos3 = 29.0;
    public static final double finishDistancePos3 = 3.0;
    public static int propStartingPos = 0;

    public static final int SCANNING_DISTANCE = 9;
    private SubSystemDrivetrain drivetrain;
    public static int TINY_DISTANCE = 0;
        private final double DROP_POS_CENTER = 25;
    private final double PUSH_OFF_DISTANCE_CENTER = 8;
    private final double DROP_POS_SIDE = 30;
    private final double PUSH_OFF_DISTANCE_SIDE = 6;
    private int frontRightTarget;
    private int backRightTarget;

    public static int CurrentHeading = 0;
    public static int Mirror = 1;
    public static int TopBottomMultiplier = 1;
    public static int InvertStrafe = 1;
    public static int ParkStrafeMultiplier = 0;
    public static int PixelPositionMultiplier = 0;
    public static int SkipAdjust = 0;
    public static int ParkDistance = 0;
    public static double CorrectionDistance = 0;
    public static int ForwardBackward = 0;
    public static int PixelCenter = 0;
    public static int ParkPos = 0;
    public static int PixelPos = 0;

    public static int TOP_PARK_DISTANCE = 38;
    public static int BOTTOM_PARK_DISTANCE = 86;
    public static boolean DoPark;

    public void initializeMotors() throws InterruptedException {
        // Initialize the drive system variables.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");

        clawArm    = new SubSystemClawArm(hardwareMap);
        claw       = new SubSystemClaw(hardwareMap);
        drivetrain = new SubSystemDrivetrain(hardwareMap, SubSystemVariables.currentBot);

        leftDistanceSensor = hardwareMap.get(DistanceSensor.class, "leftDistanceSensor");
        rightDistanceSensor = hardwareMap.get(DistanceSensor.class, "rightDistanceSensor");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    public void initializeDashboard() {
        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

    }

    public void testProgram() {
        // Step through each leg of the path,
        // Notes:   Reverse movement is obtained by setting a negative distance (not speed)
        //          holdHeading() is used after turns to let the heading stabilize
        //          Add a sleep(2000) after any step to keep the telemetry data visible for review


        driveStraight(DRIVE_SPEED, 60.0);    // Drive Forward 24"
        turnToHeading( TURN_SPEED, -45.0);               // Turn  CW to -45 Degrees
        holdHeading( TURN_SPEED, -45.0, 0.5);   // Hold -45 Deg heading for a 1/2 second

        driveStraight(DRIVE_SPEED, 17.0);  // Drive Forward 17" at -45 degrees (12"x and 12"y)
        turnToHeading( TURN_SPEED,  45.0);               // Turn  CCW  to  45 Degrees
        holdHeading( TURN_SPEED,  45.0, 0.5);    // Hold  45 Deg heading for a 1/2 second

        driveStraight(DRIVE_SPEED, 17.0);  // Drive Forward 17" at 45 degrees (-12"x and 12"y)
        turnToHeading( TURN_SPEED,   0.0);               // Turn  CW  to 0 Degrees
        holdHeading( TURN_SPEED,   0.0, 1.0);    // Hold  0 Deg heading for 1 second

        driveStraight(DRIVE_SPEED,-48.0);    // Drive in Reverse 48" (should return to approx. staring position)

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // Pause to display last telemetry message.
    }

    public void configureMotors() {
        // Ensure the robot is stationary.  Reset the encoders and set the motors to BRAKE mode
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set the encoders for closed loop speed control, and reset the heading.
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    private void updateTelemetry() {
        //telemetry.addData(">", "Robot Heading = %4.0f", getHeading());
        //telemetry.addData("Distance Sensor: ", leftDistanceSensor.getDistance(DistanceUnit.MM));
        //telemetry.addData("propStartingPos: ", propStartingPos);

        telemetry.addData("Alliance Color: ", SubSystemVariables.allianceColor);
        telemetry.addData("Alliance Side: ", SubSystemVariables.allianceSide);
        //telemetry.addData("Gyro Val: ", imu.getRobotYawPitchRollAngles());
        telemetry.addData("Parking Position", SubSystemVariables.parkingPos);
        //telemetry.addData("leftDistSensor: ", leftDistanceSensor.getDistance(DistanceUnit.MM));
        //telemetry.addData("rightDistSensor: ", rightDistanceSensor.getDistance(DistanceUnit.MM));
        telemetry.addData("Park in backstage? ", SubSystemVariables.parkInBackstage);
        telemetry.addData("Pixel pos: ", PixelPos);
        telemetry.update();
    }

    private void updateButtonPressed() {
        if(gamepad2.x) {
            SubSystemVariables.allianceColor = SubSystemVariables.ALLIANCE_COLOR.BLUE;
        } else if (gamepad2.b) {
            SubSystemVariables.allianceColor = SubSystemVariables.ALLIANCE_COLOR.RED;
        }

        if(gamepad2.a) {
            SubSystemVariables.allianceSide = SubSystemVariables.ALLIANCE_SIDE.BOTTOM;
        } else if (gamepad2.y) {
            SubSystemVariables.allianceSide = SubSystemVariables.ALLIANCE_SIDE.TOP;
        }

        if(gamepad2.dpad_left) {
            SubSystemVariables.parkingPos = 1;
        } else if (gamepad2.dpad_up) {
            SubSystemVariables.parkingPos = 2;
        } else if (gamepad2.dpad_right) {
            SubSystemVariables.parkingPos = 3;
        }

        if(gamepad2.left_bumper) {
            SubSystemVariables.parkInBackstage = false;
        } else if (gamepad2.right_bumper) {
            SubSystemVariables.parkInBackstage = true;
        }
    }
    private  void UpdateParameters_NEW() {
        if (PixelPos == 1) {
            ForwardBackward = 1;
        } else {
            ForwardBackward = -1;
        }

        if (((PixelPos == 3) && (SubSystemVariables.allianceColor == SubSystemVariables.ALLIANCE_COLOR.BLUE)) || ((PixelPos == 1) && (SubSystemVariables.allianceColor == SubSystemVariables.ALLIANCE_COLOR.RED))) {
            InvertStrafe = -1;
        } else{
            InvertStrafe = 1;
        }

        if (PixelPos == 1) {
            PixelPositionMultiplier = 1;
            PixelCenter = 0;
        } else if (PixelPos == 2) {
            PixelPositionMultiplier = 0;
            PixelCenter = 1;
        } else {
            PixelPositionMultiplier = -1;
            PixelCenter = 0;
        }

    //Check if we would end up re-centering on the tile only to then move back the way we came
        SkipAdjust = Mirror * ParkStrafeMultiplier * PixelPositionMultiplier;

        //if (SkipAdjust == -1) {
        CorrectionDistance = -(PIXEL_DROP_ADJUST_DISTANCE + (ForwardBackward * PIXEL_DROP_ALIGN_DISTANCE));
        //}
    }
    private  void InitParameters_NEW() {
        if (SubSystemVariables.allianceColor == SubSystemVariables.ALLIANCE_COLOR.RED) { //#Left or right side of the field? Which way to turn to face the backdrop
            Mirror = -1;
            SubSystemVariables.headingToBackboard = -90;
            //CurrentHeading = 90;
        } else {
            Mirror = 1;
            SubSystemVariables.headingToBackboard = 90;
            //CurrentHeading = -90;
        }

         if (SubSystemVariables.allianceSide == SubSystemVariables.ALLIANCE_SIDE.TOP) {
            TopBottomMultiplier = 1;
            ParkDistance = TOP_PARK_DISTANCE;
        }
        else {
             TopBottomMultiplier = -1;
             ParkDistance = BOTTOM_PARK_DISTANCE;
         }

        if (ParkPos == 1) {
            ParkStrafeMultiplier = -1;
            DoPark = true;
        } else if(ParkPos == 2) {
            ParkStrafeMultiplier = 0;
            DoPark = true;
        } else if (ParkPos == 3) {
            ParkStrafeMultiplier = 1;
            DoPark = true;
        } else {
            DoPark = false;
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initializeMotors();

        /* The next two lines define Hub orientation.
         * The Default Orientation (shown) is when a hub is mounted horizontally with the printed logo pointing UP and the USB port pointing FORWARD.
         *
         * To Do:  EDIT these two lines to match YOUR mounting configuration.
         */
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        // Now initialize the IMU with this mounting orientation
        // This sample expects the IMU to be in a REV Hub and named "imu".
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        configureMotors();
        claw.closeClaw(true);
        sleep(1000);
        clawArm.setClawArmSpeed(SubSystemVariables.CLAW_ARM_POWER);
        clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_0);
        initializeDashboard();
        // Wait for the game to start (Display Gyro value while waiting)
        while (opModeInInit()) {
            updateTelemetry();
            updateButtonPressed();
        }

        imu.resetYaw();

        InitParameters_NEW();
        AutonDistanceDropPixel();
        if(SubSystemVariables.parkInBackstage) {
            park_NEW();
        }

        claw.closeClaw(true);
        sleep(1000);
        clawArm.setClawArmSpeed(SubSystemVariables.CLAW_ARM_POWER);
        clawArm.setClawArmPosition(0);
        sendTelemetry();
        //sleep(1000000);


        //Strafe(SubSystemVariables.STRAFE_SPEED, 60);
        //turnToHeading(TURN_SPEED, -90);
        //driveStraight(driveSpeed-0.3, 80);
    }

    private void parkDropBackboard() {
        turnToHeading(TURN_SPEED, -SubSystemVariables.headingToBackboard);
        holdHeading(TURN_SPEED, -SubSystemVariables.headingToBackboard, 0.5);
        while (drivetrain.getFrontDistanceSensor() > 200) {
            drivetrain.driveHeading(DRIVE_SPEED, TURN_SPEED, -SubSystemVariables.headingToBackboard);
        }
        while (drivetrain.getFrontDistanceSensor() > 70) {
            drivetrain.driveHeading(DRIVE_SPEED / 2, TURN_SPEED, -SubSystemVariables.headingToBackboard);
        }
    }

    private void park_NEW() {
        //sleep(5000);
        Strafe(SubSystemVariables.STRAFE_SPEED, (InvertStrafe * ParkStrafeMultiplier * TILE_LENGTH) + (InvertStrafe * ParkStrafeMultiplier * CorrectionDistance));
        sleep(100);
        driveStraight(DRIVE_SPEED, InvertStrafe * ParkDistance);
    }
    private void AutonDistanceDropPixel() {
        driveStraight(DRIVE_SPEED, SCANNING_DISTANCE);
        PixelPos = detectPropDistance();
        UpdateParameters_NEW();
        processPropPosition_NEW(PixelPos);
    }

    private void processPropPosition_NEW(int position) {
        if (PixelPos == 2) {
            driveStraight(DRIVE_SPEED, DROP_POS_CENTER - SCANNING_DISTANCE);
            driveStraight(DRIVE_SPEED + 0.3, PUSH_OFF_DISTANCE_CENTER);
            sleep(100);
            driveStraight(DRIVE_SPEED, -PUSH_OFF_DISTANCE_CENTER);
            sleep(500);
            clawArm.setClawArmPosition(0);
            sleep(500);
            claw.closeClaw(false);
            sleep(500);
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_2);
            sleep(500);
            rotateBy(TURN_SPEED, Mirror * 90);
        } else {
            driveStraight(DRIVE_SPEED, DROP_POS_SIDE - SCANNING_DISTANCE + (ForwardBackward * PIXEL_DROP_ADJUST_DISTANCE));
            rotateBy(TURN_SPEED, (ForwardBackward * 90));
            driveStraight(DRIVE_SPEED + 0.3, PUSH_OFF_DISTANCE_SIDE);
            sleep(100);
            driveStraight(DRIVE_SPEED, -PUSH_OFF_DISTANCE_SIDE);
            sleep(500);
            clawArm.setClawArmPosition(0);
            sleep(500);
            claw.closeClaw(false);
            sleep(500);
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_2);
            sleep(500);
            if (SkipAdjust != -1) {
                //print("MADE IT HERE", CorrectionDistance);
                Strafe(DRIVE_SPEED, CorrectionDistance);
                CorrectionDistance = 0; //Don't do the correction later
            }
        }
    }

    private void rotateBy(double speed, int angle) {
        turnToHeading(speed, getHeading() + angle);
        holdHeading(TURN_SPEED, 0, 0.5);
    }

    private void processPropPosition_OLD(int position) {
        clawArm.setClawArmSpeed(SubSystemVariables.CLAW_ARM_POWER_AUTO);

        if(position == 2) {
            driveStraight(DRIVE_SPEED, DROP_POS_CENTER - SCANNING_DISTANCE);
            driveStraight(DRIVE_SPEED + 0.3, PUSH_OFF_DISTANCE_CENTER);
            sleep(100);
            driveStraight(DRIVE_SPEED, -PUSH_OFF_DISTANCE_CENTER);
            sleep(1500);
            clawArm.setClawArmPosition(0);
            sleep(1500);
            claw.closeClaw(false);
            sleep(1500);
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_0);
            sleep(1500);
            turnToHeading(TURN_SPEED, -Mirror * 90);
            holdHeading(TURN_SPEED, -Mirror * 90, 0.5);
        } else {
            driveStraight(DRIVE_SPEED, DROP_POS_SIDE - SCANNING_DISTANCE + (ForwardBackward * PIXEL_DROP_ADJUST_DISTANCE));
            turnToHeading(TURN_SPEED, (ForwardBackward * 90));
            holdHeading(TURN_SPEED, (ForwardBackward * 90), 0.5);
            driveStraight(DRIVE_SPEED + 0.3, PUSH_OFF_DISTANCE_SIDE);
            sleep(1000);
            driveStraight(DRIVE_SPEED, -PUSH_OFF_DISTANCE_SIDE);
            sleep(1500);
            clawArm.setClawArmPosition(0);
            sleep(1500);
            claw.closeClaw(false);
            sleep(1500);
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_0);
            sleep(500);
            if (SkipAdjust != -1) {
                Strafe(DRIVE_SPEED, CorrectionDistance);
                CorrectionDistance = 0; //Don't do the correction later
            }
        }
    }

    private int detectPropDistance() {
        sleep(500);
        if (leftDistanceSensor.getDistance(DistanceUnit.MM) < 450) {
            return 1;
        } else if (rightDistanceSensor.getDistance(DistanceUnit.MM) < 400) {
            return 3;
        } else {
            return 2;
        }

    }

    private int detectProp() {
        /*
        if(gamepad1.dpad_left) {
            propStartingPos = 1;
        } else if (gamepad1.dpad_up) {
            propStartingPos = 2;
        } else if (gamepad1.dpad_right) {
            propStartingPos = 3;
        }
         */

        return 2;


    }

    private void AutonSimpleDropPixelCenter(int position) {
        imu.resetYaw();

        if(position == 1) {
            driveStraight(DRIVE_SPEED, distanceDropPos1);
            turnToHeading(TURN_SPEED, 90);
            holdHeading(TURN_SPEED,  90, 0.5);
            driveStraight(DRIVE_SPEED, finishDistancePos1);

        } else if (position == 2) {
            driveStraight(DRIVE_SPEED, distanceDropPos2);
            clawArm.setClawArmPosition(0);
            sleep(1000);
            claw.closeClaw(false);
            sleep(1000);
            clawArm.setClawArmPosition(SubSystemVariables.CLAW_ARM_POS_2);
            driveStraight(DRIVE_SPEED, -4);
            clawArm.setClawArmPosition(0);
            sleep(1000);

        }  else /*if (position == 3)*/ {
            driveStraight(DRIVE_SPEED, distanceDropPos3);
            turnToHeading(TURN_SPEED, -90);
            holdHeading( TURN_SPEED,  -90, 0.5);
            driveStraight(DRIVE_SPEED, finishDistancePos3);
        }

    }


    /*
     * ====================================================================================================
     * Driving "Helper" functions are below this line.
     * These provide the high and low level methods that handle driving straight and turning.
     * ====================================================================================================
     */

    // **********  HIGH Level driving functions.  ********************

    /**
     * Drive in a straight line, on a fixed compass heading (angle), based on encoder counts.
     * Move will stop if either of these conditions occur:
     * 1) Move gets to the desired position
     * 2) Driver stops the OpMode running.
     *
     * @param maxDriveSpeed MAX Speed for forward/rev motion (range 0 to +1.0) .
     * @param distance      Distance (in inches) to move from current position.  Negative distance means move backward.
     */
    public void driveStraight(double maxDriveSpeed,
                              double distance) {

        double heading = getHeading();
        // Ensure that the OpMode is still active
        // Determine new target position, and pass to motor controller
        int moveCounts = (int) (distance * COUNTS_PER_INCH);
        backLeftTarget = backLeftDrive.getCurrentPosition() + moveCounts;
        frontLeftTarget = frontLeftDrive.getCurrentPosition() + moveCounts;
        backRightTarget = backRightDrive.getCurrentPosition() + moveCounts;
        frontRightTarget = frontRightDrive.getCurrentPosition() + moveCounts;

        // Set Target FIRST, then turn on RUN_TO_POSITION
        frontLeftDrive.setTargetPosition(frontLeftTarget);
        backLeftDrive.setTargetPosition(backLeftTarget);
        frontRightDrive.setTargetPosition(frontRightTarget);
        backRightDrive.setTargetPosition(backRightTarget);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set the required driving speed  (must be positive for RUN_TO_POSITION)
        // Start driving straight, and then enter the control loop
        maxDriveSpeed = Math.abs(maxDriveSpeed);
        moveRobot(maxDriveSpeed, 0);

        // keep looping while we are still active, and BOTH motors are running.
        while (/*opModeIsActive() &&*/
                (frontLeftDrive.isBusy() && frontRightDrive.isBusy())) {

            // Determine required steering to keep on heading
            turnSpeed = getSteeringCorrection(heading, P_DRIVE_GAIN);

            // if driving in reverse, the motor correction also needs to be reversed
            if (distance < 0)
                turnSpeed *= -1.0;

            // Apply the turning correction to the current driving speed.
            moveRobot(driveSpeed, turnSpeed);


        }

        // Display drive status for the driver.
        telemetry.addData("Driving for dist: ", distance);

        // Stop all motion & Turn off RUN_TO_POSITION
        moveRobot(0, 0);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void Strafe(double maxDriveSpeed, double distance) {
        // Ensure that the OpMode is still active
        double heading = getHeading();
        // Determine new target position, and pass to motor controller
        int moveCounts = (int)(distance * COUNTS_PER_INCH_STRAFE);
        backLeftTarget = backLeftDrive.getCurrentPosition() - moveCounts;
        frontLeftTarget = frontLeftDrive.getCurrentPosition() + moveCounts;
        backRightTarget = backRightDrive.getCurrentPosition() - moveCounts;
        frontRightTarget = frontRightDrive.getCurrentPosition() + moveCounts;

        // Set Target FIRST, then turn on RUN_TO_POSITION
        frontLeftDrive.setTargetPosition(frontLeftTarget);
        backLeftDrive.setTargetPosition(backLeftTarget);
        frontRightDrive.setTargetPosition(backRightTarget);
        backRightDrive.setTargetPosition(frontRightTarget);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set the required driving speed  (must be positive for RUN_TO_POSITION)
        // Start driving straight, and then enter the control loop
        maxDriveSpeed = Math.abs(maxDriveSpeed);
        moveRobot(maxDriveSpeed, 0);

        // keep looping while we are still active, and BOTH motors are running.
        while (/*opModeIsActive() &&
                (frontLeftDrive.isBusy() &&*/ frontRightDrive.isBusy()) {

            // Determine required steering to keep on heading
            turnSpeed = getSteeringCorrection(heading, P_DRIVE_GAIN);

            // if driving in reverse, the motor correction also needs to be reversed
            if (distance < 0)
                turnSpeed *= -1.0;

            // Apply the turning correction to the current driving speed.
            moveRobot(driveSpeed, turnSpeed);


        }

        // Display drive status for the driver.
        telemetry.addData("Strafing for: ", distance);

        // Stop all motion & Turn off RUN_TO_POSITION
        moveRobot(0, 0);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     *  Spin on the central axis to point in a new direction.
     *  <p>
     *  Move will stop if either of these conditions occur:
     *  <p>
     *  1) Move gets to the heading (angle)
     *  <p>
     *  2) Driver stops the OpMode running.
     *
     * @param maxTurnSpeed Desired MAX speed of turn. (range 0 to +1.0)
     * @param heading Absolute Heading Angle (in Degrees) relative to last gyro reset.
     *              0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *              If a relative angle is required, add/subtract from current heading.
     */
    public void turnToHeading(double maxTurnSpeed, double heading) {

        // Run getSteeringCorrection() once to pre-calculate the current error
        getSteeringCorrection(heading, P_DRIVE_GAIN);

        // keep looping while we are still active, and not on heading.
        while (opModeIsActive() && (Math.abs(headingError) > HEADING_THRESHOLD)) {

            // Determine required steering to keep on heading
            turnSpeed = getSteeringCorrection(heading, P_TURN_GAIN);

            // Clip the speed to the maximum permitted value.
            turnSpeed = Range.clip(turnSpeed, -maxTurnSpeed, maxTurnSpeed);

            // Pivot in place by applying the turning correction
            moveRobot(0, turnSpeed);


        }

        // Display drive status for the driver.
        telemetry.addData("Turning for: ", heading);

        // Stop all motion;
        moveRobot(0, 0);
    }

    /**
     *  Obtain & hold a heading for a finite amount of time
     *  <p>
     *  Move will stop once the requested time has elapsed
     *  <p>
     *  This function is useful for giving the robot a moment to stabilize it's heading between movements.
     *
     * @param maxTurnSpeed      Maximum differential turn speed (range 0 to +1.0)
     * @param heading    Absolute Heading Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     * @param holdTime   Length of time (in seconds) to hold the specified heading.
     */
    public void holdHeading(double maxTurnSpeed, double heading, double holdTime) {

        ElapsedTime holdTimer = new ElapsedTime();
        holdTimer.reset();

        // keep looping while we have time remaining.
        while (opModeIsActive() && (holdTimer.time() < holdTime)) {
            // Determine required steering to keep on heading
            turnSpeed = getSteeringCorrection(heading, P_TURN_GAIN);

            // Clip the speed to the maximum permitted value.
            turnSpeed = Range.clip(turnSpeed, -maxTurnSpeed, maxTurnSpeed);

            // Pivot in place by applying the turning correction
            moveRobot(0, turnSpeed);

            // Display drive status for the driver.
            //sendTelemetry();
        }

        // Stop all motion;
        moveRobot(0, 0);
    }

    // **********  LOW Level driving functions.  ********************

    /**
     * Use a Proportional Controller to determine how much steering correction is required.
     *
     * @param desiredHeading        The desired absolute heading (relative to last heading reset)
     * @param proportionalGain      Gain factor applied to heading error to obtain turning power.
     * @return                      Turning power needed to get to required heading.
     */
    public double getSteeringCorrection(double desiredHeading, double proportionalGain) {
        targetHeading = desiredHeading;  // Save for telemetry

        // Determine the heading current error
        headingError = targetHeading - getHeading();

        // Normalize the error to be within +/- 180 degrees
        while (headingError > 180)  headingError -= 360;
        while (headingError <= -180) headingError += 360;

        // Multiply the error by the gain to determine the required steering correction/  Limit the result to +/- 1.0
        return Range.clip(headingError * proportionalGain, -1, 1);
    }

    /**
     * Take separate drive (fwd/rev) and turn (right/left) requests,
     * combines them, and applies the appropriate speed commands to the left and right wheel motors.
     * @param drive forward motor speed
     * @param turn  clockwise turning motor speed.
     */
    public void moveRobot(double drive, double turn) {
        driveSpeed = drive;     // save this value as a class member so it can be used by telemetry.
        turnSpeed  = turn;      // save this value as a class member so it can be used by telemetry.

        leftSpeed  = drive - turn;
        rightSpeed = drive + turn;

        // Scale speeds down if either one exceeds +/- 1.0;
        double max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
        if (max > 1.0)
        {
            leftSpeed /= max;
            rightSpeed /= max;
        }

        frontLeftDrive.setPower(leftSpeed);
        frontRightDrive.setPower(rightSpeed);
        backLeftDrive.setPower(leftSpeed);
        backRightDrive.setPower(rightSpeed);
    }

    /**
     * Display the various control parameters while driving
     */
    private void sendTelemetry() {
        telemetry.update();
    }

    /**
     * read the Robot heading directly from the IMU (in degrees)
     */
    public double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }
}