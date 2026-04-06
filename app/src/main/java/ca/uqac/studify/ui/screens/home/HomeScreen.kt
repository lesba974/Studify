package ca.uqac.studify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.studify.ui.components.TaskCard
import ca.uqac.studify.ui.screens.home.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToAddExam: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val showArchived by viewModel.showArchived.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter une routine",
                    tint = Color(0xFF4B39EF),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1221))
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF4B39EF),
                        RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            ) {
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 20.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    offset = DpOffset(x = (-16).dp, y = (-120).dp),
                    modifier = Modifier
                        .background(Color(0xFF1E243D))
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📚", fontSize = 20.sp)
                                Text(
                                    "Emploi du temps",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onNavigateToSchedule()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📝", fontSize = 20.sp)
                                Text(
                                    "Ajouter un examen",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onNavigateToAddExam()
                        }
                    )
                }

                Column(
                    modifier = Modifier.padding(
                        start = 28.dp,
                        end = 28.dp,
                        top = 48.dp,
                        bottom = 36.dp
                    )
                ) {
                    Text(
                        text = "Studify",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = getCurrentDate(),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        TabButton(
                            text = "Actives",
                            isSelected = !showArchived,
                            onClick = { viewModel.setShowArchived(false) }
                        )
                        TabButton(
                            text = "Archivées",
                            isSelected = showArchived,
                            onClick = { viewModel.setShowArchived(true) }
                        )
                    }
                }
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showArchived) "Aucune routine archivée" else "Aucune routine active",
                        color = Color(0xFF6B7280),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(60.dp)
                    .background(Color.White)
            )
        }
    }
}

private fun getCurrentDate(): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH)
    return LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() }
}