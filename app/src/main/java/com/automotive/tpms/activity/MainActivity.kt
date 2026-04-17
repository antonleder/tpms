package com.automotive.tpms.activity

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.automotive.tpms.activity.action.ActivityAction
import com.automotive.tpms.activity.viewmodel.MainViewModel
import com.automotive.tpms.ui.MockUp
import com.automotive.tpms.ui.theme.TpmsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * TODO:
 * * activity should not have parameters, only layout parameter for view has sense
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var activityAction = ActivityAction.EMPTY_ACTIVITY_ACTION
    private val viewModel: MainViewModel by viewModels()
    private val loggedLines = mutableListOf<String>()

    private fun addLogLine(line: String) {
        @OptIn(ExperimentalTime::class) val now =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
        val timestamp = LOG_TIME_PATTERN.format(now)
        val activityName: String = activityAction.activityName
        loggedLines.add("[$timestamp] $activityName: $line\n")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        savedInstanceState?.getStringArrayList(BUNDLE_LOG_LINES_KEY)?.let { result ->
            loggedLines.addAll(result.sorted())
        }

        addLogLine(
            "onCreate(): intent used to start the Activity: ${if (intent != null) intent.toString() else "null"} " + "${if (intent != null && intent.extras != null) " extras:" + intent.extras.toString() else ""} "
        )

        addLogLine("onCreate(): ViewModel timestamp: ${viewModel.timestamp}")

        // check whether the Activity was launched from other activity with an intent and read
        // string extra parameter to configure the activity
        intent?.let {
            intent.getStringExtra(DEFAULT_ACTIVITY_ACTION_PARAM_NAME)?.let {
                activityAction = ActivityAction.fromString(
                    str = it,
                    logError = { str -> addLogLine("onCreate() - intent string extra ´$DEFAULT_ACTIVITY_ACTION_PARAM_NAME´: $str") })
            }
        }

        // Extracting default activity action from the manifest if provided action is empty
        if (activityAction == ActivityAction.EMPTY_ACTIVITY_ACTION) {
            activityAction = getActivityActionFromManifest()
            check(activityAction != ActivityAction.EMPTY_ACTIVITY_ACTION)
        }

        addLogLine("onCreate(): Activity created (${if (savedInstanceState != null) "not first creation" else "first creation"})")
        /** Basic application startup logic that happens only once for the entire life of the activity
         *
         * Typical actions:
         * * Initializing member variables: adapters, and data sources.
         * * Associating the activity with a ViewModel for state management.
         * * Using the savedInstanceState Bundle to restore data if the activity is being recreated
         *   (e.g., after a screen rotation).
         */

        /** Hooked up lifecycle-aware component that receives the ON_CREATE event.
         * The method annotated with @OnLifecycleEvent is called
         * => lifecycle-aware component performs any setup code it needs for the created state.
         *
         * TODO: Does this happen implicitly or requires explicit code operations?
         */

        enableEdgeToEdge()
        setContent {
            TpmsTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxWidth()) { innerPadding ->
                    MockUp(
                        activityAction = activityAction,
                        modifier = Modifier.Companion.padding(innerPadding),
                        logLines = loggedLines.toMutableStateList()
                    )
                }
            }
        }
    }
    // TODO: empty Android compose project -> App compose function

    // move out all of the compose related stuff from Activity
    // TODO: obtain parameters from the Bundle
    private fun getActivityActionFromManifest(): ActivityAction {
        val actInfo: ActivityInfo = getPackageManager().getActivityInfo(
            getComponentName(), PackageManager.GET_META_DATA
        );

        // Read default activity action from the manifest
        val modeString: String? = actInfo.metaData.getString(DEFAULT_ACTIVITY_ACTION_PARAM_NAME);

        // Try to convert string to the valid enum value
        modeString?.let {
            return ActivityAction.fromString(
                str = modeString,
                logError = { str -> addLogLine("onCreate() - read default activity action from the manifest: $str") })
        }

        return ActivityAction.EMPTY_ACTIVITY_ACTION
    }


    /** Restore the Activity state if there was something saved before.
     *
     * Caution: Always call the superclass implementation of onRestoreInstanceState() so the default
     * implementation can restore the state of the view hierarchy.
     *
     * Is called after the onStart().
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        addLogLine("onRestoreInstanceState(): Activity state restored")
    }

    /**
     * Handle the Activity becomes visible to the user as the app prepares for the activity to enter
     * the foreground and become interactive.
     *
     * Called every time the Activity returns from the background.
     *
     * TODO:
     * * Initialize maintained UI.
     * * Lifecycle-aware component (tied to the activity's lifecycle) receives the ON_START event.
     * * Dynamic BroadcastReceivers registration.
     *
     * Keep this method as lightweight as possible to avoid UI lagging.
     *
     * If the Activity is destroyed as the result of a configuration change, the system immediately
     * creates a new Activity instance and then calls onCreate() on that new instance in the new
     * configuration.
     */
    override fun onStart() {
        super.onStart()

        addLogLine("onStart(): Activity visible to the user")
    }

    /** Handle the Activity comes to the foreground and interacts with the user.
     *
     * Occurs every time the Activity is returned to the foreground.
     *
     * TODO:
     * *  Lifecycle-aware component (tied to the activity's lifecycle) receives the ON_RESUME event.
     *  * Lifecycle components can enable any functionality that needs to run while the component is
     *    visible and in the foreground (e.g. starting a camera preview).
     * *  Initialize components, that have been released during onPause().
     * * Perform any other initializations, that must occur each time the activity is resumed.
     *
     * If the Activity returns from the PAUSED state to the RESUMED state => the system keeps the
     * Activity instance resident in memory, by recalling that instance, when the system invokes
     * onResume().
     * In this scenario, there is NO need to re-initialize components created during any of the
     * callback methods leading up to the Resumed state.
     *
     * Even if the system destroys the process while the Activity is stopped, the system still:
     * * retains the state of the View objects in a Bundle (a blob of key-value pairs),
     * * and restores them if the user navigates back to the Activity.
     */
    override fun onResume() {
        super.onResume()

        addLogLine("onResume(): Activity in the foreground")
    }

    /** Handle the activity is no longer in the foreground due to an interruptive event.
     *
     * The Activity enters the Paused state.
     *
     * Something happens to take the focus away from the Activity, such as:
     * * An event that interrupts app execution:
     *   * the device receiving a phone call,
     *   * the user navigating to another activity,
     *   * or the device screen turning off.
     * * The first indication that the user is leaving the Activity (it does not always mean the
     *   Activity is being destroyed).
     *
     * TODO:
     * * Release resources, that have been acquired upon entering the RESUME state.
     * * Pause or adjust operations and that you expect to resume shortly while the Activity is in
     *   the Paused state, that:
     *   * either can't continue,
     *   * or might continue in moderation.
     *  * Release while your activity is Paused and the user does not need the following resources:
     *    * system resources,
     *    * handles to sensors (like GPS),
     *    * or any resources that affect battery life.
     *
     * Lifecycle-aware component (tied to the activity's lifecycle) receives the ON_RESUME event
     * => this is where the lifecycle components can stop any functionality that does not need to
     *    run while the component is not in the foreground.
     *
     * The activity might be fully visible even when it is in the Paused state:
     * * The opening of a new, semi-transparent activity, such as a dialog, pauses the Activity it covers.
     * * As long as the Activity is partially visible but not in focus, it remains paused.
     *
     * onPause execution is very brief and does not offer enough time to perform save operations
     * => such work might not complete before the method completes => avoid:
     * * saving application or user data,
     * * making network calls,
     * * or execute database transactions.
     *
     * Completing of the method does not mean that the activity leaves the PAUSED state
     * => the Activity remains in this state until:
     * * either the activity resumes,
     * * or it becomes completely invisible to the user.
     */
    override fun onPause() {
        super.onPause()

        addLogLine("onPause(): Activity in the background")
    }

    /** Handle the activity becomes completely invisible to the user.
     *
     * This can occur when:
     * * a newly launched Activity covers the entire screen,
     * * the activity finishes running and is about to be terminated.
     *
     * Lifecycle-aware component tied to the activity's lifecycle receives the ON_STOP event
     * => this is where the lifecycle components can stop any functionality:
     * * that does not need to run
     * * while the component is not visible on the screen.
     *
     * Paused activity might still be fully visible => Activity's STOPPED state is more preferable
     * to fully release or adjust:
     * * UI-related resources and operations.
     * * other resources, that are not needed while the app is not visible to the user.
     * Example:
     * * pause animations,
     * * switch from fine-grained to coarse-grained location updates.
     *
     * Using onStop() instead of onPause() means that UI-related work continues.
     *
     * Perform heavy-load shutdown operations:
     * * save information to a DB,
     * * save the Activity's state,
     * * etc.
     *
     * When the Activity enters the Stopped state:
     * * the Activity object is kept resident in memory,
     * * it maintains all state and member information,
     * * but is not attached to the window manager.
     * When the activity resumes, it recalls this information.
     *
     * Once the Activity is stopped, the system might destroy the process that contains the Activity
     * if the system needs to recover memory.
     */
    override fun onStop() {
        super.onStop()

        addLogLine("onStop(): Activity completely invisible to the user")
    }

    /** Handle the Activity comes back from the Stopped state to interact with the user.
     *
     * TODO: what to do in this method?
     *
     * The Activity is being re-displayed to the user (the user has navigated back to it).
     * Handle only the case of user's session resume:
     * * Track session resume event to handle the period of user inactivity (session logout timeout,
     *   authorization check => what UI to show the user in the START state, etc).
     * * Time-dependent state(s) reset (clearing out some UI fields, etc).
     * * Update data, that could have been changed from the outside (if the user went to the
     *   Settings and changed anything).
     *
     * Should be lightweight so user do not observe black screen upon returning to the application.
     *
     * If the Activity is recreated due to a configuration change, the ViewModel does not have to
     * do anything, since it is preserved and given to the next Activity instance.
     */
    override fun onRestart() {
        super.onRestart()

        addLogLine("onRestart(): Activity returned to the foreground")
    }

    /** Handle the Activity pre-destruction
     *
     * Reasons for entering the DESTROYED state:
     * * The activity is finishing, due to the user completely dismissing the activity.
     * * The system is temporarily destroying the Activity due to a configuration change:
     *   * device rotation,
     *   * entering multi-window mode.
     *
     * Lifecycle-aware component tied to the activity's lifecycle receives the ON_DESTROY event.
     * => This is where the lifecycle components can clean up anything they need to before the
     *    Activity is destroyed.
     *
     * If the Activity isn't recreated, then the ViewModel has the onCleared() method called,
     * where it can clean up any data it needs to before being destroyed.
     * This scenario can be distinguished with the isFinishing() method.
     *
     * If the Activity is finishing, onDestroy() is the final lifecycle callback of the Activity.
     */
    override fun onDestroy() {
        super.onDestroy()

        addLogLine("onDestroy(): Activity destroyed")
    }

    /** Handle Activity's state saving before stopping it.
     *
     * Is not called when:
     * * the user explicitly closes the activity,
     * * or in other cases when finish() is called.
     *
     * As your activity begins to stop, the system calls the onSaveInstanceState() method so your
     * activity can save state information to an instance state bundle.
     * The default implementation of this method saves transient information about the state of the
     * activity's view hierarchy, such as the text in an EditText widget or the scroll position of
     * a ListView widget.
     *
     * To save additional instance state information for your activity, override
     * onSaveInstanceState() and add key-value pairs to the Bundle object that is saved in the event
     * that your activity is destroyed unexpectedly.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        addLogLine("onSaveInstanceState(): Activity state saved")

        outState.putStringArrayList(BUNDLE_LOG_LINES_KEY, loggedLines.toCollection(ArrayList()))
    }

    companion object {
        const val BUNDLE_LOG_LINES_KEY = "logLines"
        val LOG_TIME_PATTERN = LocalTime.Format {
            hour()
            char(':')
            minute()
            char(':')
            second()
            char('.')
            secondFraction(3) // 3 цифры для миллисекунд
        }
        const val DEFAULT_ACTIVITY_ACTION_PARAM_NAME = "default_activity_action"
    }
}