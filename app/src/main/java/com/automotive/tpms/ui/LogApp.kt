package com.automotive.tpms.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.automotive.tpms.R
import com.automotive.tpms.activity.MainActivity
import com.automotive.tpms.activity.MainActivity.Companion.DEFAULT_ACTIVITY_ACTION_PARAM_NAME
import com.automotive.tpms.activity.action.ActivityAction
import com.automotive.tpms.activity.action.nextActivity
import com.automotive.tpms.activity.viewmodel.MainViewModel

@Composable
fun HeaderComposable(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface, // Solid background is required
        tonalElevation = 4.dp,
    ) {
        Text(
            text = stringResource(R.string.app_logs_header),
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FooterComposable(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp), contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.app_logs_footer), style = MaterialTheme.typography.labelMedium)
    }
}

// TODO: coding style
// TODO: compose should not know how screen switching is performed -> replace with Navigator in future
@Preview(showBackground = true)
@Composable
fun MockUp(
    activityAction: ActivityAction = ActivityAction.EmptyActivityAction(),
    modifier: Modifier = Modifier,
    logLines: SnapshotStateList<String> = mutableStateListOf<String>(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val LOG_MAX_HEIGHT_FRACTION = 0.65f
    val CTRLS_MAX_HEIGHT_FRACTION = 1.0f - LOG_MAX_HEIGHT_FRACTION
    val INTERNAL_PADDING = 8.dp

    val count by viewModel.counter.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(INTERNAL_PADDING)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "${activityAction.activityName} (counter: $count)")
        }

        Row(modifier = Modifier.fillMaxHeight(LOG_MAX_HEIGHT_FRACTION)) {
            val LOG_LIST_BGCOLOR = Color.LightGray
            val listState = rememberLazyListState()

            LazyColumn(
                modifier = Modifier
                    .background(LOG_LIST_BGCOLOR)
                    .fillMaxSize(),
                state = listState
            ) {
                stickyHeader { HeaderComposable() }

                items(logLines) { line ->
                    Text(text = line)
                }

                item { FooterComposable() }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(CTRLS_MAX_HEIGHT_FRACTION)
        ) {
            val BTN_PADDING = 8.dp

            Column(
                modifier = Modifier.fillMaxHeight()
                    .padding(BTN_PADDING), verticalArrangement = Arrangement.Center
            ) {
                val context: Context = LocalContext.current
                val btnModifier = Modifier.fillMaxWidth()

                // Open another Activity
                Button(
                    onClick = {
                        val intent = Intent(context, MainActivity::class.java).apply {
                            putExtra(
                                DEFAULT_ACTIVITY_ACTION_PARAM_NAME,
                                activityAction.nextActivity()::class.simpleName
                            )
                        }
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            // TODO: show error message
                        }
                    }, modifier = btnModifier
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
                    onClick = {}, modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.start_bg_service_btn_text))
                }

                // Send broadcast
                Button(
                    onClick = {}, modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.send_bcast_btn_text))
                }

                // Request runtime permission
                Button(
                    onClick = {}, modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.runtime_perm_btn_text))
                }

                // Read data from system ContentProvider (e.g. Contacts or MediaStore — read-only via ContentResolver)
                Button(
                    onClick = {}, modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.read_cp_data_btn_text))
                }
            }
        }
    }
}