package ca.uqac.studify.data.local

import androidx.room.*
import ca.uqac.studify.data.model.Exam
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Query("SELECT * FROM exams ORDER BY examDate ASC, examTime ASC")
    fun getAllExams(): Flow<List<Exam>>

    @Query("SELECT * FROM exams ORDER BY examDate ASC, examTime ASC")
    suspend fun getAllExamsList(): List<Exam>

    @Query("SELECT * FROM exams WHERE id = :examId")
    suspend fun getExamById(examId: Long): Exam?

    @Query("SELECT * FROM exams WHERE courseId = :courseId")
    fun getExamsByCourse(courseId: Long): Flow<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExamById(examId: Long)
}