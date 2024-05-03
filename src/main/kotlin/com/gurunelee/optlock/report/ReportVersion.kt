package com.gurunelee.optlock.report;

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Version

/**
 * Created by chlee on 5/1/24.
 *
 * @author Changha Lee
 * @version opt-lock
 * @since opt-lock
 */
@Entity
class ReportVersion (
    @Id
    val reportKey: Long,
    @Version
    var version: Long,
    private var snapshotId: UUID,
) {
    fun update() {
        this.snapshotId = UUID.randomUUID()
    }
}
