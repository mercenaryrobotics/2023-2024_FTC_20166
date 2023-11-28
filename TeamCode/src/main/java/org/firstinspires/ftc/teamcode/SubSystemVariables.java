package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

@Config
public class SubSystemVariables {
    public static double distToBackboard;
    public static int headingToBackboard;

    public static final int INTAKE_LIFT_POS_0 = 143;
    public static final int INTAKE_LIFT_POS_1 = 5;
    public static final int INTAKE_LIFT_POS_2 = 0;
    public static final int INTAKE_LIFT_POS_3 = 800;

    public static final double CLAW_ARM_POWER = 0.6;
    public static final int CLAW_ARM_POS_0 = 570;
    public static final int CLAW_ARM_POS_1 = 5;
    public static final int CLAW_ARM_POS_2 = 250;
    public static final int CLAW_ARM_POS_3 = 250;


    public static final double INTAKE_LIFT_POWER = 0.4;

    public static final double HANG_LIFT_HANG_POWER = 0.5;
    public static final double HANG_LIFT_DROP_POWER = 0.1;

    public static final int HANG_LIFT_POS_DROP = 0;

    public static final int HANG_LIFT_POS_HANG = -4000;

    public static boolean CLAW_OPEN = false;
    public static double STRAFE_SPEED = 0.7;

    public static enum ALLIANCE_COLOR {BLUE, RED};
    public static enum ALLIANCE_SIDE {BOTTOM, TOP};
    public static ALLIANCE_COLOR allianceColor = ALLIANCE_COLOR.BLUE;
    public static ALLIANCE_SIDE allianceSide = ALLIANCE_SIDE.BOTTOM;
    public static int parkingPos = 1;
    public static boolean parkInBackstage = true;

    public static double HOPPER_GATE_OPEN = 0.8;
    public static double HOPPER_GATE_CLOSE = 1;

    public static double HOPPER_LIFT_POWER = 0.6;
    public static int HOPPER_LIFT_POS_1 = 0;
    public static int HOPPER_LIFT_POS_2 = -1300;
    public static int HOPPER_LIFT_POS_3 = -2500;
    public static int HOPPER_LIFT_POS_4 = -3000;

    public static int HOPPER_LIFT_POS_MAX = -3200;
    public static int HOPPER_LIFT_POS_MIN = -10;

    public static double HOPPER_POS_1 = 1.0;
    public static double HOPPER_POS_2 = 0.8;
    public static double HOPPER_POS_3 = 0.7;
    public static double HOPPER_POS_4 = 0.6;
    public static double HOPPER_POS_UP = 1.0;
    public static double HOPPER_POS_DOWN = 0.7;

    public static double droneLaunchVal = 0.41;
}
