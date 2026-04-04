package org.jetbrains.compose.resources

import androidx.compose.runtime.Immutable

/**
 * Represents a string array resource in the application.
 *
 * @param id The unique identifier of the resource.
 * @param key The key used to retrieve the string array resource.
 * @param items The set of resource items associated with the string array resource.
 */
@Immutable
class StringArrayResource
@InternalResourceApi constructor(id: String, val key: String, items: Set<ResourceItem>) : Resource(id, items)

// TODO: Finish implementation