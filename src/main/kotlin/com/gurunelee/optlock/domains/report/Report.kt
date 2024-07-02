package com.gurunelee.optlock.domains.report

import org.springframework.orm.ObjectOptimisticLockingFailureException
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

    @Version
    val version: Long = 0L,

    @Column(name = "TITLE", nullable = false)
    val title: String,

    @Column(name="QUOTED_CNT", nullable = false)
    var quotedCount: Long = 0L,

    ){
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
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

    fun createOrUpdateAnswers(answers: List<ReportAnswer>, version: Long?) {
        if (this.version != version) {
            throw ObjectOptimisticLockingFailureException(
                this::class.java,
                "[key:${this.reportKey}][${this.version}:${this.version}] Version mismatch"
            )
        }

        answers.forEach{
            if(!it.isSaved()) {
                addAnswer(it.value)
            } else {
                this.answers.find { answer -> answer.answerKey == it.answerKey }?.let { old ->
                    old.last = false
                    addAnswer(it.value)
                }
            }
        }
    }
}

private fun Set<ReportAnswer>.firstOfThrow() = this.firstOrNull() ?: throw NoSuchElementException("Answer is not exist")

/***
 * create table report_answer (
 *  answer_key bigint generated by default as identity, 
 *  last_flag varchar(255) not null, 
 *  value varchar(255), 
 *  report_key bigint not null,  
 *  primary key (answer_key)
 * )
 */
