package org.firstinspires.ftc.teamcode.quack;

import android.support.annotation.CallSuper;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.CitrusGoldAlignDetector;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldAlignDetector;
import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.disnodeteam.dogecv.filters.LeviColorFilter;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.opencv.core.Size;

import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.CENTER;
import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.LEFT;
import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.RIGHT;
import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.UNKNOWN;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class QuackAutonomous extends LinearOpMode {
    protected QuackRobot robot;
    protected final float TURN_POWER = 0.6f;
    protected final float DRIVE_POWER = 0.7f;
    protected final float STRAFE_POWER = 0.8f;

    private final int CENTER_STRAFE_DISTANCE = 600;
    private final int RIGHT_STRAFE_DISTANCE = 2000;

    private final int LIFT_ENCODER_COMPETITION = -20700;

    protected SamplingOrderDetector.GoldLocation goldLocation;

    // DogeCV stuff
    private CitrusGoldAlignDetector detector;

    private void initDogeCV() {
        WebcamName webcam = hardwareMap.get(WebcamName.class, "Webcam 1");
        detector = new CitrusGoldAlignDetector();
        detector.VUFORIA_KEY = "AdwI1KD/////AAABmUC5Du193Ev9mWh7pbGwY5kaAjJ" +
                "LosnOnmkTaYcicb0TEraARC6tZLVtsjR12Qc0PB7Ddenye7i+2m6aFj6Ds+U5XC5SF" +
                "oynEHD+EvKTGjI6P3sigllCw9M2XtYQ/3po9lu1Fd0KYJkHQiGFhgSiehwJ4qOsLkq" +
                "fUSVoCzOLWLC2BAQVcQM+Z7BKY0scoiNzl8u4eD/yyh8V6we3HyXpal3OHUCX5zJ4s" +
                "gNdT9pNS+AW4sVgSQ8C0fT/CnlTJAi6ssnphApMfIjg0ZAlSiicoXyZW0W1OX658Ov" +
                "/6aIZ1DwFZ3pskXSwK77JS7XLmFSDVQJLROSaIzw59yUY61gLe4YuqkD2YSsPXhg2t" +
                "m1bf/ZU";
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(),
                DogeCV.CameraMode.WEBCAM, false, webcam);
        detector.yellowFilter = new LeviColorFilter(
                LeviColorFilter.ColorPreset.YELLOW, 100);
        detector.useDefaults();
        detector.enable();
    }

    private SamplingOrderDetector.GoldLocation grabGoldLocation() {
        Size size = detector.getAdjustedSize();
        if (size == null) {
            return UNKNOWN;
        }

        double center = size.height / 2;

        double leftDist = Math.abs(detector.getYPosition());
        double centerDist = Math.abs(detector.getYPosition() - center);
        double rightDist = Math.abs(detector.getYPosition() - size.height);
        double closest = minimum(leftDist, centerDist, rightDist);

        if (closest == leftDist) {
            return LEFT;
        } else if (closest == centerDist) {
            return CENTER;
        } else if (closest == rightDist) {
            return RIGHT;
        } else {
            return UNKNOWN;
        }
    }

    private double minimum(double... numbers) {
        double min = Integer.MAX_VALUE;
        for (double num : numbers) {
            min = Math.min(min, num);
        }

        return min;
    }

    @CallSuper
    @Override
    public void runOpMode() throws InterruptedException {
        robot = new QuackRobot(hardwareMap);
        robot.initAutonomous(this);

        initDogeCV();

        robot.teamMarker.setPosition(robot.TEAM_MARKER_LATCH);

        while (!isStarted() && !isStopRequested()) {
            SamplingOrderDetector.GoldLocation updatedGoldLoc = grabGoldLocation();
            if (updatedGoldLoc != UNKNOWN) {
                goldLocation = updatedGoldLoc;
            }

            telemetry.addData("Gold Location", goldLocation);
            telemetry.update();
        }

        detector.disable();

        lowerFromLander();

        pause();

        strafe(LEFT, 600, 0.7);

        pause();

        drive(525, 0.8);

        pause();

        if (goldLocation == CENTER) {
            rotateTo(359);
            strafe(RIGHT, CENTER_STRAFE_DISTANCE, 1);
        } else if (goldLocation == RIGHT) {
            rotateTo(359);
            strafe(RIGHT, RIGHT_STRAFE_DISTANCE, 1);
        }
    }

    private void drive(double distance, double power, boolean haltAtEnd) {
        robot.rightFront.setDirection(REVERSE);
        robot.rightBack.setDirection(REVERSE);

        while (robot.rightFront.getDirection() != REVERSE) {
            idle();
        }

        distance = currentPosition() + distance;
        robot.leftFront.setPower(power);
        robot.leftBack.setPower(power);
        robot.rightFront.setPower(power);
        robot.rightBack.setPower(power);
        if (distance > currentPosition()) {
            while (opModeIsActive() && currentPosition() < distance) {
                idle();
            }
        } else if (distance < currentPosition()) {
            while (opModeIsActive() && currentPosition() > distance) {
                idle();
            }
        }

        if (haltAtEnd)
            halt();
    }

    protected void drive(double distance, double power) {
        drive(distance, power, true);
    }

    /**
     * Activate the {@code liftMotor} to drop the robot from the lander.
     * This is the first action we run in autonomous.
     */
    private void lowerFromLander() {
        double targetEncoder = LIFT_ENCODER_COMPETITION + robot.lift.getCurrentPosition();

        robot.lift.setPower(-0.9);

        while (opModeIsActive() && robot.lift.getCurrentPosition() >= targetEncoder) {
            idle();
        }

        robot.lift.setPower(0);
    }

    /**
     * Synchronously block the autonomous program for 300 milliseconds (0.3 seconds)
     */
    protected void pause() {
        sleep(300);
    }

    /**
     * Strafe the robot left or right
     * @param direction The direction to strafe (LEFT or RIGHT)
     * @param target How many encoders to move
     * @param power The power to apply to the drivetrain
     */
    protected void strafe(SamplingOrderDetector.GoldLocation direction, int target, double power) {
        robot.rightFront.setDirection(FORWARD);
        robot.rightBack.setDirection(FORWARD);

        while (opModeIsActive() && robot.rightFront.getDirection() != FORWARD) {
            idle();
        }

        if (direction == LEFT) {
            robot.leftFront.setPower(-power);
            robot.leftBack.setPower(power);
            robot.rightFront.setPower(-power);
            robot.rightBack.setPower(power);

            double encoderValue = target + currentPosition();
            while (opModeIsActive() && currentPosition() < encoderValue) {
                idle();
            }
        } else if (direction == RIGHT) {
            robot.leftFront.setPower(power);
            robot.leftBack.setPower(-power);
            robot.rightFront.setPower(power);
            robot.rightBack.setPower(-power);
            double encoderValue = currentPosition() - target;
            while (opModeIsActive() && currentPosition() > encoderValue) {
                idle();
            }
        } else {
            gentleAbort("Strafe direction must be LEFT or RIGHT");
        }

        halt();
    }

    /**
     * Retrieve the encoder value of the leftBack motor
     * @return The current encoder position
     */
    private int currentPosition() {
        return robot.leftBack.getCurrentPosition();
    }

    /**
     * Turn to a degree between 0 and 360 using the Dubberke-Richards method
     * @param target The target degree value
     */
    protected void rotateTo(double target) {
        robot.rightFront.setDirection(REVERSE);
        robot.rightBack.setDirection(REVERSE);

        while (opModeIsActive() && robot.rightFront.getDirection() != REVERSE) {
            idle();
        }

        if (target < 0) target += 360;

        double heading = get360Heading();
        while (opModeIsActive() && !withinRange(heading, target)) {
            // Run Dubberke-Richards logic
            if (Math.abs(heading - target) <= 180) {
                if (heading-target >= 0) {
                    rotateRight();
                } else {
                    rotateLeft();
                }
            } else { // |heading-target| > 180
                if (heading-target<0) {
                    rotateRight();
                } else {
                    rotateLeft();
                }
            }

            // Update heading for next loop
            heading = get360Heading();

            telemetry.addData("Heading", heading);
            telemetry.update();
        }

        halt();
    }

    /**
     * Inclusive range check.
     * @param input your number
     * @param target the number you want to be near
     * @return whether you're near that number
     */
    private boolean withinRange(double input, double target) {
        double range = 2;
        double low = target - range;
        double high = target + range;
        return input >= low && input <= high;
    }

    /**
     * Rotate the robot left
     */
    private void rotateLeft() {
        robot.leftBack.setPower(-TURN_POWER);
        robot.leftFront.setPower(-TURN_POWER);
        robot.rightBack.setPower(TURN_POWER);
        robot.rightFront.setPower(TURN_POWER);
    }

    /**
     * Rotate the robot right
     */
    private void rotateRight() {
        robot.leftBack.setPower(TURN_POWER);
        robot.leftFront.setPower(TURN_POWER);
        robot.rightBack.setPower(-TURN_POWER);
        robot.rightFront.setPower(-TURN_POWER);
    }

    private float get360Heading() {
        float heading = getHeading();
        if (heading < 0) heading += 360;
        return heading;
    }

    /**
     * Return the orientation of the robot from IMU A's reference.
     * @return The heading of the robot from IMU A
     */
    private float getHeading() {
        Orientation anglesA = robot.imu.getAngularOrientation(
                AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return AngleUnit.DEGREES.normalize(
                AngleUnit.DEGREES.fromUnit(anglesA.angleUnit, anglesA.firstAngle));
    }

    /**
     * Stop the robot drive motors
     */
    private void halt() {
        robot.leftFront.setPower(0);
        robot.leftBack.setPower(0);
        robot.rightFront.setPower(0);
        robot.rightBack.setPower(0);
    }

    /**
     * Display a message in {@code Telemetry} and stop this {@code LinearOpMode}
     * from running any more commands
     * @param message The message to be displayed in this autonomous's {@code Telemetry}.
     */
    private void gentleAbort(String message) {
        halt();
        robot.lift.setPower(0);
        robot.backSweeper.setPower(0);
        robot.frontSweeper.setPower(0);

        telemetry.clearAll();
        telemetry.addLine("GENTLE ABORT");
        telemetry.addLine(message);
        telemetry.update();
        while (opModeIsActive()) {
            idle();
        }
    }
}
