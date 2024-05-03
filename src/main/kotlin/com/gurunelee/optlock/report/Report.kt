package com.gurunelee.optlock.report

import java.util.*
import javax.persistence.*

/**
 * Created by chlee on 5/1/24.
 *
 * @author Changha Lee
 * @version opt-lock
 * @since opt-lock
 */
@Entity
class Report (
    @Id
    @Column(name = "REPORT_KEY", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REPORT_KEY")
    @SequenceGenerator(name = "SEQ_REPORT_KEY", sequenceName = "SEQ_REPORT_KEY", allocationSize = 1)
    val reportKey : Long,
){
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
        mappedBy = "report"
    )
    private val answers: MutableSet<ReportAnswer> = mutableSetOf()
    
    @OneToOne(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
        mappedBy = "report"
    )
    private val version = ReportVersion(reportKey, 0L, UUID.randomUUID())
}
