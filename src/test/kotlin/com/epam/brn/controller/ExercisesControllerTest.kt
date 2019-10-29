package com.epam.brn.controller

import com.epam.brn.dto.ExerciseDto
import com.epam.brn.service.ExerciseService
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class ExercisesControllerTest {
    @InjectMocks
    lateinit var exerciseController: ExerciseController
    @Mock
    lateinit var exerciseService: ExerciseService

    @Test
    fun `should get done exercises for user`() {
        // GIVEN
        val userID: Long = 1
        val exercise = ExerciseDto(1, "exe", "desc")
        val listExercises = listOf(exercise)
        Mockito.`when`(exerciseService.findDoneExercises(userID)).thenReturn(listExercises)
        // WHEN
        val actualResultData: List<ExerciseDto> =
            exerciseController.getExercisesByUserID(userID).data as List<ExerciseDto>
        // THEN
        Assertions.assertTrue(actualResultData.contains(exercise))
        verify(exerciseService).findDoneExercises(userID)
    }

    @Test
    fun `should get exercise by id`() {
        // GIVEN
        val exerciseID: Long = 1
        val exercise = ExerciseDto(1, "exe", "desc")
        Mockito.`when`(exerciseService.findExercisesByID(exerciseID)).thenReturn(exercise)
        // WHEN
        val actualResultData: List<ExerciseDto> =
            exerciseController.getExercisesByID(exerciseID).data as List<ExerciseDto>
        // THEN
        Assertions.assertTrue(actualResultData.contains(exercise))
        verify(exerciseService).findExercisesByID(exerciseID)
    }
}