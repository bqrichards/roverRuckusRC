package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "Specific Servo Tester", group = "Test")
@Disabled
public class SpecificServoTester extends OpMode {
    private Servo servo;

    private boolean allowedLeftRight = true;

    @Override
    public void init() {
        servo = hardwareMap.servo.get("teamMarker");
    }

    @Override
    public void loop() {
        if (gamepad1.dpad_left ^ gamepad1.dpad_right) {
            if (allowedLeftRight) {
                if (gamepad1.dpad_left) decrement();
                else increment();
                allowedLeftRight = false;
            }
        } else {
            allowedLeftRight = true;
        }

        telemetry.addLine("A = 0, B = 0.25, X = 0.5, Y = 0.75, RB = 1");

        if (gamepad1.a) {
            setPosition(0);
        } else if (gamepad1.b) {
            setPosition(0.25);
        } else if (gamepad1.x) {
            setPosition(0.5);
        } else if (gamepad1.y) {
            setPosition(0.75);
        } else if (gamepad1.right_bumper) {
            setPosition(1);
        }

        telemetry.addData("Position", getPosition());
        telemetry.update();
    }

    private void increment() {
        setPosition(getPosition() + 0.01);
    }

    private void decrement() {
        setPosition(getPosition() - 0.01);
    }

    private void setPosition(double position) {
        servo.setPosition(position);
    }

    private double getPosition() {
        return servo.getPosition();
    }
}
