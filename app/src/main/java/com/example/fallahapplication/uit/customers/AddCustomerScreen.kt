package com.example.fallahapplication.uit.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.data.model.Customer
import com.example.fallahapplication.uit.components.FallahButton
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Scaffold(
        topBar = { FallahTopBar(title = "إضافة زبون جديد", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم الزبون *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم الهاتف") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("العنوان") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            FallahButton(
                text = "حفظ الزبون",
                enabled = name.isNotBlank(),
                onClick = {
                    viewModel.saveCustomer(
                        Customer(
                            name = name,
                            phone = phone,
                            address = address
                        )
                    )
                    onSaved()
                }
            )
        }
    }
}