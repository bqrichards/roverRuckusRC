package org.firstinspires.ftc.teamcode.quack;

import android.support.annotation.Nullable;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import edu.spa.ftclib.internal.drivetrain.MecanumDrivetrain;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class QuackRobot {
    public DcMotor leftFront, leftBack, rightFront, rightBack, lift, xrailLift;
    public CRServo frontSweeper, backSweeper;
    public DcMotor leftConveyor, rightConveyor;
    public Servo teamMarker, xrailFlipper;
    public BNO055IMUImpl imu;
    public MecanumDrivetrain drivetrain;

    public final double TEAM_MARKER_LATCH = 0.72;
    public final double TEAM_MARKER_RELEASE = 0.25;
    public final double TEAM_MARKER_IDLE = 0.66;

    public final double XRAIL_FLIPPER_LEVEL = 0.31;
    public final double XRAIL_FLIPPER_STORAGE = 0.0;
    public final double XRAIL_FLIPPER_RELEASE = 0.4;

    public QuackRobot(HardwareMap hardwareMap) {
        leftFront = hardwareMap.dcMotor.get("leftFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        rightBack = hardwareMap.dcMotor.get("rightBack");
        lift = hardwareMap.dcMotor.get("lift");
        lift.setDirection(REVERSE);
        frontSweeper = hardwareMap.crservo.get("frontSweeper");
        backSweeper = hardwareMap.crservo.get("backSweeper");
        teamMarker = hardwareMap.servo.get("teamMarker");
        leftConveyor = hardwareMap.dcMotor.get("leftConveyor");
        leftConveyor.setDirection(REVERSE);
        rightConveyor = hardwareMap.dcMotor.get("rightConveyor");
        xrailLift = hardwareMap.dcMotor.get("xrailLift");
        xrailLift.setDirection(REVERSE);
        xrailFlipper = hardwareMap.servo.get("xrailFlipper");
    }

    /**
     * Create a TeleOp drivetrain and set the {@link DcMotor.ZeroPowerBehavior}
     * on all drive motors to {@code FLOAT}
     */
    public void initTeleOp() {
        drivetrain = new MecanumDrivetrain(
                new DcMotor[]{leftFront, rightFront, leftBack, rightBack});
        leftFront.setZeroPowerBehavior(FLOAT);
        leftBack.setZeroPowerBehavior(FLOAT);
        rightFront.setZeroPowerBehavior(FLOAT);
        rightBack.setZeroPowerBehavior(FLOAT);

        teamMarker.setPosition(TEAM_MARKER_IDLE);
    }

    /**
     * Set {@link DcMotor.ZeroPowerBehavior} on all drive motors to {@code BRAKE}
     * and reset the encoders on the leftBack motor
     * @param sender The autonomous program requesting initialization
     */
    public void initAutonomous(LinearOpMode sender) {
        leftFront.setZeroPowerBehavior(BRAKE);
        leftBack.setZeroPowerBehavior(BRAKE);
        rightFront.setZeroPowerBehavior(BRAKE);
        rightBack.setZeroPowerBehavior(BRAKE);
        lift.setZeroPowerBehavior(BRAKE);
        resetDriveEncoders(sender);

        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        leftBack.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        teamMarker.setPosition(TEAM_MARKER_LATCH);

        imu = sender.hardwareMap.get(BNO055IMUImpl.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        //Add calibration file?
        parameters.loggingEnabled = true;   //For debugging
        parameters.loggingTag = "IMU";      //For debugging
        //Figure out why the naive one doesn't have a public constructor
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);

        while (!sender.opModeIsActive() && !sender.isStopRequested() && !imu.isGyroCalibrated()) {
            sender.telemetry.addLine("IMU is calibrating");
            sender.telemetry.update();
            sender.idle();
        }
    }

    /**
     * Convenience method to safely reset the drive encoders
     * NOTE: This method also clears the encoders of the {@code liftMotor}
     * {@param sender} is annotated {@link Nullable} because if {@code null} is passed in,
     * the loops waiting for the motors to receive reset commands will simply not do anything.
     * If {@param sender} is supplied, {@link LinearOpMode#idle()} will be called to allow other
     * threads to handle other things.
     * @param sender The autonomous program requesting drive encoders be reset
     */
    private void resetDriveEncoders(@Nullable LinearOpMode sender) {
        if (sender == null) {
            // Reset without using LinearOpMode#idle command
            leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            while (leftBack.getCurrentPosition() != 0 && lift.getCurrentPosition() != 0);

            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            while (leftBack.getMode() != DcMotor.RunMode.RUN_USING_ENCODER
                    && lift.getMode() != DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            // Reset with LinearOpMode#idle() command
            leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            while (leftBack.getCurrentPosition() != 0 && lift.getCurrentPosition() != 0) {
                sender.idle();
            }

            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            while (leftBack.getMode() != DcMotor.RunMode.RUN_USING_ENCODER
                    && lift.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                sender.idle();
            }
        }
    }
}
