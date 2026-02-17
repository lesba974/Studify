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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.studify.model.Task
import ca.uqac.studify.ui.components.TaskCard
import ca.uqac.studify.data.RoutineUtils
import ca.uqac.studify.ui.components.DateDuJourText

@Composable
fun StudifyScreen() {
    val allTasks = RoutineUtils.getMockRoutines()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Action ajouter */ },
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
            // COLONNE PRINCIPALE (Header + Liste)
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
                    items(allTasks) { currentTask ->
                        TaskCard(task = currentTask)
                    }
                }
            }
        }
    }
}