package com.gurunelee.optlock.report

import com.gurunelee.optlock.converters.StringBooleanConverter
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
@Where(clause = "LAST_FLAG = 'Y'")
class ReportAnswer(
    @Id
    @Column(name = "ANSWER_KEY", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ANSWER_KEY")
    @SequenceGenerator(name = "SEQ_ANSWER_KEY", sequenceName = "SEQ_ANSWER_KEY", allocationSize = 1)
    val answerKey: Long,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
        optional = false
    )
    @JoinColumn(name = "REPORT_KEY")
    val report: Report,

    @Column(name = "VALUE", nullable = true)
    val value: String? = null,
    
    @Convert(converter = StringBooleanConverter::class)
    @Column(name = "LAST_FLAG", nullable = false, updatable = true)
    var last: Boolean = false,
) {
}
