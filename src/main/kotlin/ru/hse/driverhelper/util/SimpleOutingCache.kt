package ru.hse.driverhelper.util

import java.time.Duration
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

// class SimpleOutingCache<K : Any, V : Any>(
//     private val map: ConcurrentHashMap<K, ValueWithDate<V>> = ConcurrentHashMap<K, ValueWithDate<V>>(),
//     private val expirationDuration : Duration = Duration.ofMinutes(10),
//     private val
// ) {
//     fun getOrPut(key: K, factory: Function<K, V?>): V? = map[key]?.takeIf { Date().before(it.date }.value ?: factory.apply(key)?.also { map[key] = it }
//
//     data class ValueWithDate<V : Any>(val value: V, val date: Date)
// }