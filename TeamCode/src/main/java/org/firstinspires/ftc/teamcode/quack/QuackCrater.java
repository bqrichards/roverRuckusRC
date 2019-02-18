package org.firstinspires.ftc.teamcode.quack;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.CENTER;
import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.LEFT;
import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.RIGHT;
import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.UNKNOWN;

@Autonomous (name = "Crater", group = "V2")
public class QuackCrater extends QuackAutonomous {
    private final int LEFT_DRIVE_DISTANCE = 1500;
    private final int CENTER_DRIVE_DISTANCE = 2100;
    private final int RIGHT_DRIVE_DISTANCE = 3000;

    private final int LEFT_DEPOT_DISTANCE = 2000;
    private final int CENTER_DEPOT_DISTANCE = 1900;
    private final int RIGHT_DEPOT_DISTANCE = 1900;

    @Override
    public void runOpMode() {
        super.runOpMode();

        if (goldLocation == LEFT) {
            left();
        } else if (goldLocation == CENTER || goldLocation == UNKNOWN) {
            center();
        } else {
            right();
        }
    }

    private void rotateTowardsDepot() {
        rotateTo(130);

        pause();

        strafe(RIGHT, 1500, 1);

        pause();
    }

    private void left() {
        strafe(LEFT, 550, 1);

        pause();

        drive(575, 0.5);

        pause();

        drive(-575, -0.5);

        pause();

        rotateTo(84);

        pause();

        drive(LEFT_DRIVE_DISTANCE, 0.8);

        pause();

        rotateTowardsDepot();

        pause();

        drive(LEFT_DEPOT_DISTANCE, 0.8);

        pause();

        robot.teamMarker.setPosition(robot.TEAM_MARKER_RELEASE);

        sleep(1000);

        drive(-2500, -0.8);

        pause();

        strafe(RIGHT, 200, 1);

        pause();

        drive(-800, -0.35);
    }

    private void center() {
        drive(600, 0.5);

        pause();

        drive(-550, -0.5);

        pause();

        rotateTo(83);

        pause();

        drive(CENTER_DRIVE_DISTANCE, 0.8);

        pause();

        rotateTowardsDepot();

        pause();

        drive(CENTER_DEPOT_DISTANCE, 0.8);

        pause();

        robot.teamMarker.setPosition(robot.TEAM_MARKER_RELEASE);

        sleep(1000);

        drive(-2600, -0.8);

        pause();

        strafe(RIGHT, 350, 1);

        pause();

        drive(-600, -0.35);
    }

    private void right() {
        drive(700, 0.5);

        pause();

        drive(-700, -0.5);

        pause();

        rotateTo(84);

        pause();

        drive(RIGHT_DRIVE_DISTANCE, 0.8);

        pause();

        rotateTowardsDepot();

        pause();

        drive(RIGHT_DEPOT_DISTANCE, 0.8);

        pause();

        robot.teamMarker.setPosition(robot.TEAM_MARKER_RELEASE);

        sleep(1000);

        drive(-2500, -0.8);

        pause();

        strafe(RIGHT,  300, 0.8);

        pause();

        drive(-600, -0.3);
    }
}
