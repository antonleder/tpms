package com.automotive.tpms.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.automotive.tpms.R
import com.automotive.tpms.activity.MainActivity
import com.automotive.tpms.activity.MainActivity.Companion.DEFAULT_ACTIVITY_ACTION_PARAM_NAME
import com.automotive.tpms.activity.action.ActivityAction
import com.automotive.tpms.activity.action.nextActivity
import com.automotive.tpms.activity.viewmodel.MainViewModel
import com.automotive.tpms.helper.contacts.readConacts

data object LogApp {
    const val LOG_MAX_HEIGHT_FRACTION = 0.6f
    const val CTRLS_MAX_HEIGHT_FRACTION = 1.0f - LOG_MAX_HEIGHT_FRACTION
    val LOG_LIST_BGCOLOR = Color.LightGray
    val BTN_PADDING = 8.dp
}

// TODO: coding style
// TODO: compose should not know how screen switching is performed -> replace with Navigator in future
//@Preview(showBackground = true)
@Composable
fun MockUp(
    activityAction: ActivityAction = ActivityAction.EMPTY_ACTIVITY_ACTION,
    modifier: Modifier = Modifier,
    logLines: SnapshotStateList<String> = mutableStateListOf<String>(),
    viewModel: MainViewModel = hiltViewModel(),
    onContactsReadFn: (List<String>) -> Unit = {}
) {
    val count by viewModel.counter.collectAsState()

    // Flag to call composable function for run-time permission request
    var requestRuntimePermFlag by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LogApp.BTN_PADDING)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "${activityAction.activityName} (counter: $count)")
        }

        Row(modifier = Modifier.fillMaxHeight(LogApp.LOG_MAX_HEIGHT_FRACTION)) {
            LazyColumn(
                modifier = Modifier
                    .background(LogApp.LOG_LIST_BGCOLOR)
                    .fillMaxSize(),
                state = rememberLazyListState()
            ) {
                items(logLines) { line ->
                    Text(text = line)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(LogApp.CTRLS_MAX_HEIGHT_FRACTION)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                val context: Context = LocalContext.current
                val btnModifier = Modifier.fillMaxWidth()

                // Open another Activity
                Button(
                    onClick = {
                        val intent = Intent(context, MainActivity::class.java).apply {
                            putExtra(
                                DEFAULT_ACTIVITY_ACTION_PARAM_NAME,
                                activityAction.nextActivity().name
                            )
                        }
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            // TODO: show error message
                        }
                    },
                    modifier = btnModifier
                ) {
                    Text(
                        text = stringResource(R.string.act_btn_text) + " " + activityAction.nextActivity().activityName
                    )
                }

                Button(onClick = {
                    viewModel.incrementCounter()
                }, modifier = btnModifier) {
                    Text(text = stringResource(R.string.increment_counter))
                }

                // Start background task (Service)
                Button(
                    onClick = {},
                    modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.start_bg_service_btn_text))
                }

                // Send broadcast
                Button(
                    onClick = {},
                    modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.send_bcast_btn_text))
                }

                // Request runtime permission
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_CONTACTS
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            // Ask for run-time permission further in the Compose context
                            requestRuntimePermFlag = true
                        } else {
                            // permission is already granted -> read contacts
                            val contactsList =
                                readConacts(context.contentResolver) { name, phoneNumber ->
                                    "$name: $phoneNumber"
                                }
                            // submit contacts data to the caller handler
                            onContactsReadFn(contactsList)
                        }
                    },
                    modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.runtime_perm_btn_text))

                    if (requestRuntimePermFlag) {
                        PermissionWithRationale(
                            modifier = modifier,
                            permissionToBeRequested = Manifest.permission.READ_CONTACTS,
                            buttonText = stringResource(R.string.request_runtime_contacts_read_btn_text),
                        ) {
                            // permission is granted -> read contacts and submit to the caller handler
                            val contactsList = readConacts(context.contentResolver) { name, phoneNumber -> "$name: $phoneNumber" }
                            onContactsReadFn(contactsList)
                        }
                    }
                }

                // Read data from system ContentProvider (e.g. Contacts or MediaStore — read-only via ContentResolver)
                Button(
                    onClick = {},
                    modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.read_cp_data_btn_text))
                }
            }
        }
    }
}