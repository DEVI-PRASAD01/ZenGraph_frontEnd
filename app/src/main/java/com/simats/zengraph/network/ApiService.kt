package com.simats.zengraph.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("auth/dashboard/{user_id}")
    suspend fun getDashboard(@Path("user_id") userId: Int): DashboardResponse

    @POST("session/complete/{session_id}")
    suspend fun completeSession(
        @Path("session_id") sessionId: Int,
        @Body request: SessionCompleteRequest
    ): SessionCompleteResponse

    @POST("auth/checkin")
    suspend fun checkIn(@Body request: CheckInRequest): SimpleResponse

    @POST("ai/predict-emotion")
    suspend fun predictEmotion(@Body request: EmotionPredictionRequest): EmotionPredictionResponse

    @POST("session/experience/select")
    suspend fun selectExperience(@Body request: ExperienceSelectionRequest): SimpleResponse

    @POST("ai/generate-plan")
    suspend fun generatePlan(@Body request: GeneratePlanRequest): GeneratePlanResponse

    @POST("session/start")
    suspend fun startSession(@Body request: SessionStartRequest): SessionStartResponse

    @GET("profile/{user_id}")
    suspend fun getProfile(@Path("user_id") userId: Int): ProfileResponse

    @Multipart
    @POST("profile/upload-photo")
    suspend fun uploadPhoto(
        @Part("user_id") userId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadPhotoResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): AuthResponse

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): AuthResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): AuthResponse

    @PUT("profile/preferences/{user_id}")
    suspend fun updatePreferences(
        @Path("user_id") userId: Int,
        @Body request: PreferencesRequest
    ): SimpleResponse

    // ── Analytics ──────────────────────────────────────────

    @GET("analytics/mood-history/{user_id}")
    suspend fun getMoodHistory(
        @Path("user_id") userId: Int
    ): Response<MoodHistoryResponse>

    @GET("analytics/mood-trend/{user_id}")
    suspend fun getMoodTrend(
        @Path("user_id") userId: Int
    ): MoodTrendResponse

    @GET("analytics/weekly-completion/{user_id}")
    suspend fun getWeeklyCompletion(
        @Path("user_id") userId: Int
    ): Response<WeeklyCompletionResponse>

    @GET("analytics/summary/{user_id}")
    suspend fun getSummary(
        @Path("user_id") userId: Int
    ): SummaryResponse

    @GET("analytics/summary/{user_id}")
    suspend fun getAnalyticsSummary(
        @Path("user_id") userId: Int
    ): Response<AnalyticsSummaryResponse>

    @GET("analytics/progress/{user_id}")
    suspend fun getProgress(
        @Path("user_id") userId: Int,
        @Query("period") period: String = "week"
    ): ProgressResponse

    @GET("analytics/progress/{user_id}")
    suspend fun getProgressAnalytics(
        @Path("user_id") userId: Int,
        @Query("period") period: String = "week"
    ): Response<ProgressAnalyticsResponse>

    @GET("analytics/emotion-trend/{user_id}")
    suspend fun getEmotionTrend(
        @Path("user_id") userId: Int,
        @Query("period") period: String = "week"
    ): EmotionTrendResponse

    // ── Meditation Library ─────────────────────────────────

    @GET("library/sessions")
    suspend fun getLibrarySessions(): List<LibrarySession>

    @POST("library/generate-plan")
    suspend fun libraryGeneratePlan(
        @Body request: LibraryGeneratePlanRequest
    ): LibraryGeneratePlanResponse

    @POST("session/start")
    suspend fun startLibrarySession(
        @Body request: StartLibrarySessionRequest
    ): SessionStartResponse

    // ── AI Analysis ────────────────────────────────────────

    @POST("session-analysis/analyze")
    suspend fun analyzeSession(
        @Body request: AIAnalysisRequest
    ): AIAnalysisResponse

    // ── AI Features ────────────────────────────────────────

    @GET("analytics/mood-prediction/{user_id}")
    suspend fun getMoodPrediction(
        @Path("user_id") userId: Int
    ): Response<MoodPredictionResponse>

    @GET("analytics/adaptive-duration/{user_id}")
    suspend fun getAdaptiveDuration(
        @Path("user_id") userId: Int
    ): Response<AdaptiveDurationResponse>

    // ── Session stats ──────────────────────────────────────

    @GET("session/stats/{user_id}")
    suspend fun getSessionStats(
        @Path("user_id") userId: Int
    ): Response<SessionStatsResponse>

    // ── Social Features ────────────────────────────────────

    @GET("social/friend-streaks/{user_id}")
    suspend fun getFriendStreaks(
        @Path("user_id") userId: Int
    ): Response<FriendStreaksResponse>

    @GET("social/find-user")
    suspend fun findUser(
        @Query("name") name: String,
        @Query("current_user_id") currentUserId: Int = 0
    ): Response<FindUserResponse>

    @POST("social/add-friend")
    suspend fun addFriend(
        @Body request: AddFriendRequest
    ): Response<SimpleResponse>

    @POST("social/nudge")
    suspend fun nudgeFriend(
        @Body request: NudgeRequest
    ): Response<SimpleResponse>

    @GET("social/current-challenge")
    suspend fun getCurrentChallenge(): Response<CurrentChallengeResponse>

    @POST("social/join-challenge")
    suspend fun joinChallenge(
        @Body request: JoinChallengeRequest
    ): Response<SimpleResponse>

    @GET("social/leaderboard/{challenge_id}")
    suspend fun getLeaderboard(
        @Path("challenge_id") challengeId: Int
    ): Response<LeaderboardResponse>

    @GET("social/my-challenge-progress/{user_id}")
    suspend fun getMyChallengeProgress(
        @Path("user_id") userId: Int
    ): Response<MyChallengeProgressResponse>

    @GET("analytics/mood-week/{user_id}")
    suspend fun getMoodWeek(
        @Path("user_id") userId: Int
    ): Response<MoodWeekResponse>

    @GET("session/history/{user_id}")
    suspend fun getSessionHistory(
        @Path("user_id") userId: Int
    ): Response<SessionHistoryResponse>

    // ── Chat ──────────────────────────────────────────────────

    @POST("chat/send")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<SimpleResponse>

    @GET("chat/messages/{user_id}/{friend_id}")
    suspend fun getMessages(
        @Path("user_id")   userId:   Int,
        @Path("friend_id") friendId: Int
    ): Response<ChatMessagesResponse>

    @GET("chat/unread/{user_id}")
    suspend fun getUnreadCount(
        @Path("user_id") userId: Int
    ): Response<UnreadCountResponse>

    @GET("chat/chat-list/{user_id}")
    suspend fun getChatList(
        @Path("user_id") userId: Int
    ): Response<ChatListResponse>

    @POST("chat/mark-read")
    suspend fun markRead(
        @Body request: MarkReadRequest
    ): Response<SimpleResponse>
}