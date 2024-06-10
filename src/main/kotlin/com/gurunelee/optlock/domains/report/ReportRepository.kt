package com.gurunelee.optlock.domains.report

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository: JpaRepository<Report, Long> {
    @Query("SELECT ra FROM ReportAnswer ra WHERE ra.report.reportKey = :reportKey AND ra.last = true")
    fun findAnswers(reportKey: Long): List<ReportAnswer>
}