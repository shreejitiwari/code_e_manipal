/*package com.example.calleme.Database

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

// ✅ Updated Data Class (profile_pic instead of profile_photo)
@Serializable
data class DocDetails(
    @SerialName("name") val name: String,
    @SerialName("profile_photo") val profile_photo: String? = null // ✅ Correct field name
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetchUsersScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var doctorList by remember { mutableStateOf<List<DocDetails>>(emptyList()) }
    var suggestions by remember { mutableStateOf<List<DocDetails>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // ✅ Fetch users when typing (debounced)
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            delay(300) // Debounce to prevent rapid API calls
            coroutineScope.launch {
                isLoading = true
                suggestions = fetchUsersFromSupabase(searchText)
                isLoading = false
            }
        } else {
            suggestions = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔍 Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Doctor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // ✅ Show doctor suggestions with profile pictures
            suggestions.forEach { doc ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            searchText = doc.name  // Fill search bar when clicked
                            suggestions = emptyList() // Hide suggestions
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    // ✅ Load profile picture with Coil
                    AsyncImage(
                        model = doc.profile_photo,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                    )

                    // ✅ Display doctor name
                    Text(text = doc.name, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ✅ Fetch users from Supabase with correct field names
suspend fun fetchUsersFromSupabase(query: String): List<DocDetails> {
    return try {
        Log.d("Supabase", "Fetching doctors matching: $query")
        val response = withContext(Dispatchers.IO) {
            SupabaseClient.client.postgrest.from("doctors").select().decodeList<DocDetails>()
        }
        // ✅ Filter locally if `.eq()` is not working
        response.filter { it.name.contains(query, ignoreCase = true) }
    } catch (e: Exception) {
        Log.e("Supabase Error", "Error fetching users: ${e.message}")
        emptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFetchUsersScreen() {
    FetchUsersScreen(rememberNavController())
}*/
/*package com.example.calleme.Database

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

// ✅ Updated Data Class with Correct Fields
@Serializable
data class Doctor(
    @SerialName("doc_id") val docId: Long,
    @SerialName("hospital_id") val hospitalId: Long,
    @SerialName("name") val name: String,
    @SerialName("specialization") val specialization: String,
    @SerialName("rating") val rating: Float?,
    @SerialName("Experience") val experience: Int? = 0,
    @SerialName("is_available_now") val isAvailable: Boolean,
    @SerialName("avl_time_slots") val availableTimeSlots: List<String>? = emptyList(),
    @SerialName("avl_days") val availableDays: List<String>? = emptyList(),
    @SerialName("profile_photo") val profilePhoto: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetchDoctorsScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var doctorList by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var suggestions by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // ✅ Fetch doctors when typing (debounced for better performance)
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            delay(300) // Prevents excessive API calls
            coroutineScope.launch {
                isLoading = true
                suggestions = fetchDoctorsFromSupabase(searchText)
                isLoading = false
            }
        } else {
            suggestions = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔍 Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Doctor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // ✅ Show doctor suggestions with full details
            suggestions.forEach { doctor ->
                DoctorCard(doctor) {
                    searchText = doctor.name  // Fill search bar when clicked
                    suggestions = emptyList() // Hide suggestions after selection
                }
            }
        }
    }
}

// ✅ DoctorCard UI to display search results
@Composable
fun DoctorCard(doctor: Doctor, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // ✅ Load profile picture (default if missing)
        AsyncImage(
            model = doctor.profilePhoto ?: "https://via.placeholder.com/100",
            contentDescription = "Doctor Profile",
            modifier = Modifier
                .size(50.dp)
                .padding(end = 10.dp)
        )

        Column {
            Text(text = doctor.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = doctor.specialization, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = "${doctor.experience} Years Experience", style = MaterialTheme.typography.bodySmall)

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
}

// ✅ Fetch doctors from Supabase with correct field names
suspend fun fetchDoctorsFromSupabase(query: String): List<Doctor> {
    return try {
        Log.d("Supabase", "Fetching doctors matching: $query")
        val response = withContext(Dispatchers.IO) {
            SupabaseClient.client.postgrest
                .from("doctors")
                .select()
                .decodeList<Doctor>()
        }
        // ✅ Filter results if `.eq()` doesn't work
        response.map { doctor ->
            doctor.copy(experience = doctor.experience ?: 0)  // Default to 0 if null
        }
    } catch (e: Exception) {
        Log.e("Supabase Error", "Error fetching doctors: ${e.message}")
        emptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFetchDoctorsScreen() {
    FetchDoctorsScreen(rememberNavController())
}*/
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


