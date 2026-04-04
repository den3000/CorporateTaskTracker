package org.jetbrains.compose.resources

import androidx.compose.runtime.Immutable

/**
 * Represents a font resource.
 *
 * @param id The identifier of the font resource.
 * @param items The set of resource items associated with the font resource.
 *
 * @see Resource
 */
@Immutable
class FontResource
@InternalResourceApi constructor(id: String, items: Set<ResourceItem>) : Resource(id, items)

// TODO: Finish implementation