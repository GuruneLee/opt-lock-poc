package com.gurunelee.optlock.converters

import javax.persistence.AttributeConverter

/**
 * Created by chlee on 5/1/24.
 *
 * @author Changha Lee
 * @version opt-lock
 * @since opt-lock
 */
class StringBooleanConverter: AttributeConverter<Boolean?, String?> {
    override fun convertToDatabaseColumn(value: Boolean?): String? {
        return when (value) {
            true -> "Y"
            false -> "N"
            else -> null
        }
    }

    override fun convertToEntityAttribute(value: String?): Boolean? {
        return when (value) {
            "Y" -> true
            "N" -> false
            else -> null
        }
    }
}
