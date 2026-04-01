package com.automotive.tpms.ui

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automotive.tpms.R

data object LogApp {
    const val LOG_MAX_HEIGHT_FRACTION = 0.65f
    const val CTRLS_MAX_HEIGHT_FRACTION = 1.0f - LOG_MAX_HEIGHT_FRACTION
    val LOG_LIST_BGCOLOR = Color.LightGray
    val BTN_PADDING = 8.dp
}

@Preview(showBackground = true)
@Composable
fun MockUp(modifier: Modifier = Modifier,
           logLines: SnapshotStateList<String> = mutableStateListOf<String>()
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(LogApp.BTN_PADDING)) {
        Row(modifier = Modifier.fillMaxHeight(LogApp.LOG_MAX_HEIGHT_FRACTION)) {
            LazyColumn(
                modifier = Modifier
                    .background(LogApp.LOG_LIST_BGCOLOR)
                    .fillMaxSize(),
                state = rememberLazyListState()
            ) {
                // the scroll-list is empty in the beginning
                items(logLines) { line ->
                    Text(text = line)
                }
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(LogApp.CTRLS_MAX_HEIGHT_FRACTION)
        ) {
            Column(modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                val btnModifier = Modifier.fillMaxWidth()

                // Open Activity B
                Button(
                    onClick = {},
                    modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.act_b_btn_text))
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
                    onClick = {},
                    modifier = btnModifier
                ) {
                    Text(text = stringResource(R.string.runtime_perm_btn_text))
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