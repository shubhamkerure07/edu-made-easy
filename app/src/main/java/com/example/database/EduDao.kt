package com.example.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EduDao {
    // --- Live Sessions ---
    @Query("SELECT * FROM live_sessions ORDER BY id DESC")
    fun getAllLiveSessions(): Flow<List<LiveSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveSession(session: LiveSession): Long

    @Update
    suspend fun updateLiveSession(session: LiveSession)

    @Delete
    suspend fun deleteLiveSession(session: LiveSession)

    @Query("SELECT * FROM live_sessions WHERE id = :id")
    suspend fun getLiveSessionById(id: Int): LiveSession?

    // --- Assignments ---
    @Query("SELECT * FROM assignments ORDER BY id DESC")
    fun getAllAssignments(): Flow<List<Assignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment): Long

    @Query("SELECT * FROM assignments WHERE id = :id")
    suspend fun getAssignmentById(id: Int): Assignment?

    // --- Submissions ---
    @Query("SELECT * FROM submissions ORDER BY id DESC")
    fun getAllSubmissions(): Flow<List<Submission>>

    @Query("SELECT * FROM submissions WHERE assignmentId = :assignmentId")
    fun getSubmissionsForAssignment(assignmentId: Int): Flow<List<Submission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: Submission): Long

    @Update
    suspend fun updateSubmission(submission: Submission)

    // --- Quizzes ---
    @Query("SELECT * FROM quizzes ORDER BY id DESC")
    fun getAllQuizzes(): Flow<List<Quiz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz): Long

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizById(id: Int): Quiz?

    // --- Quiz Questions ---
    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY id ASC")
    fun getQuestionsForQuiz(quizId: Int): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY id ASC")
    suspend fun getQuestionsForQuizList(quizId: Int): List<QuizQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(question: QuizQuestion): Long

    // --- Quiz Results ---
    @Query("SELECT * FROM quiz_results ORDER BY id DESC")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Query("SELECT * FROM quiz_results WHERE studentName = :studentName ORDER BY id DESC")
    fun getQuizResultsForStudent(studentName: String): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult): Long

    // --- One On One Bookings ---
    @Query("SELECT * FROM one_on_one_bookings ORDER BY id DESC")
    fun getAllOneOnOneBookings(): Flow<List<OneOnOneBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOneOnOneBooking(booking: OneOnOneBooking): Long

    @Update
    suspend fun updateOneOnOneBooking(booking: OneOnOneBooking)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getChatMessagesForSession(sessionId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage): Long

    // --- Student Session Metrics (Attendance & Quit tracking) ---
    @Query("SELECT * FROM student_session_metrics ORDER BY id DESC")
    fun getAllStudentSessionMetrics(): Flow<List<StudentSessionMetric>>

    @Query("SELECT * FROM student_session_metrics WHERE studentName = :studentName ORDER BY id DESC")
    fun getStudentSessionMetrics(studentName: String): Flow<List<StudentSessionMetric>>

    @Query("SELECT * FROM student_session_metrics WHERE studentName = :studentName AND sessionId = :sessionId LIMIT 1")
    suspend fun getStudentMetricForSession(studentName: String, sessionId: Int): StudentSessionMetric?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentMetric(metric: StudentSessionMetric): Long

    @Update
    suspend fun updateStudentMetric(metric: StudentSessionMetric)
}
