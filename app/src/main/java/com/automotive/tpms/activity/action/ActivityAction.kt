package com.automotive.tpms.activity.action

object ActivityActionDefaults {
    const val ACTIVITY_A_URI = "android.intent.action.ACTIVITY_A"
    const val ACTIVITY_B_URI = "android.intent.action.ACTIVITY_B"
    const val ACTIVITY_A_NAME = "Activity A"
    const val ACTIVITY_B_NAME = "Activity B"
}

enum class ActivityAction(
    val activityName: String,
    val nextActivityName: String,
    val nextActivityURI: String
) {
    EMPTY_ACTIVITY_ACTION("", "", ""),
    ACTIVITY_A_ACTION(
        ActivityActionDefaults.ACTIVITY_A_NAME,
        ActivityActionDefaults.ACTIVITY_B_NAME,
        ActivityActionDefaults.ACTIVITY_B_URI
    ),
    ACTIVITY_B_ACTION(
        ActivityActionDefaults.ACTIVITY_B_NAME,
        ActivityActionDefaults.ACTIVITY_A_NAME,
        ActivityActionDefaults.ACTIVITY_A_URI
    )
}