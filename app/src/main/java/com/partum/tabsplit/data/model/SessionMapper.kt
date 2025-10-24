package com.partum.tabsplit.data.model

import com.partum.tabsplit.data.api.SessionOwner
import com.partum.tabsplit.data.api.SessionWithOwner
import java.util.Date

fun Session.toSessionWithOwner(owner: SessionOwner? = null): SessionWithOwner {
    return SessionWithOwner(
        id = id,
        title = title,
        description = description,
        currency = currency,
        createdAt = createdAt.toString(),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        owner = owner ?: SessionOwner(
            id = createdBy,
            username = "Unknown",
            zaddr = "",
            userId = createdBy
        )
    )
}

fun Session.toFullSession(owner: SessionOwner? = null): FullSession {
    return FullSession(
        id = id,
        title = title,
        description = description,
        currency = currency,
        createdAt = Date(createdAt.toString()),
        createdBy = createdBy,
        owner = owner,
        inviteCode = inviteCode,
        inviteUrl = inviteUrl,
        qrDataUrl = qrDataUrl,
        startDateTime = startDateTime,
        endDateTime = endDateTime
    )
}

fun SessionWithOwner.toFullSession(): FullSession {
    return FullSession(
        id = id,
        title = title,
        description = description,
        currency = currency,
        createdAt = Date(createdAt),
        createdBy = owner.userId,
        owner = owner,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
    )
}