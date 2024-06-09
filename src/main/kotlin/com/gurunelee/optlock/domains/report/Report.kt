package com.gurunelee.optlock.domains.report

import javax.persistence.*

/**
 * Created by chlee on 5/1/24.
 *
 * @author Changha Lee
 * @version opt-lock
 * @since opt-lock
 */
@Entity
@Table(name = "REPORT")
class Report (
    @Id
    @Column(name = "REPORT_KEY", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val reportKey : Long = 0L,

    @Column(name = "TITLE", nullable = false)
    val title: String,

    @Column(name="QUOTED_CNT", nullable = false)
    var quotedCount: Long = 0L,

    ){
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
        mappedBy = "report"
    )
    val answers: MutableSet<ReportAnswer> = mutableSetOf()
    val answer: ReportAnswer
        get() = answers.firstOfThrow()

    fun quote() {
        quotedCount++
    }

    fun addAnswer(value: String) {
        val answer = ReportAnswer(value = value, last = true, report = this)
        answers.add(answer)
    }

    fun createOrUpdateAnswers(answers: List<ReportAnswer>) {
        answers.forEach{
            if(!it.isSaved()) {
                addAnswer(it.value)
            }
            this.answers.find { answer -> answer.value == it.value }?.let { ra ->
                ra.last = false
                addAnswer(it.value)
            }
        }
    }
}

private fun Set<ReportAnswer>.firstOfThrow() = this.firstOrNull() ?: throw NoSuchElementException("Answer is not exist")
