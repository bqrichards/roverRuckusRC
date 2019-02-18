package org.firstinspires.ftc.teamcode.test;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldAlignDetector;
import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.disnodeteam.dogecv.filters.LeviColorFilter;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Size;

import static com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.UNKNOWN;

@TeleOp (name = "Webcam Tester", group = "Test")
public class WebcamTester extends LinearOpMode {
    GoldAlignDetector detector;

    @Override
    public void runOpMode() throws InterruptedException {
        WebcamName webcam = hardwareMap.get(WebcamName.class, "Webcam 1");

        detector = new GoldAlignDetector();
        detector.VUFORIA_KEY = "AdwI1KD/////AAABmUC5Du193Ev9mWh7pbGwY5kaAjJ" +
                "LosnOnmkTaYcicb0TEraARC6tZLVtsjR12Qc0PB7Ddenye7i+2m6aFj6Ds+U5XC5SF" +
                "oynEHD+EvKTGjI6P3sigllCw9M2XtYQ/3po9lu1Fd0KYJkHQiGFhgSiehwJ4qOsLkq" +
                "fUSVoCzOLWLC2BAQVcQM+Z7BKY0scoiNzl8u4eD/yyh8V6we3HyXpal3OHUCX5zJ4s" +
                "gNdT9pNS+AW4sVgSQ8C0fT/CnlTJAi6ssnphApMfIjg0ZAlSiicoXyZW0W1OX658Ov" +
                "/6aIZ1DwFZ3pskXSwK77JS7XLmFSDVQJLROSaIzw59yUY61gLe4YuqkD2YSsPXhg2t" +
                "m1bf/ZU";
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(), DogeCV.CameraMode.WEBCAM, false, webcam);
        detector.yellowFilter = new LeviColorFilter(LeviColorFilter.ColorPreset.YELLOW, 100);
        detector.useDefaults();
        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA;
        detector.enable();

        SamplingOrderDetector.GoldLocation goldLocation = UNKNOWN;

        waitForStart();

        while (opModeIsActive()) {
            SamplingOrderDetector.GoldLocation updatedGoldLoc = grabGoldLocation();
            if (updatedGoldLoc != UNKNOWN) {
                goldLocation = updatedGoldLoc;
            }

            telemetry.addData("isFound", detector.isFound());
            telemetry.addData("Location", goldLocation);
            telemetry.update();
        }

        detector.disable();
    }

    private SamplingOrderDetector.GoldLocation grabGoldLocation() {
        Size size = detector.getAdjustedSize();
        double center = size.width / 2;

        double leftDist = Math.abs(detector.getXPosition());
        double centerDist = Math.abs(detector.getXPosition() - center);
        double rightDist = Math.abs(detector.getXPosition() - size.width);
        double closest = minimum(leftDist, centerDist, rightDist);

        if (closest == leftDist) {
            return SamplingOrderDetector.GoldLocation.LEFT;
        } else if (closest == centerDist) {
            return SamplingOrderDetector.GoldLocation.CENTER;
        } else if (closest == rightDist) {
            return SamplingOrderDetector.GoldLocation.RIGHT;
        } else {
            return SamplingOrderDetector.GoldLocation.UNKNOWN;
        }
    }

    private double minimum(double... numbers) {
        double min = Integer.MAX_VALUE;
        for (double num : numbers) {
            min = Math.min(min, num);
        }
        return min;
    }
}
