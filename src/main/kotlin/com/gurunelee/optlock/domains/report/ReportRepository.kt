package com.gurunelee.optlock.domains.report

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.LockModeType

@Repository
interface ReportRepository: JpaRepository<Report, Long> {
    @Query("SELECT ra FROM ReportAnswer ra WHERE ra.report.reportKey = :reportKey AND ra.last = true")
    fun findAnswers(reportKey: Long): List<ReportAnswer>

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    fun findOptimisticByReportKey(reportKey: Long): Optional<Report>

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    fun findPessimisticByReportKey(reportKey: Long): Optional<Report>

}
