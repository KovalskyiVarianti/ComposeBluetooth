package com.example.composebluetooth.presentation

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
import com.example.composebluetooth.R
import com.example.composebluetooth.appComponent
import com.example.composebluetooth.receivers.broadcast.bluetooth.BluetoothDevicesBroadcastReceiver
import com.example.composebluetooth.receivers.broadcast.bluetooth.BluetoothStateBroadcastReceiver
import com.example.composebluetooth.ui.theme.ComposeBluetoothTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        registerBluetoothReceivers()
        setContent {
            ComposeBluetoothTheme {
                Scaffold(content = { MainContent(viewModel, this::requestPermissions) })
            }
        }
    }

    private fun registerBluetoothReceivers() {
        BluetoothStateBroadcastReceiver(
            this,
            onStateOn = {
                viewModel.onBluetoothTurnedOn()
            },
            onStateOff = {
                viewModel.onBluetoothTurnedOff()
            },
            onStateTurningOn = {
                viewModel.onBluetoothStateChanging(getString(R.string.bluetooth_turning_on))
            },
            onStateTurningOff = {
                viewModel.onBluetoothStateChanging(getString(R.string.bluetooth_turning_off))
            }
        ).apply { startObserving() }
        BluetoothDevicesBroadcastReceiver(
            this,
            onDeviceFound = { bluetoothDeviceDomainEntity ->
                viewModel.onDeviceFound(bluetoothDeviceDomainEntity)
                showToast("DEVICE FOUND ${bluetoothDeviceDomainEntity.name}")
            },
            onDiscoveryStarted = {
                viewModel.onDiscoveryStarted()
                showToast("DISCOVERY STARTED")
            },
            onDiscoveryFinished = {
                viewModel.onDiscoveryFinished()
                showToast("DISCOVERY FINISHED")
            }
        ).apply { startObserving() }
    }

    private val requestPermissionContract =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    private fun requestPermissions(permissions: List<String>) {
        requestPermissionContract.launch(permissions.toTypedArray())
    }
}

@Composable
fun MainContent(
    viewModel: MainViewModel,
    permissionsRequestAction: (List<String>) -> Unit
) {
    viewModel.fetchPairedDevices(permissionsRequestAction)

    when (val state = viewModel.uiState.collectAsState().value) {
        MainUiState.BluetoothTurnedOn -> {
            Column {
                BluetoothButtonContent(buttonTitle = "Turn off") {
                    viewModel.turnOffBluetooth(permissionsRequestAction)
                }
                BluetoothButtonContent(buttonTitle = "Discover") {
                    viewModel.startDiscovery(permissionsRequestAction)
                }
                DeviceListContent(
                    title = "Paired devices",
                    devicesState = viewModel.pairedDevices.collectAsState(
                        emptyList()
                    )
                )
                DeviceListContent(
                    title = "Discovered devices",
                    devicesState = viewModel.discoveredDevicesState.collectAsState(
                        emptyList()
                    )
                )
            }
        }
        MainUiState.BluetoothTurnedOff -> {
            BluetoothButtonContent(buttonTitle = "Turn on") {
                viewModel.turnOnBluetooth(permissionsRequestAction)
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
fun DeviceListContent(title: String, devicesState: State<List<DevicePresentationEntity>>) {
    val state = devicesState.value
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = title, style = typography.h5)
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
                Text(text = deviceEntity.name ?: "Unknown", style = typography.h4)
                Text(text = deviceEntity.macAddress, style = typography.h6)
            }
        }
    }
}