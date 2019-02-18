package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

@TeleOp(name = "Lower Lift")
public class LowerLift extends LinearOpMode {
    private final int LIFT_ENCODER_HOME_FIELD = -20255;

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor lift = hardwareMap.dcMotor.get("lift");
        lift.setDirection(REVERSE);

        double target = LIFT_ENCODER_HOME_FIELD;
        target -= lift.getCurrentPosition();

        telemetry.addLine("Ready");
        telemetry.update();

        int[] samples = {-1, -1, -1};

        waitForStart();

        lift.setPower(1);

        boolean moving = true;

        while (opModeIsActive() && lift.getCurrentPosition() <= target && moving) {
            if (Math.abs(lift.getCurrentPosition() - target) <= 1000) {
                lift.setPower(0.3);
            }

            // Check samples
            if (samples[0] == -1) {
                samples[0] = lift.getCurrentPosition();
            } else if (samples[1] == -1) {
                samples[1] = lift.getCurrentPosition();
            } else if (samples[2] == -1) {
                samples[2] = lift.getCurrentPosition();
            } else {
                // Check if we're moving
                if (samples[0] == samples[1] && samples[1] == samples[2]) {
                    moving = false;
                } else {
                    samples[0] = -1;
                    samples[1] = -1;
                    samples[2] = -1;
                }
            }

            telemetry.addData("Lift Encoders", lift.getCurrentPosition());
            telemetry.update();
            idle();
        }

        lift.setPower(0);
    }
}
