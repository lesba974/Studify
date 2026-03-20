package ca.uqac.studify.ui.screens.addEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                            imageVector = Icons.Default.ArrowBack,
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
                            .menuAnchor(),
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

                FormField(
                    label = "Horaire de début",
                    value = viewModel.time,
                    onValueChange = { viewModel.updateTime(it) },
                    placeholder = "08:00 AM"
                )

                Spacer(modifier = Modifier.height(20.dp))

                FormField(
                    label = "Horaire de fin",
                    value = viewModel.endTime,
                    onValueChange = { viewModel.updateEndTime(it) },
                    placeholder = "09:00 AM"
                )

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
                            .menuAnchor(),
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
                        viewModel.saveTask {
                            onNavigateBack()
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