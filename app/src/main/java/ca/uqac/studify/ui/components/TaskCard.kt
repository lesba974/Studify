package ca.uqac.studify.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.studify.model.Task
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Place
@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF3D4566)), // Bordure
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E243D)) // Fond
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // LIGNE 1 : [ Icône + Titre ] ....... [ Badge ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = task.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = task.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Badge de catégorie (Travail / Santé)
                Surface(
                    color = if (task.category == "Travail") Color(0xFF4A4B6E) else Color(0xFF1B4D3E),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = task.category,
                        color = if (task.category == "Travail") Color.White else Color(0xFF4ADE80),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }
            }

            // LIGNE 2 : Description
            Text(
                text = task.description,
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 32.dp, top = 4.dp, bottom = 12.dp)
            )

            // LIGNE 3 : heure et lieux
            Row(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Bloc Heure
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.time,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Bloc Lieu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.location,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}