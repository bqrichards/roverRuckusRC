package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.LinkedList;
import java.util.Map;

@TeleOp (name = "Servo Tester", group = "Test")
@Disabled
public class ServoTester extends OpMode {
    private LinkedList<Map.Entry<String, Servo>> servos = new LinkedList<>();
    private int selectedServo = 0;

    private boolean allowedUpAndDown = true;
    private boolean allowedLeftRight = true;

    @Override
    public void init() {
        servos.addAll(hardwareMap.servo.entrySet());
    }

    @Override
    public void loop() {
        if (servos.size() < 1) {
            telemetry.addLine("No Servos");
            telemetry.update();
            return;
        }

        if (gamepad1.dpad_up ^ gamepad1.dpad_down) {
            if (allowedUpAndDown) {
                if (gamepad1.dpad_up) selectedServo++;
                else selectedServo--;

                if (selectedServo < 0) selectedServo = servos.size() - 1;
                if (selectedServo >= servos.size()) selectedServo = 0;

                allowedUpAndDown = false;
            }
        } else {
            allowedUpAndDown = true;
        }

        if (gamepad1.dpad_left ^ gamepad1.dpad_right) {
            if (allowedLeftRight) {
                if (gamepad1.dpad_left) decrement();
                else increment();
                allowedLeftRight = false;
            }
        } else {
            allowedLeftRight = true;
        }

        telemetry.addData("Selected Servo", getName());
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

    private String getName() {
        return servos.get(selectedServo).getKey();
    }

    private void setPosition(double position) {
        servos.get(selectedServo).getValue().setPosition(position);
    }

    private double getPosition() {
        return servos.get(selectedServo).getValue().getPosition();
    }
}
