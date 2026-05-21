package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class UserRole {
    STUDENT,
    TEACHER
}

class EduViewModel(application: Application) : AndroidViewModel(application) {
    private val db = EduDatabase.getDatabase(application)
    private val repository = EduRepository(db.eduDao())

    // --- Role Management ---
    val currentUserRole = MutableStateFlow(UserRole.STUDENT)
    val currentUserName = MutableStateFlow("Alex Carter") // Standard student name

    // --- Core State flows ---
    val liveSessions: StateFlow<List<LiveSession>> = repository.allLiveSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val assignments: StateFlow<List<Assignment>> = repository.allAssignments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val submissions: StateFlow<List<Submission>> = repository.allSubmissions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quizzes: StateFlow<List<Quiz>> = repository.allQuizzes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quizResults: StateFlow<List<QuizResult>> = repository.allQuizResults.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val premiumBookings: StateFlow<List<OneOnOneBooking>> = repository.allOneOnOneBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allStudentMetrics: StateFlow<List<StudentSessionMetric>> = currentUserName.flatMapLatest { name ->
        repository.getStudentSessionMetrics(name)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Live Class Tracker State Flows ---
    val currentSessionActiveSeconds = MutableStateFlow(0L)
    val currentSessionQuitCount = MutableStateFlow(0)
    private var trackingJob: kotlinx.coroutines.Job? = null
    private var trackingSessionId: Int = -1

    // --- Live Class Screen State ---
    val activeLiveSession = MutableStateFlow<LiveSession?>(null)
    val activeLiveChatMessages = activeLiveSession.flatMapLatest { session ->
        if (session != null) {
            repository.getChatMessagesForSession(session.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Quiz Attempt Screen State ---
    val activeQuiz = MutableStateFlow<Quiz?>(null)
    val activeQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val selectedAnswers = MutableStateFlow<Map<Int, String>>(emptyMap()) // map of questionIndex -> "A", "B", "C", "D"
    val quizCompleted = MutableStateFlow(false)
    val finalQuizScore = MutableStateFlow(0)

    init {
        seedInitialDataIfNeeded()
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch {
            // Check if there are existing live sessions, if not seed database
            repository.allLiveSessions.first().let { list ->
                if (list.isEmpty()) {
                    seedDatabase()
                }
            }
        }
    }

    private suspend fun seedDatabase() {
        // 1. Seed Sessions (Class 1 to 5 and standard subjects)
        val s1 = LiveSession(
            title = "Fun with Addition & Numbers",
            subject = "Mathematics",
            teacherName = "Prof. Marcus Vance",
            dateTime = "LIVE NOW",
            isLive = true,
            isPremium = false,
            description = "Welcome to Class 1 Math! Today, we will count colorful objects and learn simple single-digit addition.",
            activeStudentsCount = 12,
            gradeLevel = "Class 1"
        )
        val s2 = LiveSession(
            title = "Introduction to Newton's Laws",
            subject = "Physics",
            teacherName = "Dr. Helen Rostova",
            dateTime = "Scheduled: Today, 6:30 PM",
            isLive = false,
            isPremium = false,
            description = "This session demystifies mechanics. We'll breakdown Newton's 3 Laws of Motion with real-world examples and sample exam numericals.",
            activeStudentsCount = 0,
            gradeLevel = "Grade 10"
        )
        val s3 = LiveSession(
            title = "Personal Calculus Demystified (Premium)",
            subject = "Mathematics",
            teacherName = "Prof. Marcus Vance",
            dateTime = "Premium Session (1-on-1)",
            isLive = false,
            isPremium = true,
            description = "A customized, high-intensity premium coaching session tailored to help you excel in limits, derivatives, and integral calculus.",
            activeStudentsCount = 1,
            gradeLevel = "Grade 10"
        )
        val s4 = LiveSession(
            title = "Plant Parts & Photosynthesis",
            subject = "Science",
            teacherName = "Dr. Helen Rostova",
            dateTime = "LIVE NOW",
            isLive = true,
            isPremium = false,
            description = "Welcome to Class 3 Science! Let's explore roots, stems, leaves, and why solar energy keeps plants healthy.",
            activeStudentsCount = 8,
            gradeLevel = "Class 3"
        )
        val s5 = LiveSession(
            title = "Fun with English Storytelling",
            subject = "English",
            teacherName = "Sarah Jenkins",
            dateTime = "Scheduled: Tomorrow, 2:00 PM",
            isLive = false,
            isPremium = false,
            description = "Welcome to Class 2 English! We are reading 'The Fox and the Grapes' and learning new descriptive verbs.",
            activeStudentsCount = 0,
            gradeLevel = "Class 2"
        )
        val s6 = LiveSession(
            title = "Local Community & Leaders",
            subject = "Social Studies",
            teacherName = "Robert Langdon",
            dateTime = "Scheduled: May 24, 4:00 PM",
            isLive = false,
            isPremium = false,
            description = "Welcome to Class 4 Social Studies! Let's meet our helpful community volunteers, firefighters, and police heroes.",
            activeStudentsCount = 0,
            gradeLevel = "Class 4"
        )
        val s7 = LiveSession(
            title = "Creative Color Shading Core",
            subject = "Art",
            teacherName = "Emily Pierce",
            dateTime = "Scheduled: May 25, 11:30 AM",
            isLive = false,
            isPremium = false,
            description = "Welcome to Class 5 Art! Learn to blend warm and cool watercolors to paint natural sunset landscapes.",
            activeStudentsCount = 0,
            gradeLevel = "Class 5"
        )
        
        val s1Id = repository.insertLiveSession(s1).toInt()
        val s2Id = repository.insertLiveSession(s2).toInt()
        val s3Id = repository.insertLiveSession(s3).toInt()
        val s4Id = repository.insertLiveSession(s4).toInt()
        val s5Id = repository.insertLiveSession(s5).toInt()
        val s6Id = repository.insertLiveSession(s6).toInt()
        val s7Id = repository.insertLiveSession(s7).toInt()

        // 2. Chat Seed Messages for the Live Session
        repository.insertChatMessage(ChatMessage(sessionId = s1Id, senderName = "Prof. Marcus Vance", messageText = "Welcome to Class 1 Addition! Let's start with 3 apples + 2 apples.", isTeacher = true))
        repository.insertChatMessage(ChatMessage(sessionId = s1Id, senderName = "Alex Carter", messageText = "Professor, is it 5 apples?!", isTeacher = false))
        repository.insertChatMessage(ChatMessage(sessionId = s1Id, senderName = "Liam Johnson", messageText = "I count 5 too! This is fun.", isTeacher = false))
        repository.insertChatMessage(ChatMessage(sessionId = s1Id, senderName = "Prof. Marcus Vance", messageText = "Spot on, Alex and Liam! Yes, 5 is correct.", isTeacher = true))

        // Seed telemetry statistics for the Weekly Study Report
        repository.insertStudentMetric(StudentSessionMetric(sessionId = 111, sessionTitle = "Addition Practice", subject = "Mathematics", studentName = "Alex Carter", activeSeconds = 5400, quitCount = 1, dateString = "Monday"))
        repository.insertStudentMetric(StudentSessionMetric(sessionId = 112, sessionTitle = "Phonics and Reading", subject = "English", studentName = "Alex Carter", activeSeconds = 3600, quitCount = 0, dateString = "Tuesday"))
        repository.insertStudentMetric(StudentSessionMetric(sessionId = 114, sessionTitle = "Plant Biology basics", subject = "Science", studentName = "Alex Carter", activeSeconds = 7200, quitCount = 2, dateString = "Wednesday"))
        repository.insertStudentMetric(StudentSessionMetric(sessionId = 116, sessionTitle = "Social Communities", subject = "Social Studies", studentName = "Alex Carter", activeSeconds = 2400, quitCount = 0, dateString = "Thursday"))

        // 3. Seed Assignments
        val a1 = Assignment(
            title = "Quadratic Equations Worksheet",
            description = "Complete problems 1 to 10 on page 42. Show all working stages including factorization and formula application. Scan and upload as a PDF description or text.",
            subject = "Mathematics",
            dueDate = "Tomorrow, 11:59 PM",
            maxPoints = 100,
            releasedDate = "Released Today"
        )
        val a2 = Assignment(
            title = "Friction & Normal Forces Lab Report",
            description = "Submit your analysis of the static vs kinetic friction experiment. Include a brief hypothesis, collected friction coefficients data tables, and conclusion.",
            subject = "Physics",
            dueDate = "May 25, 2026",
            maxPoints = 50,
            releasedDate = "Released 2 days ago"
        )
        val a1Id = repository.insertAssignment(a1).toInt()
        val a2Id = repository.insertAssignment(a2).toInt()

        // 4. Seed Submission (to demonstrate progress tracking & grading)
        repository.insertSubmission(Submission(
            assignmentId = a1Id,
            studentName = "Alex Carter",
            submissionText = "Completed: \n1) x^2 - 5x + 6 = 0 -> (x-2)(x-3)=0 -> x=2,3.\n2) 2x^2 + 8x - 10 = 0 -> 2(x^2 + 4x - 5) = 0 -> x=1, -5.\nRest are completed in my notebook.",
            submittedAt = "Today, 10:45 AM",
            grade = 95,
            feedback = "Brilliant working. Very neat! Check your calculations again on problem 7 but overall stellar execution."
        ))

        // 5. Seed Quizzes
        val q1 = Quiz(
            title = "Organic Compounds & Alkanes",
            subject = "Chemistry",
            durationMinutes = 5,
            releasedAt = "Released Today",
            totalQuestions = 3
        )
        val q2 = Quiz(
            title = "Motion & Speed Assessment",
            subject = "Physics",
            durationMinutes = 8,
            releasedAt = "Released 3 days ago",
            totalQuestions = 3
        )
        
        val q1Id = repository.insertQuiz(q1).toInt()
        val q2Id = repository.insertQuiz(q2).toInt()

        // 6. Seed Quiz Questions
        // Quiz 1 (Chemistry) Questions
        repository.insertQuizQuestion(QuizQuestion(
            quizId = q1Id,
            questionText = "What is the general molecular formula for saturated hydrocarbons (Alkanes)?",
            optionA = "CnH2n",
            optionB = "CnH2n-2",
            optionC = "CnH2n+2",
            optionD = "CnHn",
            correctOption = "C",
            points = 10
        ))
        repository.insertQuizQuestion(QuizQuestion(
            quizId = q1Id,
            questionText = "Which hydrocarbon is the main component of natural gas?",
            optionA = "Methane (CH4)",
            optionB = "Butane (C4H10)",
            optionC = "Ethane (C2H6)",
            optionD = "Propane (C3H8)",
            correctOption = "A",
            points = 10
        ))
        repository.insertQuizQuestion(QuizQuestion(
            quizId = q1Id,
            questionText = "Which functional group is present in primary alcohols?",
            optionA = "-CHO (Aldehyde)",
            optionB = "-OH (Hydroxyl)",
            optionC = "-COOH (Carboxyl)",
            optionD = "-O- (Ether)",
            correctOption = "B",
            points = 10
        ))

        // Quiz 2 (Physics) Questions
        repository.insertQuizQuestion(QuizQuestion(
            quizId = q2Id,
            questionText = "What represents the rate of change of velocity over time?",
            optionA = "Speed",
            optionB = "Displacement",
            optionC = "Acceleration",
            optionD = "Momentum",
            correctOption = "C",
            points = 10
        ))
        repository.insertQuizQuestion(QuizQuestion(
            quizId = q2Id,
            questionText = "A car travels 150 km in 3 hours. What is its average speed?",
            optionA = "45 km/h",
            optionB = "50 km/h",
            optionC = "60 km/h",
            optionD = "30 km/h",
            correctOption = "B",
            points = 10
        ))
        repository.insertQuizQuestion(QuizQuestion(
            quizId = q2Id,
            questionText = "Which of the following is a scalar quantity?",
            optionA = "Velocity",
            optionB = "Force",
            optionC = "Weight",
            optionD = "Distance",
            correctOption = "D",
            points = 10
        ))

        // 7. Seed Quiz Result
        repository.insertQuizResult(QuizResult(
            quizId = q2Id,
            quizTitle = "Motion & Speed Assessment",
            studentName = "Alex Carter",
            score = 20,
            totalScore = 30,
            dateTaken = "May 19, 2026"
        ))

        // 8. Seed Premium One-on-One session booking
        repository.insertOneOnOneBooking(OneOnOneBooking(
            studentName = "Alex Carter",
            teacherName = "Prof. Marcus Vance",
            subject = "Personal Calculus Coaching",
            date = "May 25, 2026",
            time = "3:30 PM",
            notes = "Please review my integration by parts worksheet. I keep making algebraic errors with sign triggers.",
            status = "BOOKED"
        ))
    }

    // --- Actions ---

    fun setRole(role: UserRole) {
        currentUserRole.value = role
        if (role == UserRole.TEACHER) {
            currentUserName.value = "Prof. Marcus Vance"
        } else {
            currentUserName.value = "Alex Carter"
        }
    }

    // Classroom Video/Stream Management
    fun selectLiveSession(session: LiveSession?) {
        activeLiveSession.value = session
    }

    fun addLiveSession(title: String, subject: String, dateTime: String, isPremium: Boolean, description: String, gradeLevel: String = "All Classes") {
        viewModelScope.launch {
            val session = LiveSession(
                title = title,
                subject = subject,
                teacherName = currentUserName.value,
                dateTime = dateTime,
                isLive = dateTime.equals("live now", ignoreCase = true) || dateTime.contains("LIVE", ignoreCase = true),
                isPremium = isPremium,
                description = description,
                activeStudentsCount = if (isPremium) 1 else 0,
                gradeLevel = gradeLevel
            )
            repository.insertLiveSession(session)
        }
    }

    // --- Live Class Tracker Operations ---
    fun startTrackingSession(sessionId: Int, title: String, subject: String) {
        trackingSessionId = sessionId
        currentSessionActiveSeconds.value = 0L
        currentSessionQuitCount.value = 0
        
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            val studentName = currentUserName.value
            val existing = repository.getStudentMetricForSession(studentName, sessionId)
            if (existing != null) {
                currentSessionQuitCount.value = existing.quitCount
            }
            while (true) {
                kotlinx.coroutines.delay(1000L)
                currentSessionActiveSeconds.value += 1
                
                // Save database metrics periodically (every 10 seconds)
                if (currentSessionActiveSeconds.value % 10 == 0L) {
                    saveMetricToDatabase(sessionId, title, subject, isRegularTick = true)
                }
            }
        }
    }

    fun stopTrackingSession(sessionId: Int, title: String, subject: String, isQuit: Boolean = true) {
        trackingJob?.cancel()
        trackingJob = null
        if (trackingSessionId == sessionId) {
            viewModelScope.launch {
                saveMetricToDatabase(sessionId, title, subject, isRegularTick = false, isQuit = isQuit)
                trackingSessionId = -1
            }
        }
    }

    private suspend fun saveMetricToDatabase(
        sessionId: Int, 
        title: String, 
        subject: String, 
        isRegularTick: Boolean,
        isQuit: Boolean = false
    ) {
        val studentName = currentUserName.value
        val existing = repository.getStudentMetricForSession(studentName, sessionId)
        val gainedSeconds = if (isRegularTick) 10 else (currentSessionActiveSeconds.value % 10)
        
        val sdf = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
        val dayOfWeek = sdf.format(java.util.Date()) // e.g., "Monday", "Tuesday", etc.

        if (existing != null) {
            val updated = existing.copy(
                activeSeconds = existing.activeSeconds + gainedSeconds,
                quitCount = existing.quitCount + (if (isQuit) 1 else 0),
                lastUpdateTimestamp = System.currentTimeMillis(),
                dateString = dayOfWeek
            )
            repository.updateStudentMetric(updated)
            currentSessionQuitCount.value = updated.quitCount
        } else {
            val fresh = StudentSessionMetric(
                sessionId = sessionId,
                sessionTitle = title,
                subject = subject,
                studentName = studentName,
                activeSeconds = gainedSeconds,
                quitCount = if (isQuit) 1 else 0,
                lastUpdateTimestamp = System.currentTimeMillis(),
                dateString = dayOfWeek
            )
            repository.insertStudentMetric(fresh)
            currentSessionQuitCount.value = fresh.quitCount
        }
    }

    fun updateLiveSession(session: LiveSession) {
        viewModelScope.launch {
            repository.updateLiveSession(session)
        }
    }

    fun endLiveSession(session: LiveSession) {
        viewModelScope.launch {
            repository.deleteLiveSession(session)
            if (activeLiveSession.value?.id == session.id) {
                activeLiveSession.value = null
            }
        }
    }

    fun sendChatMessage(messageText: String) {
        val session = activeLiveSession.value ?: return
        if (messageText.isBlank()) return
        viewModelScope.launch {
            repository.insertChatMessage(
                ChatMessage(
                    sessionId = session.id,
                    senderName = currentUserName.value,
                    messageText = messageText,
                    isTeacher = currentUserRole.value == UserRole.TEACHER
                )
            )
        }
    }

    // Assignment Management
    fun addAssignment(title: String, description: String, subject: String, dueDate: String, maxPoints: Int) {
        viewModelScope.launch {
            val ass = Assignment(
                title = title,
                description = description,
                subject = subject,
                dueDate = dueDate,
                maxPoints = maxPoints
            )
            repository.insertAssignment(ass)
        }
    }

    fun submitAssignment(assignmentId: Int, text: String) {
        viewModelScope.launch {
            val sub = Submission(
                assignmentId = assignmentId,
                studentName = currentUserName.value,
                submissionText = text,
                submittedAt = "Just now",
                grade = -1,
                feedback = ""
            )
            repository.insertSubmission(sub)
        }
    }

    fun gradeSubmission(submission: Submission, grade: Int, feedback: String) {
        viewModelScope.launch {
            val updated = submission.copy(grade = grade, feedback = feedback)
            repository.updateSubmission(updated)
        }
    }

    // Quiz Management
    fun launchQuiz(quiz: Quiz) {
        viewModelScope.launch {
            activeQuiz.value = quiz
            val questions = repository.getQuestionsForQuizList(quiz.id)
            activeQuizQuestions.value = questions
            currentQuestionIndex.value = 0
            selectedAnswers.value = emptyMap()
            quizCompleted.value = false
            finalQuizScore.value = 0
        }
    }

    fun submitAnswer(questionIndex: Int, answer: String) {
        val current = selectedAnswers.value.toMutableMap()
        current[questionIndex] = answer
        selectedAnswers.value = current
    }

    fun prevQuestion() {
        if (currentQuestionIndex.value > 0) {
            currentQuestionIndex.value -= 1
        }
    }

    fun nextQuestion() {
        if (currentQuestionIndex.value < activeQuizQuestions.value.size - 1) {
            currentQuestionIndex.value += 1
        } else {
            // Last question, evaluate!
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val quiz = activeQuiz.value ?: return
        val questions = activeQuizQuestions.value
        val answers = selectedAnswers.value

        var totalEarned = 0
        var totalPossible = 0

        questions.forEachIndexed { index, question ->
            val userAnswer = answers[index] ?: ""
            if (userAnswer.equals(question.correctOption, ignoreCase = true)) {
                totalEarned += question.points
            }
            totalPossible += question.points
        }

        finalQuizScore.value = totalEarned
        quizCompleted.value = true

        // Insert results dynamically to Room
        viewModelScope.launch {
            repository.insertQuizResult(
                QuizResult(
                    quizId = quiz.id,
                    quizTitle = quiz.title,
                    studentName = currentUserName.value,
                    score = totalEarned,
                    totalScore = totalPossible,
                    dateTaken = "Today"
                )
            )
        }
    }

    fun exitQuiz() {
        activeQuiz.value = null
        activeQuizQuestions.value = emptyList()
        quizCompleted.value = false
    }

    // Premium Booking Management
    fun bookPremiumSession(subject: String, date: String, time: String, notes: String) {
        viewModelScope.launch {
            repository.insertOneOnOneBooking(
                OneOnOneBooking(
                    studentName = currentUserName.value,
                    teacherName = "Prof. Marcus Vance", // Default primary online teacher
                    subject = subject,
                    date = date,
                    time = time,
                    notes = notes,
                    status = "BOOKED"
                )
            )
            // Automatically make an upcoming Premium Live Session so they can also enter it later!
            repository.insertLiveSession(
                LiveSession(
                    title = "Premium 1-on-1: $subject",
                    subject = subject,
                    teacherName = "Prof. Marcus Vance",
                    dateTime = "$date at $time",
                    isLive = false,
                    isPremium = true,
                    description = "Personalized private block booked for Alex Carter. Notes: $notes",
                    activeStudentsCount = 1
                )
            )
        }
    }

    fun completeBooking(booking: OneOnOneBooking) {
        viewModelScope.launch {
            repository.updateOneOnOneBooking(booking.copy(status = "COMPLETED"))
        }
    }
}
