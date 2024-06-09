package com.gurunelee.optlock.service

import com.gurunelee.optlock.domains.report.ReportAnswer
import com.gurunelee.optlock.domains.report.ReportRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ReportServiceTest {
    @Autowired
    lateinit var reportService: ReportService

    @Autowired
    lateinit var reportRepository: ReportRepository

    @AfterEach
    fun tearDown() {
        reportService.clear()
    }

    @Test
    fun `Report 인용 동시성 테스트`() {
        // given
        val reportKey = reportService.addReport("test").reportKey

        val threadCount = 100
        val thread = mutableListOf<Thread>()

        // when
        repeat(threadCount) {
            thread.add(Thread {
                reportService.quoteReport(reportKey)
            })
        }

        thread.forEach { it.start() }
        thread.forEach { it.join() }

        // then
        val report = reportService.getReport(reportKey)
        assertEquals(threadCount, report.quotedCount)
    }

    @Test
    fun `Report answer 멀티탭 테스트`() {
        // given
        val reportKey = reportService.addReport("title").reportKey
        val answers = listOf(
            ReportAnswer(value = "1", last = true, report = reportService.getReport(reportKey)),
            ReportAnswer(value = "2", last = true, report = reportService.getReport(reportKey)),
            ReportAnswer(value = "3", last = true, report = reportService.getReport(reportKey)),
        )
        reportService.saveReport(reportKey, answers) // 최초 저장
        val oldAnswers = reportService.saveReport(reportKey, answers).answers

        // when
        val tab1Answers = oldAnswers.onEach { it.value += 10 }.toList()
        reportService.saveReport(
            reportKey,
            tab1Answers
        )

        val tab2Answers = oldAnswers.onEach { it.value += 20 }.toList()
        reportService.saveReport(
            reportKey,
            tab2Answers
        )

        // then
        val result = reportService.getReportAnswers(reportKey)
        assertEquals(3, result.size)
    }
}