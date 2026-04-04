package org.jetbrains.compose.resources

import androidx.compose.runtime.Immutable

/**
 * Represents a quantity string resource in the application.
 *
 * @param id The unique identifier of the resource.
 * @param key The key used to retrieve the string resource.
 * @param items The set of resource items associated with the string resource.
 */
@Immutable
class PluralStringResource
@InternalResourceApi constructor(id: String, val key: String, items: Set<ResourceItem>) : Resource(id, items)

// TODO: Finish implementation