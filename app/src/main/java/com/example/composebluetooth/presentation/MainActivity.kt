package com.example.composebluetooth.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.composebluetooth.appComponent
import com.example.composebluetooth.receivers.broadcast.bluetooth.BluetoothStateBroadcastReceiver
import com.example.composebluetooth.ui.theme.ComposeBluetoothTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        registerBluetoothStateBroadcastReceiver()
        setContent {
            ComposeBluetoothTheme {
                Scaffold(content = { MainContent(viewModel, ::requestPermission) })
            }
        }
    }

    private val requestPermissionContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                lifecycleScope.launchWhenCreated {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            MainUiState.BluetoothTurnedOff -> viewModel.turnOnBluetooth { }
                            MainUiState.BluetoothTurnedOn -> viewModel.turnOffBluetooth { }
                            else -> {}
                        }
                    }
                }
            }
        }

    private fun requestPermission(permission: String) {
        requestPermissionContract.launch(permission)
    }

    private fun Activity.registerBluetoothStateBroadcastReceiver() {
        lifecycle.addObserver(
            BluetoothStateBroadcastReceiver(this,
                onStateOn = {
                    viewModel.onBluetoothTurnedOn()
                    showToast("STATE_ON")
                },
                onStateOff = {
                    viewModel.onBluetoothTurnedOff()
                    showToast("STATE_OFF")
                },
                onStateTurningOn = {
                    viewModel.onBluetoothStateChanging("Turning on")
                    showToast("STATE_TURNING_ON")
                },
                onStateTurningOff = {
                    viewModel.onBluetoothStateChanging("Turning off")
                    showToast("STATE_TURNING_OFF")
                }
            )
        )
    }
}

@Composable
fun MainContent(
    viewModel: MainViewModel,
    permissionRequestAction: (String) -> Unit
) {
    viewModel.findPairedDevices(permissionRequestAction)

    when (val state = viewModel.uiState.collectAsState().value) {
        MainUiState.BluetoothTurnedOn -> {
            Column {
                BluetoothButtonContent(buttonTitle = "Turn off") {
                    viewModel.turnOffBluetooth(permissionRequestAction)
                }
                PairedDeviceListContent(
                    pairedDevicesState = viewModel.pairedDevices.collectAsState(
                        emptyList()
                    )
                )
            }
        }
        MainUiState.BluetoothTurnedOff -> {
            BluetoothButtonContent(buttonTitle = "Turn on") {
                viewModel.turnOnBluetooth(permissionRequestAction)
            }
        }
        is MainUiState.Loading -> {
            LoadingContent(title = state.title)
        }
    }

}


@Composable
fun LoadingContent(title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = title, style = typography.h6)
    }

}

@Composable
fun BluetoothButtonContent(buttonTitle: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = { onClick() },
            content = { Text(text = buttonTitle) },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Composable
fun PairedDeviceListContent(pairedDevicesState: State<List<DevicePresentationEntity>>) {
    val state = pairedDevicesState.value
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            items(items = state, itemContent = { DeviceListItem(it) })
        }
    }

}

@Composable
fun DeviceListItem(deviceEntity: DevicePresentationEntity) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = deviceEntity.name, style = typography.h4)
                Text(text = deviceEntity.macAddress, style = typography.h6)
            }
        }
    }
}
