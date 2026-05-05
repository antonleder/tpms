package com.automotive.tpms.activity.factory

import android.app.Activity
import android.app.AppComponentFactory
import android.content.Intent
import com.automotive.tpms.activity.MainActivity
import com.automotive.tpms.activity.MainActivity.Companion.DEFAULT_ACTIVITY_ACTION_PARAM_NAME
import com.automotive.tpms.activity.action.ActivityAction

class TpmsActivityFactory : AppComponentFactory() {
    override fun instantiateActivity(
        cl: ClassLoader,
        className: String,
        intent: Intent?
    ): Activity {
        return if (className == MainActivity::class.java.name) {
            val param =
                intent?.getStringExtra(DEFAULT_ACTIVITY_ACTION_PARAM_NAME) ?: ""
            val activityAction = ActivityAction.fromString(param)
            MainActivity(activityAction)
        } else {
            super.instantiateActivity(cl, className, intent)
        }
    }
}