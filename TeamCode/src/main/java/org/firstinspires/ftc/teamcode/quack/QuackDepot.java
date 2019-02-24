package org.firstinspires.ftc.teamcode.quack;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.LEFT;
import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.RIGHT;

@Autonomous (name = "Depot", group = "V2")
public class QuackDepot extends QuackAutonomous {
    @Override
    public void runOpMode() {
        super.runOpMode();

        if (goldLocation == LEFT) {
            left();
        } else if (goldLocation == RIGHT) {
            right();
        } else {
            // TODO - implement center and right
            depotSample();
        }
    }

    private void depotSample() {
        drive(800, 0.7);

        pause();

        drive(-550, -0.7);
    }

    private void left() {
        drive(250, DRIVE_POWER);

        pause();

        strafe(LEFT, 500, STRAFE_POWER);

        pause();

        drive(1300, DRIVE_POWER);

        pause();

        drive(-100, -DRIVE_POWER);

        pause();

        strafe(LEFT, 700, STRAFE_POWER);

        pause();

        rotateTo(325);

        pause();

        drive(1500, DRIVE_POWER);

        pause();

        robot.teamMarker.setPosition(robot.TEAM_MARKER_RELEASE);

        pause();
        pause();

        drive(-1500, -DRIVE_POWER);

        pause();

        strafe(LEFT, 700, 0.8);

        pause();

        drive(-2000, -0.35);
    }

    private void right() {
        drive(1000, 0.8);

        rotateTo(45);

        pause();

        strafe(RIGHT, 1800, 1);

        pause();

        drive(1000, 0.8);

        pause();

        robot.teamMarker.setPosition(robot.TEAM_MARKER_RELEASE);
    }
}
