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
    // 1. GESTION DE L'ÉTAT ET DES DONNÉES
    var selectedTab by remember { mutableStateOf("Aujourd'hui") }
    val allTasks = RoutineUtils.getMockRoutines()

    // 2. CONTENEUR PRINCIPAL
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B2244), Color(0xFF0F1221))
                )
            )
    ) {
        // --- COLONNE PRINCIPALE (Header + Liste)
        Column(modifier = Modifier.fillMaxSize()) {

            // A. LE HEADER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4B39EF),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 50.dp, bottom = 28.dp)
                ) {
                    Text(
                        text = "Studify",
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    )
                    DateDuJourText()
                }
            }

            // B. CONTENU SOUS LE HEADER (Filtres + Liste)
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                Spacer(modifier = Modifier.height(24.dp))

                // LES ONGLETS DE FILTRAGE
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterTab("Aujourd'hui", selectedTab == "Aujourd'hui") { selectedTab = "Aujourd'hui" }
                    FilterTab("Semaine", selectedTab == "Semaine") { selectedTab = "Semaine" }
                    FilterTab("Toutes", selectedTab == "Toutes") { selectedTab = "Toutes" }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // LA LISTE DES ROUTINES
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp) // Espace pour le bouton (+)
                ) {
                    items(allTasks) { currentTask ->
                        TaskCard(task = currentTask)
                    }
                }
            }
        } // Fin de la Column principale

        // 3. LE BOUTON (+) FLOTTANT (Enfant direct de la Box)
        FloatingActionButton(
            onClick = { /* Action ajouter */ },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Aligné en bas à droite de la BOX
                .padding(24.dp)
                .size(64.dp),
            containerColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun FilterTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) Color(0xFF353FD1) else Color(0xFF1E243D),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}