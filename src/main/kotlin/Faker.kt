package org.example

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor

class Faker<T>(
    val constructor: (random: Random, overrides: Map<String, Overrider>) -> T,
    private val overrides: Map<String, Overrider> = mapOf()
) {
    private val logger = KotlinLogging.logger {}

    fun generate(random: Random): Sequence<T> = sequence {
        while (true) {
            val instance = constructor(random, overrides)
            yield(instance)
        }
    }

    fun <K, U> specify(prop: KProperty1<T, K>, value: U): Faker<T> where U: Comparable<K> {
        return Faker(constructor, overrides + (prop.name to SequenceOverrider(sequenceOf(value))))
    }

    fun <K, U> specify(prop: KProperty1<T, K>, value: Sequence<U>): Faker<T> where U: Comparable<K> {
        return Faker(constructor, overrides + (prop.name to SequenceOverrider(value)))
    }

    fun <K, U> specify(prop: KProperty1<T, K>, value: (Random) -> Sequence<U>): Faker<T> where U: Comparable<K> {
        return Faker(constructor, overrides + (prop.name to FunctionOverrider(value)))
    }

    companion object
}

inline fun <reified T : Any> Faker.Companion.fake(): Faker<T> = fakerHelper( T::class )

private val logger = KotlinLogging.logger ("fakerHelper")
fun <T: Any> fakerHelper(t: KClass<T>): Faker<T> {
    logger.debug { "Class: ${t.qualifiedName}" }
    val constructor = t.primaryConstructor ?: throw RuntimeException("No primary constructor found for type [${t.qualifiedName}]")
    val constructorParams: List<Pair<String, (Random) -> Sequence<Any>>> = constructor.parameters.map { param ->
        logger.debug { "└─ ${param.name}: ${param.type}"}
        val generator = when (param.type.classifier) {
            // Numbers
            Byte::class -> Byte::generator
            UByte::class -> UByte::generator
            Short::class -> Short.generator()
            UShort::class -> UShort.generator()
            Int::class -> Int.generator()
            UInt::class -> UInt.generator()
            Long::class -> Long.generator()
            ULong::class -> ULong.generator()
            Float::class -> Float.generator()
            Double::class -> Double.generator()

            // String types
            String::class -> String.generator()
            Char::class -> Char.generator()

            // Boolean
            Boolean::class -> Boolean::generator

            // TODO: handle arrays

            // Others are complex types and must be handled recursively
//            else -> throw RuntimeException("Unhandled type $it")
            else -> {
                throw NotImplementedError("Unhandled type ${param.type}")
//                { random -> fakerHelper(param::class).generate(random) }
            }
        }
        Pair(param.name!!, generator)
    }

    return Faker(
        { random: Random, overrides: Map<String, Overrider> ->
            constructor.call(
                *constructorParams.map {
                    overrides[it.first]?.let { override ->
                        when (override) {
                            is SequenceOverrider<*> -> override.sequence.first()
                            is FunctionOverrider<*> -> override.function(random).first()
                        }
                    } ?: it.second(random).first()
                }.toTypedArray()
            )
        }
    )
}

sealed class Overrider
data class SequenceOverrider<T>(val sequence: Sequence<T>) : Overrider()
data class FunctionOverrider<T>(val function: (Random) -> Sequence<T>) : Overrider()
