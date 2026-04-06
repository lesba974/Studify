package ca.uqac.studify.ui.screens.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.studify.data.model.Course
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamScreen(
    viewModel: AddExamViewModel,
    examId: Long?,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val courses by viewModel.courses.collectAsState()
    val availableWeeks by viewModel.availableWeeks
    val isLateWarning by viewModel.isLateWarning

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(examId) {
        if (examId != null && examId > 0) {
            viewModel.loadExam(examId)
        } else {
            viewModel.resetForm()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (examId == null || examId == 0L) "Nouvel examen" else "Modifier examen") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
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
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFFF3B30),
                    contentColor = Color.White
                )
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

            CourseDropdown(
                label = "Matière *",
                courses = courses,
                selectedCourse = viewModel.selectedCourse.value,
                onCourseSelected = { viewModel.selectedCourse.value = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            DatePickerField(
                label = "Date de l'examen",
                value = viewModel.examDate.value,
                onShowPicker = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLateWarning) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF3B30).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "Attention : Moins d'une semaine",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF3B30)
                            )
                            Text(
                                text = "Révision intensive recommandée",
                                fontSize = 12.sp,
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            TimePickerField(
                label = "Heure de l'examen",
                value = viewModel.examTime.value,
                onValueChange = { viewModel.examTime.value = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FormField(
                label = "Lieu",
                value = viewModel.location.value,
                onValueChange = { viewModel.location.value = it },
                placeholder = "Ex: UQAC - Gymnase"
            )

            Spacer(modifier = Modifier.height(20.dp))

            DurationField(
                label = "Durée de l'examen (minutes)",
                value = viewModel.examDuration.value,
                onValueChange = { viewModel.examDuration.value = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Plan de révision",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            WeekSelector(
                selectedWeeks = viewModel.revisionWeeks.value,
                onWeeksSelected = { viewModel.revisionWeeks.value = it },
                getRevisionCount = { viewModel.getRevisionCount(it) },
                availableWeeks = availableWeeks
            )

            Spacer(modifier = Modifier.height(24.dp))

            CalendarPreview(weeks = viewModel.revisionWeeks.value)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val success = viewModel.saveExam()
                        if (success) {
                            onNavigateBack()
                        } else {
                            snackbarHostState.showSnackbar(
                                "⚠️ Veuillez remplir tous les champs obligatoires"
                            )
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
                    text = "Créer l'examen et les révisions",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    if (showDatePicker) {
        val today = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = try {
                LocalDate.parse(viewModel.examDate.value)
                    .atStartOfDay(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            } catch (e: Exception) {
                today
            },
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= today
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                        viewModel.examDate.value = date.toString()
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler", color = Color.Gray)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    dayContentColor = Color.Black,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF6C63FF),
                    todayContentColor = Color(0xFF6C63FF),
                    todayDateBorderColor = Color(0xFF6C63FF),
                    titleContentColor = Color.Black,
                    headlineContentColor = Color.Black,
                    navigationContentColor = Color(0xFF6C63FF),
                    disabledDayContentColor = Color.Gray,
                    weekdayContentColor = Color.DarkGray
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDropdown(
    label: String,
    courses: List<Course>,
    selectedCourse: Course?,
    onCourseSelected: (Course) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedCourse?.name ?: "Sélectionner un cours",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4B39EF),
                    unfocusedBorderColor = Color(0xFF3D4566),
                    focusedContainerColor = Color(0xFF2A3150),
                    unfocusedContainerColor = Color(0xFF2A3150)
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF2A3150))
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = course.name,
                                color = Color.White
                            )
                        },
                        onClick = {
                            onCourseSelected(course)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WeekSelector(
    selectedWeeks: Int,
    onWeeksSelected: (Int) -> Unit,
    getRevisionCount: (Int) -> Int,
    availableWeeks: List<Int>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(1, 2, 3).forEach { weeks ->
            val isAvailable = availableWeeks.contains(weeks)
            WeekOption(
                weeks = weeks,
                sessions = getRevisionCount(weeks),
                isSelected = selectedWeeks == weeks,
                isEnabled = isAvailable,
                onClick = {
                    if (isAvailable) {
                        onWeeksSelected(weeks)
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WeekOption(
    weeks: Int,
    sessions: Int,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = when {
                    !isEnabled -> Color(0xFF1A1F35) // Désactivé
                    isSelected -> Color(0xFF6C63FF).copy(alpha = 0.1f)
                    else -> Color(0xFF2A3150)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = when {
                    !isEnabled -> Color(0xFF2A2F45) // Border grisé
                    isSelected -> Color(0xFF6C63FF)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weeks.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) Color(0xFF6C63FF) else Color(0xFF4A4F65)
            )
            Text(
                text = if (weeks == 1) "semaine" else "semaines",
                fontSize = 14.sp,
                color = if (isEnabled) Color.White else Color(0xFF6B7280),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = if (isEnabled) "$sessions sessions" else "Non disponible",
                fontSize = 12.sp,
                color = if (isEnabled) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CalendarPreview(weeks: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E243D)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📅 Calendrier de révision",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (weeks) {
                1 -> {
                    CalendarItem("J-7 à J-1", "3/jour")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚡ Révision intensive",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                2 -> {
                    CalendarItem("J-14 à J-8", "1/jour")
                    Spacer(modifier = Modifier.height(8.dp))
                    CalendarItem("J-7 à J-1", "2/jour")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚖️ Révision équilibrée",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                3 -> {
                    CalendarItem("J-21 à J-1", "1/jour")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🌿 Révision progressive",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CalendarItem(
                "📝 Total révisions",
                "21",
                isTotal = true
            )
        }
    }
}

@Composable
fun CalendarItem(
    text: String,
    count: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A3150), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )

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
fun DatePickerField(
    label: String,
    value: String,
    onShowPicker: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val displayDate = try {
            LocalDate.parse(value).format(formatter)
        } catch (e: Exception) {
            value
        }

        OutlinedTextField(
            value = displayDate,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onShowPicker),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A3150),
                unfocusedContainerColor = Color(0xFF2A3150),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFF3D4566),
                disabledTextColor = Color.White,
                disabledContainerColor = Color(0xFF2A3150),
                disabledBorderColor = Color(0xFF3D4566)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = false,
            trailingIcon = {
                IconButton(onClick = onShowPicker) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Choisir la date",
                        tint = Color.White
                    )
                }
            }
        )
    }
}

@Composable
fun TimePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

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
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A3150),
                unfocusedContainerColor = Color(0xFF2A3150),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFF3D4566),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White,
                disabledContainerColor = Color(0xFF2A3150),
                disabledBorderColor = Color(0xFF3D4566)
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text("09:00", color = Color(0xFF6B7280))
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Choisir l'heure",
                    tint = Color.White
                )
            },
            enabled = false
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val formatted = String.format("%02d:%02d", hour, minute)
                onValueChange(formatted)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        text = {
            TimePicker(
                state = timePickerState
            )
        }
    )
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

@Composable
fun DurationField(
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
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
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
                Text("180", color = Color(0xFF6B7280))
            },
            suffix = {
                Text("min", color = Color(0xFF9CA3AF), fontSize = 14.sp)
            }
        )

        val minutes = value.toIntOrNull() ?: 0
        if (minutes > 0) {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            val timeText = when {
                hours > 0 && remainingMinutes > 0 -> "${hours}h${remainingMinutes}min"
                hours > 0 -> "${hours}h"
                else -> "${minutes}min"
            }
            Text(
                text = "= $timeText",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}