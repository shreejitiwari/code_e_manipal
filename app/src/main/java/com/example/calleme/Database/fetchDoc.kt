package com.example.calleme.Database

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.copy
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.calleme.navigation.navigateFromAffectedAreaBack
import com.example.calleme.ui.theme.GreenPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

@Composable
fun FetchDoctorsScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var doctorList by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var filteredDoctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            doctorList = fetchDoctorsFromSupabase()
            filteredDoctors = doctorList
            isLoading = false
        }
    }

    LaunchedEffect(searchText) {
        filteredDoctors = if (searchText.isNotEmpty()) {
            doctorList.filter { it.name.contains(searchText, ignoreCase = true) }
        } else {
            doctorList
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Doctor") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (isLoading) {
            CircularProgressIndicator()
        }
        else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredDoctors) { doctor -> // Use items here
                    DoctorCard(doctor)
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp), // Reduced vertical padding
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column (modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(12.dp)){
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {}
                ) {
                AsyncImage(
                    model = doctor.profilePhoto,
                    contentDescription = "Doctor Profile",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 12.dp)
                )
                Column {
                    Text(text = doctor.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = doctor.specialization ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${doctor.experience} Years Experience",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row {
                        Text(
                            text = "⭐ ${doctor.rating ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (doctor.isAvailable) "🟢 Available Now" else "🔴 Not Available",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Button(
                onClick = {
                    Toast.makeText(context, "Appointment Booked !", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            )
            {
                Text("Book Appointment")
            }
        }
    }
}

suspend fun fetchDoctorsFromSupabase(): List<Doctor> {
    return try {
        withContext(Dispatchers.IO) {
            SupabaseClient.client.postgrest
                .from("doctors")
                .select()
                .decodeList<Doctor>()
                .map { doctor ->
                    doctor.copy(
                        specialization = doctor.specialization ?: "Unknown",
                        experience = doctor.experience ?: 0
                    )
                }
        }
    } catch (e: Exception) {
        Log.e("Supabase Error", "Error fetching doctors: ${e.message}")
        emptyList()
    }
}


