package com.epam.brn.config

import com.epam.brn.model.Exercise
import com.epam.brn.model.ExerciseGroup
import com.epam.brn.model.Series
import com.epam.brn.model.StudyHistory
import com.epam.brn.model.Task
import com.epam.brn.model.Resource
import com.epam.brn.model.UserAccount
import com.epam.brn.repo.ExerciseGroupRepository
import com.epam.brn.repo.ExerciseRepository
import com.epam.brn.repo.SeriesRepository
import com.epam.brn.repo.StudyHistoryRepository
import com.epam.brn.repo.UserAccountRepository
import org.apache.logging.log4j.kotlin.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.LocalDate
import java.time.LocalDateTime

@Profile("dev")
@Configuration
class DevConfig(
    @Autowired val userAccountRepository: UserAccountRepository,
    @Autowired val exerciseRepository: ExerciseRepository,
    @Autowired val seriesRepository: SeriesRepository,
    @Autowired val studyHistoryRepository: StudyHistoryRepository,
    @Autowired val exerciseGroupRepository: ExerciseGroupRepository
) {
    private val log = logger()

    @Bean
    fun databaseInitializer() = ApplicationRunner {
        log.debug("------- Started DEV initialization")
        createDBMockContentValues()
        loadInitialDataToDb()
        log.debug("------- Finished DEV initialization")
    }

    private fun createDBMockContentValues() {
        val exerciseGroup = exerciseGroupRepository.save(
            ExerciseGroup(
                id = 0,
                description = "desc",
                name = "group"
            )
        )
        log.debug("---- Created $exerciseGroup")

        val series = seriesRepository.save(
            Series(
                id = 0,
                description = "desc",
                name = "group",
                exerciseGroup = exerciseGroup
            )
        )
        log.debug("---- Created $series")

        val useraccount = userAccountRepository.save(
            UserAccount(
                id = 0,
                name = "manuel",
                birthDate = LocalDate.now(),
                email = "123@123.asd"
            )
        )
        log.debug("---- Created $useraccount")

        val exercise = exerciseRepository.save(
            Exercise(
                id = 0,
                description = "someDescription",
                series = series,
                level = 0,
                name = "exercise"
            )
        )
        log.debug("---- Created $exercise")

        val exercise2 = exerciseRepository.save(
            Exercise(
                id = 0,
                description = "someDescription2",
                series = series,
                level = 0,
                name = "exercise2"
            )
        )
        log.debug("---- Created $exercise2")

        val studyHistory = studyHistoryRepository.save(
            StudyHistory(
                id = 0,
                userAccount = useraccount,
                exercise = exercise,
                endTime = LocalDateTime.now(),
                startTime = LocalDateTime.now(),
                doneTasksCount = 2,
                successTasksCount = 1,
                repetitionCount = 3
            )
        )
        log.debug("---- Created $studyHistory")
    }

    fun loadInitialDataToDb() {
        val resource11 =
            Resource(audioFileUrl = "no_noise/бал.mp3", word = "бал", pictureFileUrl = "", soundsCount = 1)
        val resource12 =
            Resource(audioFileUrl = "no_noise/бум.mp3", word = "бум", pictureFileUrl = "", soundsCount = 1)
        val resource13 =
            Resource(audioFileUrl = "no_noise/быль.mp3", word = "быль", pictureFileUrl = "", soundsCount = 1)
        val resource14 =
            Resource(audioFileUrl = "no_noise/вить.mp3", word = "вить", pictureFileUrl = "", soundsCount = 1)
        val resource15 =
            Resource(audioFileUrl = "no_noise/гад.mp3", word = "гад", pictureFileUrl = "", soundsCount = 1)
        val resource16 =
            Resource(audioFileUrl = "", word = "сад", pictureFileUrl = "", soundsCount = 1)
        val resource21 =
            Resource(audioFileUrl = "noise_0db/бал.mp3", word = "бал", pictureFileUrl = "", soundsCount = 1)
        val resource22 =
            Resource(audioFileUrl = "noise_0db/бум.mp3", word = "бум", pictureFileUrl = "", soundsCount = 1)
        val resource23 =
            Resource(audioFileUrl = "noise_0db/быль.mp3", word = "быль", pictureFileUrl = "", soundsCount = 1)
        val resource24 =
            Resource(audioFileUrl = "noise_0db/вить.mp3", word = "вить", pictureFileUrl = "", soundsCount = 1)
        val resource25 =
            Resource(audioFileUrl = "noise_0db/гад.mp3", word = "гад", pictureFileUrl = "", soundsCount = 1)
        val resource26 =
            Resource(audioFileUrl = "", word = "сад", pictureFileUrl = "", soundsCount = 1)
        val group = ExerciseGroup(name = "речевые упражения", description = "речевые упражения")
        val series1 =
            Series(name = "распознование слов", description = "распознование слов", exerciseGroup = group)
        val series2 =
            Series(name = "диахоничкеское слушание", description = "диахоничкеское слушание", exerciseGroup = group)
        group.series.addAll(setOf(series1, series2))

        val exercise1 = Exercise(
            name = "Однослоговые слова",
            description = "Однослоговые слова без шума",
            level = 0,
            series = series1
        )
        val task11 = Task(
            name = "task",
            serialNumber = 1,
            exercise = exercise1,
            correctAnswer = resource11
        )
        task11.answerOptions.addAll(setOf(resource12, resource13, resource14, resource15, resource16))

        val task12 = Task(
            name = "task",
            serialNumber = 2,
            exercise = exercise1,
            correctAnswer = resource12
        )
        task12.answerOptions.addAll(setOf(resource11, resource13, resource14, resource15, resource16))
        exercise1.tasks.addAll(setOf(task11, task12))

        // ============ exercise2 ==========
        val exercise2 = Exercise(
            name = "Однослоговые слова",
            description = "Однослоговые слова малым шумом",
            level = 2,
            series = series1
        )

        val task21 = Task(
            name = "task",
            serialNumber = 1,
            exercise = exercise1,
            correctAnswer = resource21
        )
        task11.answerOptions.addAll(setOf(resource22, resource23, resource24, resource25, resource26))
        val task22 = Task(
            name = "task",
            serialNumber = 2,
            exercise = exercise1,
            correctAnswer = resource22
        )
        task12.answerOptions.addAll(setOf(resource21, resource23, resource24, resource25, resource26))
        exercise2.tasks.addAll(setOf(task21, task22))

        series1.exercises.addAll(setOf(exercise1, exercise2))
        exerciseGroupRepository.save(group)
    }
}