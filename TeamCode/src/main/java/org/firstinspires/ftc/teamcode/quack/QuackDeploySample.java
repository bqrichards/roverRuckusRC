package org.firstinspires.ftc.teamcode.quack;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous (name = "Deploy + Sample", group = "V2")
public class QuackDeploySample extends QuackAutonomous {
    @Override
    public void runOpMode() {
        super.runOpMode();

        drive(800, 0.7);

        pause();

        drive(-550, -0.7);
    }
}
