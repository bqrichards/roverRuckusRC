package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class AsciiArt {
    private String[] dance = {
            "(•_•)\n<)   )╯\n/    \\\n\n",
                    "\\(•_•)\n(   (> \n/    \\\n\n",
                    " (•_•)\n<)   )>  \n/    \\"
    };

    private ElapsedTime time;
    private final double INTERVAL = 0.3; // Seconds for art to switch
    private int index = 0;

    public AsciiArt() {
        time = new ElapsedTime();
    }

    public String update() {
        if (time.time() < INTERVAL) return dance[index];
        time.reset();
        index++;
        if (index == dance.length) index = 0;
        return dance[index];
    }
}
