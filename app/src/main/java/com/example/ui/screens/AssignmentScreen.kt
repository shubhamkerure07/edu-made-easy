package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.Assignment
import com.example.database.Submission
import com.example.ui.EduViewModel
import com.example.ui.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val role by viewModel.currentUserRole.collectAsState()
    val assignments by viewModel.assignments.collectAsState()
    val submissions by viewModel.submissions.collectAsState()

    var showPublishDialog by remember { mutableStateOf(false) }
    var selectedAssignment by remember { mutableStateOf<Assignment?>(null) }
    var submissionText by remember { mutableStateOf("") }

    // Teacher selection states
    var selectedSubmissionForGrading by remember { mutableStateOf<Submission?>(null) }
    var gradeInput by remember { mutableStateOf("") }
    var feedbackInput by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // App Header & Action Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Homework & Assignments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (role == UserRole.TEACHER) "Management & Grading Console" else "Submit Tasks & View Evaluations",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (role == UserRole.TEACHER) {
                Button(
                    onClick = { showPublishDialog = true },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Publish assignment", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Half: Assignment List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    "All Course Homework",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (assignments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No assignments published yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(assignments) { assignment ->
                            val isSelected = selectedAssignment?.id == assignment.id
                            val submission = submissions.find { it.assignmentId == assignment.id && it.studentName == "Alex Carter" }
                            val isCompleted = submission != null

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAssignment = assignment
                                        submissionText = submission?.submissionText ?: ""
                                        selectedSubmissionForGrading = null
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) {
                                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                } else {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
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
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = assignment.subject,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (role == UserRole.STUDENT) {
                                            val badgeColor = if (isCompleted) {
                                                if ((submission?.grade ?: -1) >= 0) Color(0xFF4CAF50) else Color(0xFFFFA000)
                                            } else {
                                                Color(0xFFE53935)
                                            }
                                            val badgeText = if (isCompleted) {
                                                if ((submission?.grade ?: -1) >= 0) "GRADED" else "SUBMITTED"
                                            } else {
                                                "PENDING"
                                            }
                                            Surface(color = badgeColor, shape = RoundedCornerShape(4.dp)) {
                                                Text(
                                                    badgeText,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        } else {
                                            // Count submissions for Teacher
                                            val subCount = submissions.count { it.assignmentId == assignment.id }
                                            Text(
                                                "$subCount turns",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = assignment.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Due: ${assignment.dueDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Right Half: Detail Interactive Area
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
            ) {
                Text(
                    "Workstation Workspace",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val curAssignment = selectedAssignment
                if (curAssignment == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MenuBook, contentDescription = "Notebook logo", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Select an assignment to view details.", color = Color.Gray)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Column {
                                    Text(
                                        curAssignment.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Max Points: ${curAssignment.maxPoints}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Text("•", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Text("Due: ${curAssignment.dueDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Description:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        curAssignment.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            if (role == UserRole.STUDENT) {
                                // STUDENT VIEW: Try / Submit
                                item {
                                    val mySubmission = submissions.find { it.assignmentId == curAssignment.id && it.studentName == "Alex Carter" }
                                    if (mySubmission == null) {
                                        Column {
                                            Text(
                                                "Write Homework Submission Text:",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = submissionText,
                                                onValueChange = { submissionText = it },
                                                placeholder = { Text("E.g. Problem 1 completed workspace details...") },
                                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                                maxLines = 10,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.submitAssignment(curAssignment.id, submissionText)
                                                    focusManager.clearFocus()
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = submissionText.isNotBlank()
                                            ) {
                                                Icon(Icons.Default.CloudUpload, contentDescription = "Upload")
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Submit Homework")
                                            }
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                "Your Submitted Answer:",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                mySubmission.submissionText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Divider()
                                            Spacer(modifier = Modifier.height(12.dp))

                                            if (mySubmission.grade >= 0) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "Graded Logo", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text("Grade: ${mySubmission.grade} / ${curAssignment.maxPoints} Points", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                                        Text("Teacher Review Feedback:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                                                        Text(mySubmission.feedback, style = MaterialTheme.typography.bodyMedium)
                                                    }
                                                }
                                            } else {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(Icons.Default.HourglassEmpty, contentDescription = "Awaiting feedback", tint = Color(0xFFFFA000))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text("Evaluation Pending", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFFFA000))
                                                        Text("Your teacher hasn't graded this submission yet.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // TEACHER VIEW: Grading & Submissions
                                item {
                                    val assignmentSubmissions = submissions.filter { it.assignmentId == curAssignment.id }
                                    Text(
                                        "Student Submissions (${assignmentSubmissions.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (assignmentSubmissions.isEmpty()) {
                                        Text("No students have turned in this assignment yet.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            assignmentSubmissions.forEach { sub ->
                                                val isGraded = sub.grade >= 0
                                                val isBeingGradedObj = selectedSubmissionForGrading?.id == sub.id

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(
                                                            if (isBeingGradedObj) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                                        )
                                                        .clickable {
                                                            selectedSubmissionForGrading = sub
                                                            gradeInput = if (isGraded) sub.grade.toString() else ""
                                                            feedbackInput = sub.feedback
                                                        }
                                                        .padding(10.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(sub.studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                        if (isGraded) {
                                                            Text(
                                                                "Score: ${sub.grade} / ${curAssignment.maxPoints}",
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF4CAF50),
                                                                style = MaterialTheme.typography.bodySmall
                                                            )
                                                        } else {
                                                            Text(
                                                                "Awaiting Grade",
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFFFFA000),
                                                                style = MaterialTheme.typography.bodySmall
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        sub.submissionText, 
                                                        style = MaterialTheme.typography.bodySmall, 
                                                        maxLines = 2,
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Interactive grading editor block
                                item {
                                    val gradingSub = selectedSubmissionForGrading
                                    AnimatedVisibility(
                                        visible = gradingSub != null,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        if (gradingSub != null) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                                    .padding(12.dp)
                                            ) {
                                                Text(
                                                    "Grading: ${gradingSub.studentName}",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))

                                                OutlinedTextField(
                                                    value = gradeInput,
                                                    onValueChange = { gradeInput = it },
                                                    label = { Text("Score Earned (Max ${curAssignment.maxPoints})") },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))

                                                OutlinedTextField(
                                                    value = feedbackInput,
                                                    onValueChange = { feedbackInput = it },
                                                    label = { Text("Teacher feedback / remarks") },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    maxLines = 3
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))

                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    OutlinedButton(
                                                        onClick = { selectedSubmissionForGrading = null },
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text("Cancel")
                                                    }
                                                    Button(
                                                        onClick = {
                                                            val grade = gradeInput.toIntOrNull() ?: 0
                                                            viewModel.gradeSubmission(gradingSub, grade, feedbackInput)
                                                            selectedSubmissionForGrading = null
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text("Submit Grade")
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
        }
    }

    // CREATE ASSIGNMENT POPUP DIALOG
    if (showPublishDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newSubject by remember { mutableStateOf("Mathematics") }
        var newDesc by remember { mutableStateOf("") }
        var newDueDate by remember { mutableStateOf("In 3 days") }
        var newPoints by remember { mutableStateOf("100") }

        AlertDialog(
            onDismissRequest = { showPublishDialog = false },
            title = { Text("Publish New Assignment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Assignment Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    var expandedSubject by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = newSubject,
                            onValueChange = {},
                            label = { Text("Course Subject") },
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
                            DropdownMenuItem(
                                text = { Text("Mathematics") },
                                onClick = { newSubject = "Mathematics"; expandedSubject = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Physics") },
                                onClick = { newSubject = "Physics"; expandedSubject = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Chemistry") },
                                onClick = { newSubject = "Chemistry"; expandedSubject = false }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Assignment guidelines & rules") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    OutlinedTextField(
                        value = newDueDate,
                        onValueChange = { newDueDate = it },
                        label = { Text("Deadline / Due Date") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPoints,
                        onValueChange = { newPoints = it },
                        label = { Text("Max Possible Points") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addAssignment(
                                title = newTitle,
                                description = newDesc,
                                subject = newSubject,
                                dueDate = newDueDate,
                                maxPoints = newPoints.toIntOrNull() ?: 100
                            )
                            showPublishDialog = false
                        }
                    },
                    enabled = newTitle.isNotBlank()
                ) {
                    Text("Publish Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPublishDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}
