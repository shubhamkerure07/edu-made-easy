package com.example.database

import kotlinx.coroutines.flow.Flow

class EduRepository(private val eduDao: EduDao) {

    // --- Live Sessions ---
    val allLiveSessions: Flow<List<LiveSession>> = eduDao.getAllLiveSessions()

    suspend fun getLiveSessionById(id: Int): LiveSession? = eduDao.getLiveSessionById(id)

    suspend fun insertLiveSession(session: LiveSession): Long = eduDao.insertLiveSession(session)

    suspend fun updateLiveSession(session: LiveSession) = eduDao.updateLiveSession(session)

    suspend fun deleteLiveSession(session: LiveSession) = eduDao.deleteLiveSession(session)

    // --- Assignments ---
    val allAssignments: Flow<List<Assignment>> = eduDao.getAllAssignments()

    suspend fun getAssignmentById(id: Int): Assignment? = eduDao.getAssignmentById(id)

    suspend fun insertAssignment(assignment: Assignment): Long = eduDao.insertAssignment(assignment)

    // --- Submissions ---
    val allSubmissions: Flow<List<Submission>> = eduDao.getAllSubmissions()

    fun getSubmissionsForAssignment(assignmentId: Int): Flow<List<Submission>> =
        eduDao.getSubmissionsForAssignment(assignmentId)

    suspend fun insertSubmission(submission: Submission): Long = eduDao.insertSubmission(submission)

    suspend fun updateSubmission(submission: Submission) = eduDao.updateSubmission(submission)

    // --- Quizzes ---
    val allQuizzes: Flow<List<Quiz>> = eduDao.getAllQuizzes()

    suspend fun getQuizById(id: Int): Quiz? = eduDao.getQuizById(id)

    suspend fun insertQuiz(quiz: Quiz): Long = eduDao.insertQuiz(quiz)

    // --- Quiz Questions ---
    fun getQuestionsForQuiz(quizId: Int): Flow<List<QuizQuestion>> =
        eduDao.getQuestionsForQuiz(quizId)

    suspend fun getQuestionsForQuizList(quizId: Int): List<QuizQuestion> =
        eduDao.getQuestionsForQuizList(quizId)

    suspend fun insertQuizQuestion(question: QuizQuestion): Long =
        eduDao.insertQuizQuestion(question)

    // --- Quiz Results ---
    val allQuizResults: Flow<List<QuizResult>> = eduDao.getAllQuizResults()

    fun getQuizResultsForStudent(studentName: String): Flow<List<QuizResult>> =
        eduDao.getQuizResultsForStudent(studentName)

    suspend fun insertQuizResult(result: QuizResult): Long = eduDao.insertQuizResult(result)

    // --- One On One Bookings ---
    val allOneOnOneBookings: Flow<List<OneOnOneBooking>> = eduDao.getAllOneOnOneBookings()

    suspend fun insertOneOnOneBooking(booking: OneOnOneBooking): Long =
        eduDao.insertOneOnOneBooking(booking)

    suspend fun updateOneOnOneBooking(booking: OneOnOneBooking) =
        eduDao.updateOneOnOneBooking(booking)

    // --- Chat Messages ---
    fun getChatMessagesForSession(sessionId: Int): Flow<List<ChatMessage>> =
        eduDao.getChatMessagesForSession(sessionId)

    suspend fun insertChatMessage(message: ChatMessage): Long = eduDao.insertChatMessage(message)

    // --- Student Session Metrics ---
    val allStudentSessionMetrics: Flow<List<StudentSessionMetric>> = eduDao.getAllStudentSessionMetrics()

    fun getStudentSessionMetrics(studentName: String): Flow<List<StudentSessionMetric>> =
        eduDao.getStudentSessionMetrics(studentName)

    suspend fun getStudentMetricForSession(studentName: String, sessionId: Int): StudentSessionMetric? =
        eduDao.getStudentMetricForSession(studentName, sessionId)

    suspend fun insertStudentMetric(metric: StudentSessionMetric): Long =
        eduDao.insertStudentMetric(metric)

    suspend fun updateStudentMetric(metric: StudentSessionMetric) =
        eduDao.updateStudentMetric(metric)
}
