package com.gurunelee.optlock.service

import com.gurunelee.optlock.domains.report.Report
import com.gurunelee.optlock.domains.report.ReportAnswer
import com.gurunelee.optlock.domains.report.ReportRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReportService (
    val reportRepository: ReportRepository,
) {
    @Transactional(readOnly = true)
    fun getReport(reportKey: Long) = reportRepository.findById(reportKey).get()

    @Transactional(readOnly = true)
    fun getReportAnswers(reportKey: Long) = getReport(reportKey).answers

    @Transactional
    fun addReport(title: String): Report {
        return reportRepository.save(Report(title = title))
    }

    @Transactional
    fun saveReport(reportKey: Long, answers: List<ReportAnswer>): Report {
        return getReport(reportKey).apply {
            createOrUpdateAnswers(answers)
        }
    }

    @Transactional
    fun quoteReport(reportKey: Long) {
        val report = reportRepository.findById(reportKey).get()
        report.quote()
    }

    @Transactional
    fun clear() {
        reportRepository.deleteAll()
    }
}