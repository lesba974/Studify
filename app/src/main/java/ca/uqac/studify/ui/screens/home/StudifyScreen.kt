package ca.uqac.studify.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.uqac.studify.ui.components.TaskCard
import ca.uqac.studify.ui.components.DateDuJourText

@Composable
fun StudifyScreen(
    viewModel: HomeViewModel,
    onAddTaskClick: () -> Unit = {},
    onTaskClick: (Long) -> Unit = {}
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter une routine",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        containerColor = Color.Transparent
    ) { contentPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B2244), Color(0xFF0F1221))
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4B39EF),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 28.dp)
                            .padding(top = 60.dp, bottom = 36.dp)
                    ) {
                        Text(
                            text = "Studify",
                            color = Color.White,
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                        DateDuJourText()
                    }
                }

                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📋",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aucune routine",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Appuyez sur + pour créer votre première routine",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 28.dp,
                            bottom = 100.dp
                        )
                    ) {
                        items(
                            items = tasks,
                            key = { task -> task.id }
                        ) { currentTask ->
                            TaskCard(
                                task = currentTask,
                                onClick = { onTaskClick(currentTask.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}