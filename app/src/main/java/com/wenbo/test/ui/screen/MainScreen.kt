package com.wenbo.test.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.wenbo.test.model.BalanceItem
import com.wenbo.test.model.UserPreferences
import com.wenbo.test.viewmodel.LoadingState
import com.wenbo.test.viewmodel.WalletViewModel

@Composable
fun MainScreen(
    viewModel: WalletViewModel,
    onNavigateToCurrencyManagement: () -> Unit
) {
    val balanceItems by remember { viewModel.balanceItems }.collectAsState()
    val loadingState by remember { viewModel.loadingState }.collectAsState()
    val errorState by remember { viewModel.errorState }.collectAsState()

    // 计算总余额
    val totalBalance = remember(balanceItems) {
        balanceItems.sumOf { it.usdValue }
    }

    Scaffold(
        topBar = { AppBar(viewModel, totalBalance) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCurrencyManagement) {
                Icon(Icons.Filled.Settings, contentDescription = "Manage currencies")
            }
        }
    ) { padding ->
        when (loadingState) {
            LoadingState.LOADING -> LoadingView(padding)
            LoadingState.ERROR -> ErrorView(errorState, viewModel, padding)
            else -> WalletList(balanceItems, padding)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(viewModel: WalletViewModel, totalBalance: Double,) {
    var expanded by remember { mutableStateOf(false) }
    val useMockApi by viewModel.useMockApi.collectAsState()
    TopAppBar(
        title = {
            Column {
                Text("Wallet Dashboard", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Total: $${"%.2f".format(totalBalance)} USD",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        actions = {
            // API 模式切换按钮
            IconButton(onClick = { viewModel.toggleApiMode(!useMockApi) }) {
                Icon(
                    imageVector = if (useMockApi) Icons.Sharp.Done else Icons.Sharp.Close,
                    contentDescription = if (useMockApi) "Using Mock API" else "Using Real API"
                )
            }

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Sort options"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sort by name") },
                    onClick = {
                        viewModel.updateSortOption(UserPreferences.SortOption.NAME)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sort by value (asc)") },
                    onClick = {
                        viewModel.updateSortOption(UserPreferences.SortOption.VALUE_ASC)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sort by value (desc)") },
                    onClick = {
                        viewModel.updateSortOption(UserPreferences.SortOption.VALUE_DESC)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sort by balance") },
                    onClick = {
                        viewModel.updateSortOption(UserPreferences.SortOption.BALANCE)
                        expanded = false
                    }
                )
            }
        }
    )
}

@Composable
fun LoadingView(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(error: String?, viewModel: WalletViewModel, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.loadDashboardData() }) {
            Text("Retry")
        }
    }
}

@Composable
fun WalletList(items: List<BalanceItem>, padding: PaddingValues) {
    LazyColumn(modifier = Modifier.padding(padding)) {
        items(items) { item ->
            WalletItem(item)
        }
    }
}

@Composable
fun WalletItem(item: BalanceItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.currency.colorful_image_url,
                contentDescription = item.currency.name,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(item.currency.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${"%.${item.currency.token_decimal}f".format(item.balance)} ${item.currency.coin_id}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "≈ $${"%.2f".format(item.usdValue)} USD",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}