package org.firstinspires.ftc.teamcode;

public class SubSystemVariables {
    public static double distToBackboard;
    public static int headingToBackboard;

    public static final int INTAKE_LIFT_POS_0 = 143;
    public static final int INTAKE_LIFT_POS_1 = 5;
    public static final int INTAKE_LIFT_POS_2 = 0;
    public static final int INTAKE_LIFT_POS_3 = 800;

    public static final double CLAW_ARM_POWER = 0.3;
    public static final int CLAW_ARM_POS_0 = 450;
    public static final int CLAW_ARM_POS_1 = 5;
    public static final int CLAW_ARM_POS_2 = 250;
    public static final int CLAW_ARM_POS_3 = 250;


    public static final double INTAKE_LIFT_POWER = 0.4;

    public static final double HANG_LIFT_HANG_POWER = 0.5;
    public static final double HANG_LIFT_DROP_POWER = 0.1;

    public static final int HANG_LIFT_POS_DROP = 0;

    public static final int HANG_LIFT_POS_HANG = -8000;

    public static boolean CLAW_OPEN = false;
    public static enum ALLIANCE_COLOR {BLUE, RED};
    public static enum ALLIANCE_SIDE {BOTTOM, TOP};
    public static ALLIANCE_COLOR allianceColor = ALLIANCE_COLOR.BLUE;
    public static ALLIANCE_SIDE allianceSide = ALLIANCE_SIDE.BOTTOM;
    public static String parkingPos = "backboard";

    public static double HOPPER_SERVO_DOWN = 0.05;
    public static double HOPPER_SERVO_UP = 0.4;
    public static double HOPPER_GATE_OPEN = 0.8;
    public static double HOPPER_GATE_CLOSE = 1;

}
