package com.mr3y.podcaster.ui.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.serializer

@ExperimentalSerializationApi
inline fun <reified T : Destinations> createRoutePattern(): String = createRoutePattern(serializer<T>())

/**
 * Converts a Destination to a route pattern.
 *
 * Utilize the generic variant of this function.
 */
@ExperimentalSerializationApi
fun <T : Destinations> createRoutePattern(serializer: KSerializer<T>): String {
    val destination = createRouteSlug(serializer)
    if (serializer.descriptor.elementsCount == 0) {
        return destination
    }

    val path = StringBuilder()
    val query = StringBuilder()
    for (i in 0 until serializer.descriptor.elementsCount) {
        val name = serializer.descriptor.getElementName(i)
        if (serializer.descriptor.isNavTypeOptional(i)) {
            query.append("&$name={$name}")
        } else {
            path.append("/{$name}")
        }
    }
    if (query.isNotEmpty()) {
        query[0] = '?'
    }

    return destination + path.toString() + query.toString()
}

@ExperimentalSerializationApi
internal fun createRouteSlug(serializer: KSerializer<*>): String =
    serializer.descriptor.serialName

/**
 * Optional parameter is if:
 * - there is a default value for the property
 * - property is nullable -> it has to be modelled as a missing query parameter
 * - property is a String that can be empty -> it has to be modelled as a query parameter
 */
@ExperimentalSerializationApi
internal fun SerialDescriptor.isNavTypeOptional(index: Int): Boolean =
    isElementOptional(index) ||
            getElementDescriptor(index).let {
                it.isNullable || it.kind == PrimitiveKind.STRING
            }
