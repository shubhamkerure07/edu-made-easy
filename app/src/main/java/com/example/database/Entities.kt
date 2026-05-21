package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "live_sessions")
data class LiveSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val teacherName: String,
    val dateTime: String,
    val isLive: Boolean = false,
    val isPremium: Boolean = false,
    val description: String = "",
    val activeStudentsCount: Int = 0,
    val gradeLevel: String = "All Classes"
) : Serializable

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val subject: String,
    val dueDate: String,
    val maxPoints: Int = 100,
    val releasedDate: String = "Today"
) : Serializable

@Entity(tableName = "submissions")
data class Submission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assignmentId: Int,
    val studentName: String,
    val submissionText: String,
    val submittedAt: String,
    val grade: Int = -1, // -1 means ungraded
    val feedback: String = ""
) : Serializable

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val durationMinutes: Int = 15,
    val releasedAt: String = "Today",
    val totalQuestions: Int = 3
) : Serializable

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String, // "A", "B", "C", "D"
    val points: Int = 10
) : Serializable

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val quizTitle: String,
    val studentName: String,
    val score: Int,
    val totalScore: Int,
    val dateTaken: String
) : Serializable

@Entity(tableName = "one_on_one_bookings")
data class OneOnOneBooking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val teacherName: String,
    val subject: String,
    val date: String,
    val time: String,
    val notes: String = "",
    val status: String = "BOOKED" // "BOOKED", "COMPLETED"
) : Serializable

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int, // fits LiveSession id or OneOnOne id
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isTeacher: Boolean = false
) : Serializable

@Entity(tableName = "student_session_metrics")
data class StudentSessionMetric(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val sessionTitle: String,
    val subject: String,
    val studentName: String,
    val activeSeconds: Long = 0,
    val quitCount: Int = 0,
    val lastUpdateTimestamp: Long = 0,
    val dateString: String = "" // e.g., "Monday", "Tuesday", etc., or "May 21"
) : Serializable
