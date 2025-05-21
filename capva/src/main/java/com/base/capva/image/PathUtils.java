package com.base.capva.image;

import android.graphics.Path;
import android.view.View;

public class PathUtils {

    public static Path getPathClip(Path clipPath, View view, float cornerClip, String clipRound) {
        clipPath.reset();
        if (clipRound.equals("CIRCLE")) {
            float d = (float) Math.min(view.getWidth(), view.getHeight()) / 2f;
            clipPath.addCircle(view.getWidth() / 2f, view.getHeight() / 2f, d, Path.Direction.CW);
        } else if (clipRound.equals("defective rectangle bot")) {
            clipPath.addRect(0, 0, view.getWidth(), view.getHeight(), Path.Direction.CW);
        } else if (clipRound.equals("defective rectangle top")) {
            clipPath.addRect(0, 0, view.getWidth(), view.getHeight(), Path.Direction.CW);
        } else if (clipRound.equals("OVAL")) {
            clipPath.addRect(0, 0, view.getWidth(), view.getHeight(), Path.Direction.CW);
        } else if (cornerClip != 0) {
            clipPath.addRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerClip, cornerClip, Path.Direction.CW);
        } else {
            clipPath.addRect(0, 0, view.getWidth(), view.getHeight(), Path.Direction.CW);
        }
        return clipPath;
    }
}
