package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.OneOnOneBooking
import com.example.ui.EduViewModel
import com.example.ui.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val role by viewModel.currentUserRole.collectAsState()
    val bookings by viewModel.premiumBookings.collectAsState()

    var showBookingForm by remember { mutableStateOf(false) }

    // Booking parameters
    var subjectInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("May 26, 2026") }
    var timeInput by remember { mutableStateOf("4:30 PM") }
    var notesInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Personalized Premium Tutoring",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Private 1-on-1 scheduled sessions for targeted instruction",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (role == UserRole.STUDENT) {
                Button(
                    onClick = { showBookingForm = !showBookingForm },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(
                        if (showBookingForm) Icons.Default.Close else Icons.Default.Event,
                        contentDescription = "Trigger form",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (showBookingForm) "Close Form" else "Schedule Lesson", color = Color.White)
                }
            }
        }

        // Layout Split: Left (Form / Info Panel), Right (Scheduled bookings)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Half: Booking Info / Form
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showBookingForm && role == UserRole.STUDENT) {
                    // SCHEDULING FORM
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "Book 1-on-1 Class",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )

                            OutlinedTextField(
                                value = subjectInput,
                                onValueChange = { subjectInput = it },
                                label = { Text("Topic/Syllabus Subject") },
                                placeholder = { Text("E.g. Advanced Fluid Dynamics problems") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = "book") },
                                singleLine = true
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = dateInput,
                                    onValueChange = { dateInput = it },
                                    label = { Text("Preferred Date") },
                                    modifier = Modifier.weight(1f),
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "date") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = timeInput,
                                    onValueChange = { timeInput = it },
                                    label = { Text("Preferred Hour") },
                                    modifier = Modifier.weight(1f),
                                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = "time") },
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("Tutor instructions / requirements") },
                                placeholder = { Text("Ask for specific worksheet reviews, focus areas...") },
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                maxLines = 4
                            )

                            Button(
                                onClick = {
                                    if (subjectInput.isNotBlank()) {
                                        viewModel.bookPremiumSession(
                                            subject = subjectInput,
                                            date = dateInput,
                                            time = timeInput,
                                            notes = notesInput
                                        )
                                        // Clear states
                                        subjectInput = ""
                                        notesInput = ""
                                        showBookingForm = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = subjectInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "book now button icon", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Confirm & Reserve Slot", color = Color.White)
                            }
                        }
                    }
                } else {
                    // Subscriptions / Premium tutoring benefits informational board
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.WorkspacePremium,
                                    contentDescription = "Badge",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "Personal Tutoring Benefits",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Unlock ultimate dedicated attention. Elite certified teachers are paired exclusively with you to tackle difficult college material, exams worksheets, and customized study programs.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val benefits = listOf(
                                Icons.Default.SupportAgent to "Dedicated instant student help line",
                                Icons.Default.BorderColor to "Interactive whiteboard workspace with real slides",
                                Icons.Default.SettingsVoice to "High fidelity low latency live microphone & cams",
                                Icons.Default.Analytics to "Personal score logs trends tracker profile"
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                benefits.forEach { (icon, text) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            icon,
                                            contentDescription = "check",
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Half: Booked Appointments / Scheduled events lists
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Your Private Lesson List",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (bookings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.EventNote, contentDescription = "calendar logo", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No premium tutoring booked yet.", color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(bookings) { booking ->
                            val isCompleted = booking.status == "COMPLETED"
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = if (isCompleted) {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                } else {
                                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f))
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = if (isCompleted) Color(0xFFE2E2E6) else MaterialTheme.colorScheme.tertiaryContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    if (isCompleted) Icons.Default.Check else Icons.Default.Timelapse,
                                                    contentDescription = "status symbol icon",
                                                    modifier = Modifier.size(10.dp),
                                                    tint = if (isCompleted) Color.Gray else MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = booking.status,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isCompleted) Color.Gray else MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                        }

                                        Text(
                                            "${booking.date} at ${booking.time}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        booking.subject,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                        Icon(Icons.Default.School, contentDescription = "Tutor", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Tutor: ${booking.teacherName} • Student: ${booking.studentName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }

                                    if (booking.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider()
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Notes to Tutor:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(
                                            booking.notes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                    }

                                    if (role == UserRole.TEACHER && !isCompleted) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { viewModel.completeBooking(booking) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                            shape = RoundedCornerShape(50.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "complete button", tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Mark Lesson Completed", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
