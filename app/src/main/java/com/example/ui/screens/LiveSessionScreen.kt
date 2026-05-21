package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.LiveSession
import com.example.ui.EduViewModel
import com.example.ui.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LiveClassroomScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeLiveSession.collectAsState()
    val chatMessages by viewModel.activeLiveChatMessages.collectAsState()
    val role by viewModel.currentUserRole.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var chatInput by remember { mutableStateOf("") }
    var slideNumber by remember { mutableIntStateOf(1) }
    var isMuted by remember { mutableStateOf(false) }
    var isCameraOn by remember { mutableStateOf(true) }
    var handRaised by remember { mutableStateOf(false) }

    // Scroll chat to end on message receive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val currentSession = activeSession
    if (currentSession == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No active live session. Join inside the sessions tab.")
        }
        return
    }

    // Launch student live session activity duration tracking
    LaunchedEffect(currentSession.id, role) {
        if (role == UserRole.STUDENT) {
            viewModel.startTrackingSession(currentSession.id, currentSession.title, currentSession.subject)
        }
    }
    DisposableEffect(currentSession.id, role) {
        onDispose {
            if (role == UserRole.STUDENT) {
                viewModel.stopTrackingSession(currentSession.id, currentSession.title, currentSession.subject, isQuit = true)
            }
        }
    }

    // List of mock presentation slides based on the subject
    val slides = remember(currentSession.subject) {
        if (currentSession.subject.contains("Math", ignoreCase = true)) {
            listOf(
                "Quadratic Equation basics: ax² + bx + c = 0",
                "Derivation of the Quadratic Formula from ax² + bx + c = 0 State",
                "Solving by Factorisation: x² - 5x + 6 = (x-2)(x-3) = 0",
                "Solving by Completing the Square coordinates details",
                "Practice exercise: Solve 2x² - 7x + 3 = 0"
            )
        } else if (currentSession.subject.contains("Physic", ignoreCase = true)) {
            listOf(
                "Newton's First Law: Inertia (State of rest or uniform motion)",
                "Newton's Second Law: Force = Mass x Acceleration (F = ma)",
                "Newton's Third Law: Action = Reaction (Opposite force vectors)",
                "Friction & Normal forces on inclined plane parameters",
                "Practical Example: Tension in elevator cables"
            )
        } else {
            listOf(
                "Organic Chemistry: Carbon structure & covalent bonds",
                "Alkanes general formula (CnH2n+2) vs Alkenes (CnH2n)",
                "IUPAC nomenclature naming rules for organic elements",
                "Saturated hydrocarbons structural isomerism properties",
                "Group Activity: Name the structural forms of Pentane"
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.selectLiveSession(null) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Exit Room")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = currentSession.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (currentSession.isPremium) MaterialTheme.colorScheme.tertiary else Color(0xFFEF5350),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (currentSession.isPremium) "1-ON-1 PREMIUM" else "LIVE CLASS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.People, 
                            contentDescription = "Students count", 
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${if (currentSession.isPremium) 1 else 12} watching",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        if (role == UserRole.STUDENT) {
                            val activeSeconds by viewModel.currentSessionActiveSeconds.collectAsState()
                            val quitCount by viewModel.currentSessionQuitCount.collectAsState()
                            val minutes = activeSeconds / 60
                            val seconds = activeSeconds % 60
                            val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "⏱️ Focus: $timeFormatted (Exits: $quitCount)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.selectLiveSession(null) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Leave", color = Color.White)
            }
        }

        // Classroom split view: Video feed + Whiteboard Presentation (or split vertical)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .background(Color.Black)
        ) {
            // Simulated Screen Canvas (Whiteboard / Video Stream)
            Column(modifier = Modifier.fillMaxSize()) {
                // Interactive Presentation Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "Whiteboard",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "BOARD PRESENTATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = slides[slideNumber - 1],
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Slide $slideNumber of ${slides.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    // Slide controls (Visible to teacher directly, or simulated for Student)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (slideNumber > 1) slideNumber-- },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Slide", tint = Color.White)
                        }
                        IconButton(
                            onClick = { if (slideNumber < slides.size) slideNumber++ },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Slide", tint = Color.White)
                        }
                    }
                }

                // In Classroom Webcam Mini Strip (Simulates student webcams + teacher webcam)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Teacher Webcam Info Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF334155))
                            .border(1.dp, Color(0xFF475569), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    currentSession.teacherName.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    currentSession.teacherName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Host • Webcam Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = Color.Green
                                )
                            }
                        }
                    }

                    // Student Personal Camera View (Self Video Simulation)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCameraOn) Color(0xFF1E293B) else Color(0xFF0F172A))
                            .border(
                                1.dp, 
                                if (handRaised) MaterialTheme.colorScheme.tertiary else Color(0xFF475569), 
                                RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCameraOn) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "You (${if (role == UserRole.STUDENT) "Student" else "Teacher"})",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    if (handRaised) "✋ Hand Raised" else "Webcam Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = if (handRaised) MaterialTheme.colorScheme.tertiary else Color.LightGray
                                )
                            }
                        } else {
                            Icon(Icons.Default.VideocamOff, contentDescription = "Camera Off", tint = Color.Gray)
                        }
                    }
                }
            }
        }

        // Live Chat Feed
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Chat header / info bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Live Classroom Chat",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                // Simulate another student chat question dynamically
                                delay(300)
                                viewModel.sendChatMessage("Is there any homework assignment scheduled after the lesson?")
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Simulate classmate chat", modifier = Modifier.size(16.dp))
                    }
                }
                Divider()

                // Messages Scroll List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { message ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = message.senderName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (message.isTeacher) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                                if (message.isTeacher) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                        modifier = Modifier.padding(1.dp).border(0.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "TEACHER",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 8.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            Text(
                                text = message.messageText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (message.isTeacher) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Divider()

                // Keyboard / Message Typing strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick classroom controls
                    IconButton(onClick = { isMuted = !isMuted }) {
                        Icon(
                            if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute mic",
                            tint = if (isMuted) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { isCameraOn = !isCameraOn }) {
                        Icon(
                            if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Toggle webcam",
                            tint = if (isCameraOn) MaterialTheme.colorScheme.primary else Color.Red
                        )
                    }

                    if (role == UserRole.STUDENT) {
                        IconButton(onClick = { handRaised = !handRaised }) {
                            Icon(
                                Icons.Default.PanTool,
                                contentDescription = "Raise Hand",
                                tint = if (handRaised) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Ask a question...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (chatInput.isNotBlank()) {
                                    viewModel.sendChatMessage(chatInput)
                                    chatInput = ""
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                            }
                        }
                    )
                }
            }
        }
    }
}
