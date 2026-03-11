package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.ArrayDeque;
import java.util.Deque;

public class Spindex {

    public enum BallState {
        GREEN,
        PURPLE,
        EMPTY
    }

    public BallState[] ballStates = {BallState.EMPTY, BallState.EMPTY, BallState.EMPTY};

    public ElapsedTime drumIsSwitching = new ElapsedTime(23198);
    public final double switchCooldown = 1;

    public String drumMode = "intake";
    public int drumPosition = 0;

    private ServoImplEx drumServo;

    final double[] intakePositions = {0, 0.3826, 0.7831};
    // final double[] outtakePositions = {0.5745, 0.975, 0.1823}; old values
    final double[] outtakePositions = {0.5845, 0.98, 0.18};

    public DcMotor intakeMotor;

    public Servo flickServo;

    NormalizedColorSensor intakeColorSensor;

    /**
     * should run BEFORE waitForStart()
     * */
    public Spindex(HardwareMap hardwareMap) {

        drumServo = hardwareMap.get(ServoImplEx.class, "drumServo");
        drumServo.setPwmRange(new PwmControl.PwmRange(500, 2500));

        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        //intake.setDirection(DcMotor.Direction.REVERSE);

        flickServo = hardwareMap.get(Servo.class, "flick");

        intakeColorSensor = hardwareMap.get(NormalizedColorSensor.class, "intakeColorSensor");
        intakeColorSensor.setGain(15);

        //outtakeColorSensor = hardwareMap.get(NormalizedColorSensor.class, "idk lmao");
        setDrumPosition("intake", 0);
    }

    public void setDrumPosition(String mode, int position) {
        drumMode = mode;
        drumPosition = position;

        if (mode.equals("intake")) {
            drumServo.setPosition(intakePositions[position]);

        } else if (mode.equals("outtake")) {
            drumServo.setPosition(outtakePositions[position]);
        }

        drumIsSwitching.reset();
    }

    public void update() {
        if (drumIsSwitching.seconds() < switchCooldown) {
            return;
        }

        boolean ballDetected = ((DistanceSensor) intakeColorSensor).getDistance(DistanceUnit.CM) < 4;

        if (!ballDetected) {
            return;
        }

        NormalizedRGBA colors = intakeColorSensor.getNormalizedColors();
        final float[] hsvValues = new float[3];
        Color.colorToHSV(colors.toColor(), hsvValues);
        if (hsvValues[0] < 180) {
            ballStates[drumPosition] = BallState.GREEN;
        } else {
            ballStates[drumPosition] = BallState.PURPLE;
        }

        setDrumPosition("intake", (drumPosition + 1) % 3);
    }
}
