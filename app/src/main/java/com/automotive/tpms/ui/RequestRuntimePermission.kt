package com.automotive.tpms.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import com.automotive.tpms.R

@Composable
fun PermissionWithRationale(
    permissionToBeRequested: String = Manifest.permission.READ_CONTACTS,
    modifier: Modifier = Modifier,
    buttonText: String = stringResource(R.string.request_runtime_contacts_read_btn_text),
    onPermissionGrantedFn: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showRationaleFlag by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGrantedFn()
        }
    }

    val whetherRationaleShouldBeShownFn = { permission: String ->
        // Check whether a rationale should be shown to the user:
        // * true:
        //   * permission has been denied by the user, but not permanently -> show rationale
        // * false:
        //   * permission has been permanently denied previously (do not ask again)
        //   * permission is being asked the 1st time
        //   * permission has been already granted
        //   * device policy does not allow permissions to be granted to the application
        val shouldShow = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
        } ?: false

        if (shouldShow) {
            showRationaleFlag = true
        } else {
            launcher.launch(permission)
        }
    }

    if (showRationaleFlag) {
        AlertDialog(
            onDismissRequest = { showRationaleFlag = false },
            title = { Text(stringResource(R.string.contacts_access_request_dialog_title)) },
            text = { Text(stringResource(R.string.runtime_access_justification)) },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleFlag = false
                    launcher.launch(permissionToBeRequested)
                }) { Text(stringResource(R.string.runtime_perm_btn_text)) }
            }
        )
    }

    Button(onClick = { whetherRationaleShouldBeShownFn(permissionToBeRequested) }, modifier = modifier) {
        Text(buttonText)
    }
}