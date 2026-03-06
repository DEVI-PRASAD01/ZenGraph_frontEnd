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

    @POST("session/complete")
    suspend fun completeSession(@Body request: SessionCompleteRequest): SessionCompleteResponse

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

    @GET("session/stats/{user_id}")
    suspend fun getSessionStats(@Path("user_id") userId: Int): SessionStatsResponse

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

    @GET("analytics/progress/{user_id}")
    suspend fun getProgress(
        @Path("user_id") userId: Int,
        @Query("period") period: String = "day"
    ): ProgressResponse

    @GET("analytics/mood-trend/{user_id}")
    suspend fun getMoodTrend(@Path("user_id") userId: Int): MoodTrendResponse

    @GET("analytics/weekly-completion/{user_id}")
    suspend fun getWeeklyCompletion(@Path("user_id") userId: Int): WeeklyCompletionResponse

    @GET("analytics/summary/{user_id}")
    suspend fun getSummary(@Path("user_id") userId: Int): SummaryResponse

    @GET("analytics/emotion-trend/{user_id}")
    suspend fun getEmotionTrend(
        @Path("user_id") userId: Int,
        @Query("period") period: String = "week"
    ): EmotionTrendResponse

    @POST("session-analysis/analyze")
    suspend fun analyzeSession(@Body request: AIAnalysisRequest): AIAnalysisResponse

    @PUT("profile/preferences/{user_id}")
    suspend fun updatePreferences(
        @Path("user_id") userId: Int,
        @Body request: PreferencesRequest
    ): SimpleResponse

    @GET("library/sessions")
    suspend fun getLibrarySessions(): List<LibrarySession>

    @POST("session/start-library-session")
    suspend fun startLibrarySession(@Body request: StartLibrarySessionRequest): StartLibrarySessionResponse

    @POST("library/generate-plan")
    suspend fun libraryGeneratePlan(@Body request: LibraryGeneratePlanRequest): LibraryGeneratePlanResponse

    @POST("ai/coach-chat")
    suspend fun coachChat(@Body request: CoachChatRequest): CoachChatResponse
}
