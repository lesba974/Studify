package ca.uqac.studify.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ca.uqac.studify.data.model.Task
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Place
import ca.uqac.studify.data.model.getIcon
import androidx.compose.foundation.shape.CircleShape
import ca.uqac.studify.ui.screens.detail.formatDateToFrench

@Composable
fun TaskCard(task: Task, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF3D4566)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E243D))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .background(
                                color = when (task.priority) {
                                    "Élevée" -> Color(0xFFDC2626)
                                    "Moyenne" -> Color(0xFFF59E0B)
                                    "Faible" -> Color(0xFF10B981)
                                    else -> Color.Transparent
                                },
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = task.getIcon(),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = task.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = when (task.category) {
                        "Révision" -> Color(0xFF4A4B6E)
                        "Cours" -> Color(0xFF6E4A4A)
                        "Sport" -> Color(0xFF1B4D3E)
                        else -> Color(0xFF4D3A1B)
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = task.category,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = task.description,
                color = Color.LightGray,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 42.dp, top = 8.dp, bottom = 14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        if (!task.date.isNullOrBlank()) {
                            Text(
                                text = formatDateToFrench(task.date),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = task.time,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.location,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}