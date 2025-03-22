package com.example.calleme.Database


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.calleme.ui.theme.GreenPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest


@Composable
fun FetchHospitalsScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var hospitalList by remember { mutableStateOf<List<Hospital>>(emptyList()) }
    var filteredHospitals by remember { mutableStateOf<List<Hospital>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            hospitalList = fetchHospitalsFromSupabase()
            filteredHospitals = hospitalList
            isLoading = false
        }
    }

    LaunchedEffect(searchText) {
        filteredHospitals = if (searchText.isNotEmpty()) {
            hospitalList.filter { it.name.contains(searchText, ignoreCase = true) }
        } else {
            hospitalList
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Hospital") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (isLoading) {
            CircularProgressIndicator()
        }
        else {
            LazyColumn(modifier = Modifier.fillMaxSize()){
                items(filteredHospitals.size) { index ->
                    HospitalCard(filteredHospitals[index])
                }
            }
        }
    }
}


@Composable
fun HospitalCard(hospital: Hospital) {
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
            .padding(12.dp))
        {
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {}
                ) {
                AsyncImage(
                    model = hospital.image,
                    contentDescription = "Hospital Profile",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 12.dp)
                )
                Column {
                    Text(text = hospital.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = hospital.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${hospital.city}, ${hospital.state}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row {
                        Text(
                            text = "⭐ ${hospital.ratings ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hospital.haveErDept) "🟢 Emergency Available" else "🔴 No Emergency",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Button(
                onClick = {
                    Toast.makeText(context, "Ambulance on the Way !", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            )
            {
                Text("Call")
            }
        }
    }

}


suspend fun fetchHospitalsFromSupabase(): List<Hospital> {
    return try {
        withContext(Dispatchers.IO) {
            SupabaseClient.client.postgrest
                .from("hospitals")
                .select()
                .decodeList<Hospital>()
                .map { hospital ->
                    hospital.copy(
                        ratings = hospital.ratings ?: 0.0f, // Default rating if null
                        haveErDept = hospital.haveErDept ?: false // Default if null
                    )
                }
        }
    } catch (e: Exception) {
        Log.e("Supabase Error", "Error fetching hospitals: ${e.message}")
        emptyList()
    }
}
