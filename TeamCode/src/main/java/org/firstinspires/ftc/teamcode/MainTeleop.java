package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class MainTeleop extends LinearOpMode {
    @Override
    public void runOpMode() {
        Spindex spindex = new Spindex(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            spindex.update();
        }
    }
}