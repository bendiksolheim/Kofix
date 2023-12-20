@file:Suppress("UNCHECKED_CAST")

package faker

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.primaryConstructor

class Faker<T>(
    val constructor: (random: Random, overrides: Map<String, Overrider>) -> T,
    private val overrides: Map<String, Overrider> = mapOf()
) {
    fun generate(random: Random): Sequence<T> = sequence {
        while (true) {
            val instance = constructor(random, overrides)
            yield(instance)
        }
    }

    fun <K, U> set(prop: KProperty1<T, K>, value: U): Faker<T> where U : Comparable<K> {
        return Faker(constructor, overrides + (prop.name to FunctionOverrider { sequenceOf(value) }))
    }

    fun <K, U> set(prop: KProperty1<T, K>, value: Sequence<U>): Faker<T> where U : Comparable<K> {
        return Faker(constructor, overrides + (prop.name to FunctionOverrider { value }))
    }

    fun <K, U> set(prop: KProperty1<T, K>, value: (Random) -> Sequence<U>): Faker<T> where U : Comparable<K> {
        return Faker(constructor, overrides + (prop.name to FunctionOverrider(value)))
    }

    fun <K> set(prop: KProperty1<T, K>, faker: Faker<K>): Faker<T> {
        return Faker(constructor, overrides + (prop.name to FakerOverrider(faker)))
    }

    companion object
}

inline fun <reified T : Any> fake(): Faker<T> = fakerHelper(T::class, typeOf<T>())
fun fakerHelper(t: KType): Faker<*> = fakerHelper(t.classifier as KClass<*>, t)
fun <T : Any> fakerHelper(t: KClass<T>, type: KType): Faker<T> {
    logger.trace { "Class: ${t.simpleName}" }

    val builtIn = makeBuiltInInstance(t, type)
    if (builtIn != null) {
        return Faker({ random, _ ->
            builtIn(random).first() as T
        })
    }

    if (t.java.isEnum) {
        val enumValues = Class.forName(t.qualifiedName).enumConstants
        return Faker({ random, _ ->
            enumValues.random(random) as T
        })
    }

    logger.trace { "Parameter is complex" }
    val constructor = primaryConstructorOrThrow(t)
    val constructorParamGenerators = constructor.parameters.map { param ->
        logger.trace { "└─ ${param.name}: ${param.type}" }
        val generator = fakerHelper(param.type)
        Pair(param.name!!, generator)
    }

    return Faker(
        { random: Random, overrides: Map<String, Overrider> ->
            val params = constructorParamGenerators.map {
                overrides[it.first]?.let { override ->
                    when (override) {
                        is FunctionOverrider<*> -> override.function(random).first()
                        is FakerOverrider<*> -> override.faker.generate(random).first()
                    }
                } ?: it.second.generate(random).first()
            }.toTypedArray()
            constructor.call(*params)
        }
    )
}

private fun <T : Any> primaryConstructorOrThrow(t: KClass<T>) = (t.primaryConstructor
    ?: throw RuntimeException("No primary constructor found for type [${t.qualifiedName}]"))

fun makeBuiltInInstance(cls: KClass<*>, type: KType): Generator? = when (cls) {
    Any::class -> { _ -> sequenceOf("Any") }
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

    List::class -> makeList(type)
    Set::class -> makeSet(type)
    Map::class -> makeMap(type)

    else -> null
}

fun makeList(type: KType): (Random) -> Sequence<*> {
    logger.trace { "Creating list instance: $type" }
    val collectionContentType = type.arguments.first().type!!
    val generator = fakerHelper(collectionContentType)
    return { random ->
        listOf(generator.generate(random).take(random.nextInt(0, 10)).toList()).asSequence()
    }
}

fun makeSet(type: KType): (Random) -> Sequence<*> {
    logger.trace { "Creating set instance: $type" }
    val collectionContentType = type.arguments.first().type!!
    val generator = fakerHelper(collectionContentType)
    return { random ->
        listOf(generator.generate(random).take(random.nextInt(0, 10)).toSet()).asSequence()
    }
}

fun makeMap(type: KType): (Random) -> Sequence<*> {
    logger.trace { "Creating map instance" }
    val keys = fakerHelper(type.arguments[0].type!!)
    val values = fakerHelper(type.arguments[1].type!!)
    return ({ random ->
        sequenceOf(keys.generate(random).zip(values.generate(random)).take(random.nextInt(0, 10)).toMap())
    })
}

sealed class Overrider
data class FunctionOverrider<T>(val function: (Random) -> Sequence<T>) : Overrider()
data class FakerOverrider<T>(val faker: Faker<T>) : Overrider()

private val logger = KotlinLogging.logger("fakerHelper")

typealias Generator = (Random) -> Sequence<*>
