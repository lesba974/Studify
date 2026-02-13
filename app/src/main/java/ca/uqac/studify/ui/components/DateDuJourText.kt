package ca.uqac.studify.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateDuJourText() {
    val dateFormatee = remember {
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH))
            .replaceFirstChar { it.uppercase() }
    }

    Text(
        text = dateFormatee,
        color = Color(0xFFFFFFFF),
        fontSize = 14.sp
    )
}