package org.firstinspires.ftc.teamcode.quack;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * Controls:
 *  Gamepad 1:
 *      Left stick: rotation
 *      Right stick: movement
 *  Gamepad 2:
 *      Triggers: Lift
 *      Left stick: sweeper
 */

@TeleOp (name = "TeleOp")
public class QuackTeleOp extends OpMode {
    private QuackRobot robot;
    private final boolean ENABLE_TEAM_MARKER_CONTROLS = false;

    private boolean canSwitchDrive = true;
    private boolean masonDrive = false;

    @Override
    public void init() {
        robot = new QuackRobot(hardwareMap);
        robot.initTeleOp();
    }

    @Override
    public void loop() {
        // Drive train controls
        double course;
        double velocity;
        double rotation;

        if (masonDrive) {
            course = Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x) + Math.PI / 2.0;
            velocity = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
            rotation = gamepad1.right_stick_x;
        } else {
            course = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x) + Math.PI / 2.0;
            velocity = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
            rotation = gamepad1.left_stick_x;
        }

        robot.drivetrain.setCourse(course);
        robot.drivetrain.setVelocity(velocity);
        robot.drivetrain.setRotation(rotation);

        // Lift
        if (gamepad2.right_trigger > 0) {
            robot.lift.setPower(-gamepad2.right_trigger);
        } else if (gamepad2.left_trigger > 0) {
            robot.lift.setPower(gamepad2.left_trigger);
        } else {
            robot.lift.setPower(0);
        }

        // Sweeper
        float sweeperPower = -gamepad2.left_stick_y;
        robot.frontSweeper.setPower(sweeperPower);
        robot.backSweeper.setPower(sweeperPower);

        // End effector controllers
        float conveyorPower = -gamepad2.right_stick_y;
        robot.leftConveyor.setPower(conveyorPower);
        robot.rightConveyor.setPower(conveyorPower);

        // Xrail Lift
        double xrailPower = 0;
        if (gamepad2.dpad_up) {
            xrailPower = 0.7;
        } else if (gamepad2.dpad_down) {
            xrailPower = -0.7;
        }

        robot.xrailLift.setPower(xrailPower);

        // Team Marker or Xrail Box
        if (ENABLE_TEAM_MARKER_CONTROLS) {
            teamMarkerControls();
        } else {
            xrailBoxControls();
        }

        if (gamepad1.left_bumper) {
            if (canSwitchDrive) {
                masonDrive = !masonDrive;
                canSwitchDrive = false;
            }
        } else {
            canSwitchDrive = true;
        }
    }

    private void teamMarkerControls() {
        if (gamepad2.a) {
            robot.teamMarker.setPosition(robot.TEAM_MARKER_LATCH);
        } else if (gamepad2.b) {
            robot.teamMarker.setPosition(robot.TEAM_MARKER_RELEASE);
        } else if (gamepad2.x) {
            robot.teamMarker.setPosition(robot.TEAM_MARKER_IDLE);
        }

        telemetry.addLine("A = LATCH");
        telemetry.addLine("B = RELEASE");
        telemetry.addLine("X = IDLE");
        telemetry.update();
    }

    private void xrailBoxControls() {
        if (gamepad2.b) {
            robot.xrailFlipper.setPosition(robot.XRAIL_FLIPPER_RELEASE);
        } else if (gamepad2.a) {
            robot.xrailFlipper.setPosition(robot.XRAIL_FLIPPER_LEVEL);
        } else if (gamepad2.x) {
            robot.xrailFlipper.setPosition(robot.XRAIL_FLIPPER_STORAGE);
        }

        telemetry.addLine("A = LEVEL");
        telemetry.addLine("B = RELEASE");
        telemetry.addLine("X = STORAGE");
        telemetry.addData("Mason Drive", masonDrive);
        telemetry.update();
    }
}
