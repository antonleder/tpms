package com.automotive.tpms.activity.action

import com.automotive.tpms.activity.MainActivity
import kotlin.jvm.java

object ActivityActionDefaults {
    val ACTIVITY_A_INTENT_CLASS = MainActivity::class.java
    val ACTIVITY_B_INTENT_CLASS = MainActivity::class.java
    const val ACTIVITY_A_NAME = "Activity A"
    const val ACTIVITY_B_NAME = "Activity B"
}

enum class ActivityAction(
    val activityName: String,
    val nextActivityClass: Class<*>?
) {
    EMPTY_ACTIVITY_ACTION("", null),
    ACTIVITY_A_ACTION(
        ActivityActionDefaults.ACTIVITY_A_NAME,
        ActivityActionDefaults.ACTIVITY_B_INTENT_CLASS
    ),
    ACTIVITY_B_ACTION(
        ActivityActionDefaults.ACTIVITY_B_NAME,
        ActivityActionDefaults.ACTIVITY_A_INTENT_CLASS
    );

    companion object {
        fun fromString(str: String, logError: (String) -> Unit = { _ -> }): ActivityAction {
            try {
                enumValueOf<ActivityAction>(str)?.let { return it }
            } catch (e: IllegalArgumentException) {
                logError("ERROR: unable to convert string ´$str´ to the valid ActivityAction enum value")
            }

            return EMPTY_ACTIVITY_ACTION
        }
    }
}

fun ActivityAction.nextActivity(): ActivityAction =
    when (this) {
        ActivityAction.EMPTY_ACTIVITY_ACTION -> ActivityAction.EMPTY_ACTIVITY_ACTION
        ActivityAction.ACTIVITY_A_ACTION -> ActivityAction.ACTIVITY_B_ACTION
        ActivityAction.ACTIVITY_B_ACTION -> ActivityAction.ACTIVITY_A_ACTION
    }

