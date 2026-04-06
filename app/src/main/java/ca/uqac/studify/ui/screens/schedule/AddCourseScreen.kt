package ca.uqac.studify.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(
    viewModel: AddCourseViewModel,
    courseId: Long?,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(courseId) {
        if (courseId != null && courseId > 0) {
            viewModel.loadCourse(courseId)
        } else {
            viewModel.resetForm()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (courseId == null || courseId == 0L) "Nouveau cours" else "Modifier cours") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4B39EF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1221))
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            FormField(
                label = "Nom du cours *",
                value = viewModel.name.value,
                onValueChange = { viewModel.name.value = it },
                placeholder = "Ex: Informatique mobile"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Jour de la semaine",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DaySelector(
                selectedDay = viewModel.selectedDay.value,
                onDaySelected = { viewModel.selectedDay.value = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            TimeField(
                label = "Heure de début",
                value = viewModel.startTime.value,
                onValueChange = { viewModel.startTime.value = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            TimeField(
                label = "Heure de fin",
                value = viewModel.endTime.value,
                onValueChange = { viewModel.endTime.value = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FormField(
                label = "Salle / Lieu",
                value = viewModel.location.value,
                onValueChange = { viewModel.location.value = it },
                placeholder = "Ex: UQAC - Salle P1-5020"
            )

            Spacer(modifier = Modifier.height(20.dp))

            TimeField(
                label = "Heure de révision",
                value = viewModel.revisionTime.value,
                onValueChange = { viewModel.revisionTime.value = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PreviewSection()

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val success = viewModel.saveCourse()
                        if (success) {
                            onNavigateBack()
                        } else {
                            snackbarMessage = "Le nom du cours est obligatoire"
                            showSnackbar = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (courseId == null || courseId == 0L) "Créer le cours" else "Modifier le cours",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DaySelector(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val days = listOf(
        1 to "Lun",
        2 to "Mar",
        3 to "Mer",
        4 to "Jeu",
        5 to "Ven",
        6 to "Sam"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { (dayNum, dayName) ->
            DayButton(
                day = dayName,
                isSelected = selectedDay == dayNum,
                onClick = { onDaySelected(dayNum) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DayButton(
    day: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color(0xFF6C63FF) else Color(0xFF2A3150),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF6C63FF) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun TimeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A3150),
                unfocusedContainerColor = Color(0xFF2A3150),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFF3D4566),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text("08:00", color = Color(0xFF6B7280))
            }
        )
    }
}

@Composable
fun PreviewSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E243D)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🎯 Routines générées",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PreviewItem(
                emoji = "📚",
                text = "Cours",
                count = "1"
            )

            Spacer(modifier = Modifier.height(8.dp))

            PreviewItem(
                emoji = "📖",
                text = "Révision",
                count = "1"
            )
        }
    }
}

@Composable
fun PreviewItem(
    emoji: String,
    text: String,
    count: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A3150), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .background(Color(0xFF6C63FF), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = count,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A3150),
                unfocusedContainerColor = Color(0xFF2A3150),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFF3D4566),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text(placeholder, color = Color(0xFF6B7280))
            }
        )
    }
}