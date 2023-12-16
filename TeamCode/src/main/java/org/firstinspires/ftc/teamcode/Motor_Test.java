package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.SubSystemHangLift;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemHopper;
import org.firstinspires.ftc.teamcode.subsystems.SubSystemIntake;

//This is a test on 11/23 to push to githuv
@TeleOp
@Config
//@Disabled
public class Motor_Test extends LinearOpMode {
    //Dashboard demo variables
    public static double ORBITAL_FREQUENCY = 0.05;
    public static double SPIN_FREQUENCY = 0.25;
    public static double ORBITAL_RADIUS = 50;
    public static double SIDE_LENGTH = 10;
    public FtcDashboard dashboard;

    //Motor demo variables
    private DcMotorEx frontLeftDrive = null;
    private DcMotorEx frontRightDrive = null;
    private DcMotorEx backLeftDrive = null;
    private DcMotorEx backRightDrive = null;
//    private DcMotorEx intakeLift = null;

    private Servo testServo = null;
    private Servo torqueServo = null;
    private Servo HOPPER_SERVO_OLD = null;

    public static double servoMax = 1.0;
    public static double servoMin = 0.0;
    private int motorToTest = 2;
    //private DcMotorEx hopperLift;
    private float powerToSet;
    private SubSystemHopper hopperServo;
    private boolean hangRelease;
    private SubSystemHangLift hangLift;
    private boolean hangLiftHang;
    private boolean hangLiftDrop;
    private DcMotorEx hangMotor;

    public void initializeHardware() throws InterruptedException {

        frontLeftDrive = hardwareMap.get(DcMotorEx.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "frontRightDrive");
        backLeftDrive = hardwareMap.get(DcMotorEx.class, "backLeftDrive");
        backRightDrive = hardwareMap.get(DcMotorEx.class, "backRightDrive");
       // intakeLift = hardwareMap.get(DcMotorEx.class, "intakeLift");
        //hopperLift = hardwareMap.get(DcMotorEx.class, "hopperLift");
        hangMotor = hardwareMap.get(DcMotorEx.class, "hangLift");

        frontRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       // intakeLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //hopperLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hangMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       // intakeLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //hopperLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hangMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeftDrive.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //intakeLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //hopperLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        testServo = hardwareMap.get(Servo.class, "hopperGateServo");
        //torqueServo = hardwareMap.get(Servo.class, "torqueServo");
        HOPPER_SERVO_OLD = hardwareMap.get(Servo.class, "hopperServo");
        hopperServo = new SubSystemHopper(hardwareMap);
        //hangLift = new SubSystemHangLift(hardwareMap);
    }

    private static void initTestmotor() {

    }

    private static void rotatePoints(double[] xPoints, double[] yPoints, double angle) {
        for (int i = 0; i < xPoints.length; i++) {
            double x = xPoints[i];
            double y = yPoints[i];
            xPoints[i] = x * Math.cos(angle) - y * Math.sin(angle);
            yPoints[i] = x * Math.sin(angle) + y * Math.cos(angle);
        }
    }

    public void initializeDashboard() {
        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

    }

    public void dashboardDemo(){
        double time = getRuntime();

        double bx = ORBITAL_RADIUS * Math.cos(2 * Math.PI * ORBITAL_FREQUENCY * time);
        double by = ORBITAL_RADIUS * Math.sin(2 * Math.PI * ORBITAL_FREQUENCY * time);
        double l = SIDE_LENGTH / 2;

        double[] bxPoints = { l, -l, -l, l };
        double[] byPoints = { l, l, -l, -l };
        rotatePoints(bxPoints, byPoints, 2 * Math.PI * SPIN_FREQUENCY * time);
        for (int i = 0; i < 4; i++) {
            bxPoints[i] += bx;
            byPoints[i] += by;
        }

        TelemetryPacket packet = new TelemetryPacket();
        packet.fieldOverlay()
                .setStrokeWidth(1)
                .setStroke("goldenrod")
                .strokeCircle(0, 0, ORBITAL_RADIUS)
                .setFill("black")
                .fillPolygon(bxPoints, byPoints);
        dashboard.sendTelemetryPacket(packet);

        sleep(20);
    }

