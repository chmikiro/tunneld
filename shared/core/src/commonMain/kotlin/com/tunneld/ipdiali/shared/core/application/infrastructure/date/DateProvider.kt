package com.tunneld.ipdiali.shared.core.application.infrastructure.date

import kotlinx.datetime.LocalDateTime

interface DateProvider {
    fun now(): LocalDateTime
}
