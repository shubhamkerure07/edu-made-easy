package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.LiveSession
import com.example.ui.EduViewModel
import com.example.ui.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: EduViewModel
) {
    val activeSession by viewModel.activeLiveSession.collectAsState()
    val role by viewModel.currentUserRole.collectAsState()
    val userName by viewModel.currentUserName.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Sessions, 1: Assignments, 2: Assessments, 3: Premium 1-on-1

    // If there is an active session, override the full container and show the high-fidelity live stream!
    if (activeSession != null) {
        LiveClassroomScreen(viewModel = viewModel)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = "Academy emblem", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                "EduClass",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Live Tuition Network",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                actions = {
                    // Profile selector styled exactly like HTML: w-10 h-10 rounded-full bg-[#E7E0EC] flex items-center border border-[#79747E]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(height = 42.dp, width = 142.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable {
                                    val newRole = if (role == UserRole.STUDENT) UserRole.TEACHER else UserRole.STUDENT
                                    viewModel.setRole(newRole)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (role == UserRole.TEACHER) Icons.Default.SupervisorAccount else Icons.Default.Person,
                                    contentDescription = "Active user avatar logo",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    userName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    if (role == UserRole.TEACHER) "Teacher View" else "Student View",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = "Toggle Mode",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3EDF7),
                modifier = Modifier.border(
                    width = 0.5.dp,
                    color = Color(0xFFD0BCFF),
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                )
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.VideoCall, contentDescription = "live sessions button") },
                    label = { Text("Live Classes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D192B),
                        selectedTextColor = Color(0xFF1D192B),
                        indicatorColor = Color(0xFFE8DEF8),
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "homework buttons") },
                    label = { Text("Assignments") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D192B),
                        selectedTextColor = Color(0xFF1D192B),
                        indicatorColor = Color(0xFFE8DEF8),
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "quizzes button") },
                    label = { Text("Assessments") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D192B),
                        selectedTextColor = Color(0xFF1D192B),
                        indicatorColor = Color(0xFFE8DEF8),
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "premium tab buttons icon") },
                    label = { Text("Premium 1-1") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D192B),
                        selectedTextColor = Color(0xFF1D192B),
                        indicatorColor = Color(0xFFE8DEF8),
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
            }
        }
    ) { innerPadding ->
        when (activeTab) {
            0 -> LiveSessionsTab(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
                onNavigateToPremium = { activeTab = 3 }
            )
            1 -> AssignmentScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> QuizScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> PremiumScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// LIVE SESSIONS TAB PANEL
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSessionsTab(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier,
    onNavigateToPremium: () -> Unit = {}
) {
    val role by viewModel.currentUserRole.collectAsState()
    val sessions by viewModel.liveSessions.collectAsState()
    var showCreateLiveDialog by remember { mutableStateOf(false) }

    // Interactively Filter Selection States
    var selectedGrade by remember { mutableStateOf("All Grades") }
    var selectedSubjectFilter by remember { mutableStateOf("All Subjects") }

    val gradeLevels = listOf("All Grades", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Grade 10")
    val subjectFilters = listOf("All Subjects", "Mathematics", "Science", "English", "Social Studies", "Art", "Hindi", "EVS", "Physics", "Chemistry")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Welcomer Greeting Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome to EduClass Live",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (role == UserRole.TEACHER) "Host and coordinate your video lectures" else "Connect with tutor coaches and peer groups live",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (role == UserRole.TEACHER) {
                Button(
                    onClick = { showCreateLiveDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Host Live Class", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Host Lecture")
                }
            }
        }

        // Interactive Class Grade Filter Chips Scroll Row
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(gradeLevels) { grade ->
                val isSelected = selectedGrade == grade
                Surface(
                    modifier = Modifier.clickable { selectedGrade = grade },
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = grade,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Interactive Subject Filter Chips Scroll Row
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(subjectFilters) { sub ->
                val isSelected = selectedSubjectFilter == sub
                Surface(
                    modifier = Modifier.clickable { selectedSubjectFilter = sub },
                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        val icon = when (sub) {
                            "Mathematics" -> "🔢"
                            "Science" -> "🧪"
                            "English" -> "📖"
                            "Social Studies" -> "🌍"
                            "Art" -> "🎨"
                            "Hindi" -> "🅰️"
                            "EVS" -> "🍃"
                            "Physics" -> "⚛️"
                            "Chemistry" -> "🧪"
                            else -> "✏️"
                        }
                        if (sub != "All Subjects") {
                            Text(icon, style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Classroom split lists
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Half: Live stream broadcasts lists
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Classrooms Active Now",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val activeStreams = sessions.filter { 
                    it.isLive && 
                    (selectedGrade == "All Grades" || it.gradeLevel.contains(selectedGrade, ignoreCase = true) || selectedGrade.contains(it.gradeLevel, ignoreCase = true)) && 
                    (selectedSubjectFilter == "All Subjects" || it.subject.equals(selectedSubjectFilter, ignoreCase = true) || it.subject.contains(selectedSubjectFilter, ignoreCase = true))
                }
                if (activeStreams.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No classrooms are currently streaming.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(activeStreams) { session ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28.dp)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    // Decorative Video Camera overlay, matching absolute span bottom-right of HTML
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                        modifier = Modifier
                                            .size(110.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(x = 16.dp, y = 16.dp)
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Live Indicator
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            // Pulsing active red dot
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFB3261E))
                                            )
                                            Text(
                                                text = if (session.isPremium) "LIVE - 1-ON-1 PREMIUM" else "LIVE NOW",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    letterSpacing = 1.2.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }

                                        // Subject & Title header
                                        Text(
                                            text = session.title,
                                            style = MaterialTheme.typography.titleLarge.copy(lineHeight = 26.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )

                                        Text(
                                            text = "Tutor: ${session.teacherName} • Subject: ${session.subject}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )

                                        // Join Session elegant button
                                        Button(
                                            onClick = { viewModel.selectLiveSession(session) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(50.dp),
                                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (role == UserRole.TEACHER) Icons.Default.Launch else Icons.Default.PlayCircleFilled,
                                                contentDescription = "Join cursor icon",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (role == UserRole.TEACHER) "Enter Classroom" else "Join Session",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Split sections: Upcoming/scheduled lists
                Text(
                    "Upcoming Lectures",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )

                val scheduledClasses = sessions.filter { 
                    !it.isLive && 
                    (selectedGrade == "All Grades" || it.gradeLevel.contains(selectedGrade, ignoreCase = true) || selectedGrade.contains(it.gradeLevel, ignoreCase = true)) && 
                    (selectedSubjectFilter == "All Subjects" || it.subject.equals(selectedSubjectFilter, ignoreCase = true) || it.subject.contains(selectedSubjectFilter, ignoreCase = true))
                }
                if (scheduledClasses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No lectures scheduled currently.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        items(scheduledClasses) { scheduled ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${scheduled.subject} • ${scheduled.gradeLevel}",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            scheduled.dateTime,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        scheduled.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "Tutor: ${scheduled.teacherName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        scheduled.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        lineHeight = 16.sp,
                                        maxLines = 2
                                    )

                                    if (role == UserRole.TEACHER) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Divider()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    // Stream upcoming class instantly
                                                    viewModel.updateLiveSession(scheduled.copy(isLive = true, dateTime = "LIVE NOW"))
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Videocam, "Stream", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Start Streaming Now")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Half: Student quick summaries stats panel
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Tuition Center Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Render dynamic statistics depending on the active mode (Student or Teacher)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = "Emblem", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your Enrollment Hub",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        if (role == UserRole.STUDENT) {
                            val stats = listOf(
                                "Enrolled syllabus" to "Grade 10 Honors",
                                "Scheduled classes" to "${sessions.size}",
                                "Completed homeworks" to "1 Done",
                                "Active Premium Tutor" to "Prof. Marcus Vance"
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                stats.forEach { (label, value) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            val stats = listOf(
                                "Assigned Subjects" to "Maths • Physics • Chemistry",
                                "Total active pupils" to "142 Subscribed",
                                "Awaiting evaluation" to "0 pending homeworks",
                                "1-on-1 private classes" to "1 Scheduled today"
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                stats.forEach { (label, value) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // --- WEEKLY REPORT OF ACTIVE LEARNING HOURS AND CLASSES HELD ---
                if (role == UserRole.STUDENT) {
                    val studentMetrics by viewModel.allStudentMetrics.collectAsState()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp, 
                                            contentDescription = "Stats Icon", 
                                            tint = MaterialTheme.colorScheme.tertiary, 
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Weekly Activity Report",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "Class 1-5 Core",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            val totalSeconds = studentMetrics.sumOf { it.activeSeconds }
                            val activeHoursStr = String.format("%.2f hrs", totalSeconds / 3600f)
                            val totalQuits = studentMetrics.sumOf { it.quitCount }
                            val classesHeldOrAttended = studentMetrics.size

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = activeHoursStr,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Focus Active Time",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$classesHeldOrAttended",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "Classes Held",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$totalQuits",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Class Exits",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "Study Minutes By Weekday",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
                            val maxTargetSeconds = 7200L // 2 Hours max bar target
                            
                            days.forEach { dayName ->
                                val daySeconds = studentMetrics.filter { it.dateString.equals(dayName, ignoreCase = true) }.sumOf { it.activeSeconds }
                                val fraction = (daySeconds.toFloat() / maxTargetSeconds).coerceIn(0f, 1f)
                                val minutesStr = "${daySeconds / 60}m"
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dayName.take(3),
                                        modifier = Modifier.width(32.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Gray
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fraction)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                        colors = listOf(
                                                            MaterialTheme.colorScheme.primary,
                                                            MaterialTheme.colorScheme.tertiary
                                                        )
                                                    )
                                                )
                                        )
                                    }
                                    
                                    Text(
                                        text = minutesStr,
                                        modifier = Modifier.width(44.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Go Premium Banner (exactly matching the style / instructions of the design theme)
                if (role == UserRole.STUDENT) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPremium() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        border = BorderStroke(1.dp, Color(0xFFF9DEDC)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Go Premium",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "1-on-1 Personalized Tutoring",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Button(
                                onClick = { onNavigateToPremium() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                shape = RoundedCornerShape(50.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "Book Now",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Classroom guidelines
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Network Instructions",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "1. Keep mic muted on entry to live video lectures.\n" +
                            "2. Complete assignments in the homework console by due dates.\n" +
                            "3. Request help during scheduled 1-on-1 gold-tinted sessions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }

    // CREATE NEW LIVE LECTURE POPUP DIALOG FOR TEACHER
    if (showCreateLiveDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newSubject by remember { mutableStateOf("Mathematics") }
        var newDesc by remember { mutableStateOf("") }
        var newTime by remember { mutableStateOf("LIVE NOW") }
        var isPremiumClass by remember { mutableStateOf(false) }
        var newGradeLevel by remember { mutableStateOf("Class 1") }

        AlertDialog(
            onDismissRequest = { showCreateLiveDialog = false },
            title = { Text("Host Live Lecture Session") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚡ Quick-Host Presets (1-Click Fill)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    ) {
                        val presets = listOf(
                            Triple("Fun Addition & Numbers", "Counting primary objects and single-digit arithmetic practice with dynamic shapes.", "Class 1" to "Mathematics"),
                            Triple("Alphabet Storytelling", "Learning pronunciation rules, spelling grids, and reading classic animal fables.", "Class 2" to "English"),
                            Triple("Leaf and Plant Anatomy", "Visual breakdown of root systems, vascular stems, and chlorophyll sunshine photosynthesis.", "Class 3" to "Science"),
                            Triple("Community helpers", "Meeting police responders, fire crews, nurse helpers, and local social volunteers.", "Class 4" to "Social Studies"),
                            Triple("Sunset watercolors paint", "Applying gradients, warm-cold shading steps, and blending sunset sky horizons.", "Class 5" to "Art")
                        )
                        items(presets) { (title, desc, pair) ->
                            val (grade, subject) = pair
                            Surface(
                                modifier = Modifier.clickable {
                                    newTitle = title
                                    newDesc = desc
                                    newGradeLevel = grade
                                    newSubject = subject
                                    newTime = "LIVE NOW"
                                },
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(text = "Primary $grade", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(text = title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Lecture Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    var expandedGrade by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = newGradeLevel,
                            onValueChange = {},
                            label = { Text("Target Grade Level") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expandedGrade = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "dropdown")
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedGrade,
                            onDismissRequest = { expandedGrade = false }
                        ) {
                            val grades = listOf("All Classes", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Grade 10")
                            grades.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g) },
                                    onClick = { newGradeLevel = g; expandedGrade = false }
                                )
                            }
                        }
                    }

                    var expandedSubject by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = newSubject,
                            onValueChange = {},
                            label = { Text("Syllabus Subject") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expandedSubject = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "dropdown")
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedSubject,
                            onDismissRequest = { expandedSubject = false }
                        ) {
                            val subjects = listOf("Mathematics", "English", "Science", "Social Studies", "Art", "Hindi", "Environmental Studies (EVS)", "Physics", "Chemistry")
                            subjects.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = { newSubject = s; expandedSubject = false }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Lecture Agenda description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("Lecture scheduled datetime") },
                        placeholder = { Text("E.g. LIVE NOW or Tomorrow, 5:00 PM") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Personalized 1-on-1 Premium session", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isPremiumClass,
                            onCheckedChange = { isPremiumClass = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.tertiary)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addLiveSession(
                                title = newTitle,
                                subject = newSubject,
                                dateTime = newTime,
                                isPremium = isPremiumClass,
                                description = newDesc,
                                gradeLevel = newGradeLevel
                            )
                            showCreateLiveDialog = false
                        }
                    },
                    enabled = newTitle.isNotBlank()
                ) {
                    Text("Reserve & Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateLiveDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}
