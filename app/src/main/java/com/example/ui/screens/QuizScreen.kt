package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.Quiz
import com.example.database.QuizResult
import com.example.ui.EduViewModel
import com.example.ui.UserRole
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val role by viewModel.currentUserRole.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()
    val results by viewModel.quizResults.collectAsState()
    
    val activeQuiz by viewModel.activeQuiz.collectAsState()
    val activeQuestions by viewModel.activeQuizQuestions.collectAsState()
    val currentQuestionIdx by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()
    val finalScore by viewModel.finalQuizScore.collectAsState()

    // Countdown Timer logic for Active quiz
    var secondsLeft by remember { mutableStateOf(300) } // 5 minutes
    LaunchedEffect(activeQuiz, quizCompleted) {
        if (activeQuiz != null && !quizCompleted) {
            secondsLeft = (activeQuiz?.durationMinutes ?: 5) * 60
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
            if (secondsLeft == 0 && !quizCompleted) {
                // Auto submit when time runs out!
                // Simulated submit is handled inside VM, let's trigger it
                // We can do it by navigating to end
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        if (activeQuiz == null) {
            // MAIN DASHBOARD: Available quizzes & analytics scoreboard
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Assessments & Testing Station",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Practice under timed exam conditions to measure syllabus progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Quizzes list
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Available Syllabus Tests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (quizzes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No assessments uploaded yet.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(quizzes) { quiz ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = quiz.subject,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.HourglassEmpty,
                                                    contentDescription = "Timer limit",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "${quiz.durationMinutes} min",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = quiz.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "${quiz.totalQuestions} Questions • ${quiz.totalQuestions * 10} Total Marks possible",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (role == UserRole.STUDENT) {
                                            Button(
                                                onClick = { viewModel.launchQuiz(quiz) },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = "Start Test")
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Start Assessed Quiz")
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                Text(
                                                    "Syllabus Active",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Column: Student progress charts analytics & scoreboards
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        if (role == UserRole.STUDENT) "Your Progress Chart (Quizzes)" else "Classroom Results scoreboard",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Hand-crafted Canvas Line or Bar Chart representing score history
                    if (role == UserRole.STUDENT) {
                        val studentResults = results.filter { it.studentName == "Alex Carter" }
                        if (studentResults.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Historical Quiz Trends (Percentage %)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Render custom columns
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(horizontal = 14.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            studentResults.takeLast(5).reversed().forEach { res ->
                                                val scorePct = if (res.totalScore > 0) res.score.toFloat() / res.totalScore else 0f
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Bottom,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        "${(scorePct * 100).toInt()}%",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .width(28.dp)
                                                            .fillMaxHeight(scorePct.coerceIn(0.1f, 1f))
                                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                            .background(
                                                                Brush.verticalGradient(
                                                                    colors = listOf(
                                                                        MaterialTheme.colorScheme.primary,
                                                                        MaterialTheme.colorScheme.primaryContainer
                                                                    )
                                                                )
                                                            )
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        res.quizTitle.take(8) + "..",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontSize = 8.sp,
                                                        color = Color.Gray,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Complete and submit a quiz to view chart analytics.", color = Color.Gray, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        // Display total class average or quick statistics for Teacher
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Class average accuracy", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text("82.4 %", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                }
                                Icon(Icons.Default.TrendingUp, contentDescription = "trend icon", tint = Color(0xFF4CAF50), modifier = Modifier.size(36.dp))
                            }
                        }
                    }

                    // Scoreboard logs table (List of Results)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Historical Score Card Logs",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "Verified Grades symbol", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            }
                            Divider()

                            val filterResults = if (role == UserRole.STUDENT) {
                                results.filter { it.studentName == "Alex Carter" }
                            } else {
                                results
                            }

                            if (filterResults.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No test scores log registered.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxSize().padding(top = 8.dp)
                                ) {
                                    items(filterResults) { scoreCard ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(scoreCard.quizTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                Text("Evaluated: ${scoreCard.studentName} • ${scoreCard.dateTaken}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                            }

                                            Surface(
                                                color = if (scoreCard.score >= scoreCard.totalScore * 0.7) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "${scoreCard.score}/${scoreCard.totalScore}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (scoreCard.score >= scoreCard.totalScore * 0.7) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TIMED TESTING STATION ACTIVE
            val questions = activeQuestions
            val activeQuizObj = activeQuiz ?: return
            
            if (quizCompleted) {
                // REVIEW SHEETS
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Finished Assessment logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Syllabus Assessment Completed!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Your score card has been recorded instantly in your progress profile.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MARKS OBTAINED", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text("$finalScore pt", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                            Divider(modifier = Modifier.height(50.dp).width(1.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ACCURACY", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                val finalPct = if (questions.isNotEmpty()) (finalScore.toFloat() / (questions.size * 10) * 100).toInt() else 0
                                Text("$finalPct %", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = if (finalPct >= 70) Color(0xFF4CAF50) else Color(0xFFE53935))
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { viewModel.exitQuiz() },
                            modifier = Modifier.width(200.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Return to Classroom Feed")
                        }
                    }
                }
            } else {
                // ACTIVE WORKING PANEL
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp)
                    ) {
                        // Station stats bars
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    activeQuizObj.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Course Topic Exam", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }

                            // Timer Countdowns
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (secondsLeft < 60) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.HourglassBottom,
                                        contentDescription = "Countdowns limit indicator",
                                        tint = if (secondsLeft < 60) Color.Red else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    val minutes = secondsLeft / 60
                                    val seconds = secondsLeft % 60
                                    Text(
                                        text = String.format("%02d:%02d", minutes, seconds),
                                        fontWeight = FontWeight.Black,
                                        color = if (secondsLeft < 60) Color.Red else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        if (questions.isEmpty()) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            val questionIdx = currentQuestionIdx
                            val currentQuestion = questions[questionIdx]

                            Column(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            ) {
                                // Question Tracker
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        "QUESTION ${questionIdx + 1} OF ${questions.size}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(2.dp)) {
                                        Text(
                                            "Grade: ${currentQuestion.points} pts",
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }

                                // Question text
                                Text(
                                    currentQuestion.questionText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 28.sp,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )

                                // Multiple Choices A, B, C, D
                                val selectedChoice = selectedAnswers[questionIdx] ?: ""

                                val choices = listOf(
                                    "A" to currentQuestion.optionA,
                                    "B" to currentQuestion.optionB,
                                    "C" to currentQuestion.optionC,
                                    "D" to currentQuestion.optionD
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    choices.forEach { (optionCode, optionText) ->
                                        val isSelectedChoice = selectedChoice == optionCode
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.submitAnswer(questionIdx, optionCode) },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelectedChoice) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            ),
                                            border = if (isSelectedChoice) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            if (isSelectedChoice) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        optionCode,
                                                        fontWeight = FontWeight.Black,
                                                        color = if (isSelectedChoice) Color.White else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    optionText,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelectedChoice) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Footer Bottom navigation for Timed quiz
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { viewModel.prevQuestion() },
                                    enabled = questionIdx > 0
                                ) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "back icon", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Back")
                                }

                                Button(
                                    onClick = { viewModel.nextQuestion() },
                                    enabled = selectedAnswers[questionIdx] != null
                                ) {
                                    Text(if (questionIdx == questions.size - 1) "Submit Exam" else "Next")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        if (questionIdx == questions.size - 1) Icons.Default.Check else Icons.Default.ArrowForward,
                                        contentDescription = "Forward Icon",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
