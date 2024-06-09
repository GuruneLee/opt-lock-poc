package com.gurunelee.optlock.domains.report

import com.gurunelee.optlock.domains.converters.StringBooleanConverter
import org.hibernate.annotations.Where
import javax.persistence.*

/**
 * Created by chlee on 5/1/24.
 *
 * @author Changha Lee
 * @version opt-lock
 * @since opt-lock
 */
@Entity
@Table(name = "REPORT_ANSWER")
@Where(clause = "LAST_FLAG = 'Y'")
class ReportAnswer(
    @Id
    @Column(name = "ANSWER_KEY", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val answerKey: Long = 0L,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
        optional = false
    )
    @JoinColumn(name = "REPORT_KEY")
    val report: Report,

    @Column(name = "VALUE")
    var value: String,

    @Convert(converter = StringBooleanConverter::class)
    @Column(name = "LAST_FLAG", nullable = false, updatable = true)
    var last: Boolean,
) {

    fun isSaved() = answerKey > 0L
}
