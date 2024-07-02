package com.gurunelee.optlock.service

import com.gurunelee.optlock.domains.report.ReportAnswer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("h2")
@SpringBootTest
class ReportServiceTest {
    @Autowired
    lateinit var reportService: ReportService

    @AfterEach
    fun tearDown() {
        reportService.deleteAllReport()
    }

    @Test
    fun `Report 인용 동시성 테스트`() { // 얘만 비관적 락 걸고싶으면 어떡하지?
        // given
        val reportKey = reportService.addReport("test").reportKey

        val threadCount = 10
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
        assertEquals(threadCount.toLong(), report.quotedCount)
    }

    @Test
    fun `Report answer 동시성 테스트 - 동시에 여러 탭에서 최초 저장 시도`() {
        // given
        val report = reportService.addReport("title")

        val tabCount = 10
        val answersList = (1..tabCount).map { tab ->
            listOf(
                ReportAnswer(value = "tab$tab-1", last = true, report = report),
                ReportAnswer(value = "tab$tab-2", last = true, report = report),
                ReportAnswer(value = "tab$tab-3", last = true, report = report)
            )
        }

        val thread = mutableListOf<Thread>()

        // when
        repeat(tabCount) {
            thread.add(Thread {
                reportService.saveReport(report.reportKey, answersList[it], report.version)
            })
        }

        thread.forEach { it.start() }
        thread.forEach { it.join() }

        // then
        val result = reportService.getReportAnswers(report.reportKey)
        assertEquals(3, result.size)
    }

    @Test
    fun `Report answer 멀티탭 테스트 - 멀티 탭 최초 저장 시 첫 저장 이후 save 시도는 예외 발생`() {
        // given
        val report = reportService.addReport("title")

        val answersTab1 = listOf(
            ReportAnswer(value = "tab1-1", last = true, report = report),
            ReportAnswer(value = "tab1-2", last = true, report = report),
            ReportAnswer(value = "tab1-3", last = true, report = report),
        )

        val answersTab2 = listOf(
            ReportAnswer(value = "tab2-1", last = true, report = report),
            ReportAnswer(value = "tab2-2", last = true, report = report),
            ReportAnswer(value = "tab2-3", last = true, report = report),
        )

        // when
        reportService.saveReport(report.reportKey, answersTab1, report.version)
        assertThrows<ObjectOptimisticLockingFailureException> {
            reportService.saveReport(report.reportKey, answersTab2, report.version)
        }

        // then
        val result = reportService.getReportAnswers(report.reportKey)
        assertEquals(3, result.size)
    }
}
