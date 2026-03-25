package ca.uqac.studify.ui.screens.addEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.studify.ui.screens.detail.formatDateToFrench
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: AddEditTaskViewModel,
    taskId: Long? = null,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTask(taskId)
        } else {
            viewModel.resetForm()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (taskId == null) "Nouvelle routine" else "Modifier",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (taskId == null) "Créez une routine personnalisée" else "Détail de la routine",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4B39EF)
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
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B2244), Color(0xFF0F1221))
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {

                FormField(
                    label = "Nom de la routine*",
                    value = viewModel.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = "Ex: Cours de mathématiques"
                )

                Spacer(modifier = Modifier.height(20.dp))

                FormField(
                    label = "Description",
                    value = viewModel.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    placeholder = "Ajoutez des détails sur cette routine",
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Catégorie",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                var expandedCategory by remember { mutableStateOf(false) }
                val categories = listOf("Cours", "Révision", "Pause", "Sport")

                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = it }
                ) {
                    OutlinedTextField(
                        value = viewModel.category,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4B39EF),
                            unfocusedBorderColor = Color(0xFF3D4566),
                            focusedContainerColor = Color(0xFF2A3150),
                            unfocusedContainerColor = Color(0xFF2A3150)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false },
                        modifier = Modifier.background(Color(0xFF2A3150))
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = category,
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    viewModel.updateCategory(category)
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                var showDatePicker by remember { mutableStateOf(false) }

                Column {
                    Text(
                        text = "Date",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = formatDateToFrench(viewModel.date),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        placeholder = { Text("Sélectionner une date", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Choisir la date",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4B39EF),
                            unfocusedBorderColor = Color(0xFF3D4566),
                            focusedContainerColor = Color(0xFF2A3150),
                            unfocusedContainerColor = Color(0xFF2A3150)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismiss = { showDatePicker = false },
                        onConfirm = { selectedDateMillis ->
                            selectedDateMillis?.let {
                                val instant = java.time.Instant.ofEpochMilli(it)
                                val selectedDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                                viewModel.updateDate(selectedDate.toString())
                            }
                            showDatePicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                var showTimePicker by remember { mutableStateOf(false) }

                Column {
                    Text(
                        text = "Horaire de début",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.time,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true },
                        placeholder = { Text("Sélectionner une heure", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Choisir l'heure",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4B39EF),
                            unfocusedBorderColor = Color(0xFF3D4566),
                            focusedContainerColor = Color(0xFF2A3150),
                            unfocusedContainerColor = Color(0xFF2A3150)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (showTimePicker) {
                    TimePickerDialog(
                        onDismiss = { showTimePicker = false },
                        onConfirm = { hour, minute ->
                            val formatted = String.format("%02d:%02d", hour, minute)
                            viewModel.updateTime(formatted)
                            showTimePicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                var showEndTimePicker by remember { mutableStateOf(false) }

                Column {
                    Text(
                        text = "Horaire de fin",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.endTime,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndTimePicker = true },
                        placeholder = { Text("Sélectionner une heure", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { showEndTimePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Choisir l'heure de fin",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4B39EF),
                            unfocusedBorderColor = Color(0xFF3D4566),
                            focusedContainerColor = Color(0xFF2A3150),
                            unfocusedContainerColor = Color(0xFF2A3150)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (showEndTimePicker) {
                    TimePickerDialog(
                        onDismiss = { showEndTimePicker = false },
                        onConfirm = { hour, minute ->

                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val selectedTime = LocalTime.of(hour, minute)

                            if (viewModel.time.isNotBlank()) {
                                val startTime = LocalTime.parse(viewModel.time, formatter)

                                if (selectedTime.isBefore(startTime)) {

                                    showEndTimePicker = false

                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "⛔ L'heure de fin doit être après l'heure de début"
                                        )
                                    }

                                    return@TimePickerDialog
                                }
                            }

                            viewModel.updateEndTime(selectedTime.format(formatter))
                            showEndTimePicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                FormField(
                    label = "Localisation",
                    value = viewModel.location,
                    onValueChange = { viewModel.updateLocation(it) },
                    placeholder = "Ex: UQAC, À la maison"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Périodicité",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                var expandedPeriodicity by remember { mutableStateOf(false) }
                val periodicities = listOf("Une fois", "Quotidien", "Hebdomadaire", "Mensuel")

                ExposedDropdownMenuBox(
                    expanded = expandedPeriodicity,
                    onExpandedChange = { expandedPeriodicity = it }
                ) {
                    OutlinedTextField(
                        value = viewModel.periodicity,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeriodicity)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4B39EF),
                            unfocusedBorderColor = Color(0xFF3D4566),
                            focusedContainerColor = Color(0xFF2A3150),
                            unfocusedContainerColor = Color(0xFF2A3150)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedPeriodicity,
                        onDismissRequest = { expandedPeriodicity = false },
                        modifier = Modifier.background(Color(0xFF2A3150))
                    ) {
                        periodicities.forEach { periodicity ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = periodicity,
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    viewModel.updatePeriodicity(periodicity)
                                    expandedPeriodicity = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Priorité",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PriorityButton(
                        label = "Faible",
                        selected = viewModel.priority == "Faible",
                        onClick = { viewModel.updatePriority("Faible") },
                        modifier = Modifier.weight(1f)
                    )
                    PriorityButton(
                        label = "Moyenne",
                        selected = viewModel.priority == "Moyenne",
                        onClick = { viewModel.updatePriority("Moyenne") },
                        modifier = Modifier.weight(1f)
                    )
                    PriorityButton(
                        label = "Élevée",
                        selected = viewModel.priority == "Élevée",
                        onClick = { viewModel.updatePriority("Élevée") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (viewModel.title.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "⚠️ Le nom de la routine est obligatoire",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            viewModel.saveTask {
                                onNavigateBack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C63FF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (taskId == null) "Créer la routine" else "Enregistrer les modifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF4B39EF),
                unfocusedBorderColor = Color(0xFF3D4566),
                focusedContainerColor = Color(0xFF2A3150),
                unfocusedContainerColor = Color(0xFF2A3150),
                cursorColor = Color(0xFF6C63FF)
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines
        )
    }
}

@Composable
fun PriorityButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF6C63FF) else Color(0xFF2A3150),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit
) {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = today,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(datePickerState.selectedDateMillis)
            }) {
                Text("OK", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().get(Calendar.MINUTE),
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