    private void displayTelemetry() {
        telemetry.addLine("Iniyann is the greatest programmer in the world");
        telemetry.addData("Motor (intake is true, hopper is false)", motorToTest);

        //telemetry.addData("Motor 1 encoder: ", intakeLift.getCurrentPosition());
        //telemetry.addData("Motor 2 encoder: ", hopperLift.getCurrentPosition());
        telemetry.addData("Motor 3 encoder: ", hangMotor.getCurrentPosition());
        telemetry.update();
    }

    public void runOpMode() throws InterruptedException {
        initializeDashboard();
        initializeHardware();

        waitForStart();
        while (opModeIsActive()) {
            displayTelemetry();
            updateController();
            //hangLiftUpdate();
            updateTestMotors();

        }
    }

    private void updateTestMotors() {
        if(motorToTest == 1) {
           // intakeLift.setPower(powerToSet);
            hangMotor.setPower(0);
            //hopperLift.setPower(0);
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }

        if(motorToTest == 2) {
           // intakeLift.setPower(0);
            hangMotor.setPower(powerToSet);
            //hopperLift.setPower(0);
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }
        if(motorToTest == 3) {
           // intakeLift.setPower(0);
            hangMotor.setPower(0);
            //hopperLift.setPower(powerToSet);
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }

        if(motorToTest == 4) {
           // intakeLift.setPower(0);
            hangMotor.setPower(0);
            //hopperLift.setPower(0);
            frontLeftDrive.setPower(powerToSet);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }

        if(motorToTest == 5) {
          //  intakeLift.setPower(0);
            hangMotor.setPower(0);
            //hopperLift.setPower(0);
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(powerToSet);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }

        if(motorToTest == 6) {
           // intakeLift.setPower(0);
            hangMotor.setPower(0);
            //hopperLift.setPower(0);
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(powerToSet);
            backRightDrive.setPower(0);
        }

        if(motorToTest == 7) {
           // intakeLift.setPower(0);
            hangMotor.setPower(0);
            //hopperLift.setPower(0);
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(powerToSet);
        }

    }

    private void updateController() {
        /*
        if(gamepad1.left_bumper)
            setServoPos(0.8); //0.8 is open for gate servo

        if(gamepad1.right_bumper)
            setServoPos(1); //1 is closed gate servo

        if(gamepad1.left_trigger > 0.5)
            setTorqueServoPos(0);

        if(gamepad1.right_trigger > 0.5)
            setTorqueServoPos(1);

        if(gamepad1.x)
            setHopperServo(servoMax); //HopperServo is 0.05 for down

        if(gamepad1.b)
            setHopperServo(servoMin); //HopperServo is 0.4 for up
         */
        if (gamepad1.dpad_down && hangRelease)
            hangLiftHang = true;
        else
            hangLiftHang = false;

        if(gamepad1.a)
            hangLiftDrop = true;
        else
            hangLiftDrop = false;

        boolean endgame = true;

        if (gamepad1.b && gamepad1.dpad_right && endgame)
            hangRelease = true;

//        if(gamepad1.dpad_left) {
//            motorToTest++;
//        }
//        if(motorToTest > 7) {
//            motorToTest = 1;
//        }

        if(gamepad1.left_trigger > 0.1) {
            powerToSet = gamepad1.left_trigger;
            telemetry.addLine("powering motor");
        } else if (gamepad1.right_trigger > 0.1 ) {
            powerToSet = -gamepad1.right_trigger;
            telemetry.addLine("powering motor");
        } else {
            powerToSet = 0;
            telemetry.addLine("Not powering any motor");
        }

        if (gamepad1.right_bumper) {
            hopperServo.openGate(true);
        }

        if (gamepad1.left_bumper) {
            hopperServo.openGate(false);
        }

        if(gamepad1.x) {
            hopperServo.setHopperPosition(SubSystemVariables.HOPPER_POS_1);
        }
        if(gamepad1.y) {
            hopperServo.setHopperPosition(SubSystemVariables.HOPPER_POS_3);
        }
        if(gamepad1.b) {
            hopperServo.setHopperPosition(SubSystemVariables.HOPPER_POS_4);
        }

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

    private void setHOPPER_SERVO_OLD(double pos) {
        HOPPER_SERVO_OLD.setPosition(pos);}
    private void setTorqueServoPos(double pos) {torqueServo.setPosition(pos);}
    private void setServoPos(double pos) {testServo.setPosition(pos);}
}

