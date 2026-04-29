package com.automotive.tpms.activity.action

import com.automotive.tpms.activity.MainActivity
import kotlinx.serialization.modules.EmptySerializersModule
import kotlin.jvm.java

sealed class ActivityAction(
    val activityName: String,
    val nextActivityClass: Class<*>?
) {
    object EmptyActivityAction : ActivityAction("", null)
    object ActivityAAction : ActivityAction(
        ACTIVITY_A_NAME,
        ACTIVITY_B_INTENT_CLASS
    )

    object ActivityBAction : ActivityAction(
        ACTIVITY_B_NAME,
        ACTIVITY_A_INTENT_CLASS
    )

    companion object {
        val ACTIVITY_A_INTENT_CLASS = MainActivity::class.java
        val ACTIVITY_B_INTENT_CLASS = MainActivity::class.java
        const val ACTIVITY_A_NAME = "Activity A"
        const val ACTIVITY_B_NAME = "Activity B"
        fun fromString(str: String, logError: (String) -> Unit = { _ -> }): ActivityAction {
            when (str) {
                ActivityAAction::class.simpleName -> return ActivityAAction
                ActivityBAction::class.simpleName -> return ActivityBAction
            }
            logError("ERROR: unable to convert string ´$str´ to the valid ActivityAction enum value")

            return EmptyActivityAction
        }
    }
}

fun ActivityAction.nextActivity(): ActivityAction =
    when (this) {
        is ActivityAction.EmptyActivityAction -> ActivityAction.EmptyActivityAction
        is ActivityAction.ActivityAAction -> ActivityAction.ActivityBAction
        is ActivityAction.ActivityBAction -> ActivityAction.ActivityAAction
    }

