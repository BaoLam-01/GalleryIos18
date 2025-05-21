package com.base.capva.utils;

public enum ActionTouch {
    NONE,
    DRAG,
    ROTATE,
    ZOOM,
    ZOOM_WITH_TWO_TOUCH,
    SIZE_LEFT,
    SIZE_TOP,
    SIZE_RIGHT,
    SIZE_BOT,

    /**
     * crop
     */
    LEFT_TOP,
    RIGHT_TOP,
    RIGHT_BOT,
    LEFT_BOT,
}
