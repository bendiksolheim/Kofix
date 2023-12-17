package faker

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.random.Random
import kotlin.reflect.*
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

    fun <K, U> set(prop: KProperty1<T, K>, value: U): Faker<T> where U : Comparable<K> {
        return Faker(constructor, overrides + (prop.name to SequenceOverrider(sequenceOf(value))))
    }

    fun <K, U> set(prop: KProperty1<T, K>, value: Sequence<U>): Faker<T> where U : Comparable<K> {
        return Faker(constructor, overrides + (prop.name to SequenceOverrider(value)))
    }

    fun <K, U> set(prop: KProperty1<T, K>, value: (Random) -> Sequence<U>): Faker<T> where U : Comparable<K> {
        return Faker(constructor, overrides + (prop.name to FunctionOverrider(value)))
    }

    fun <K> set(prop: KProperty1<T, K>, faker: Faker<K>): Faker<T> {
        return Faker(constructor, overrides + (prop.name to FakerOverrider(faker)))
    }

    companion object
}

inline fun <reified T : Any> fake(): Faker<T> = fakerHelper(T::class)

private val logger = KotlinLogging.logger("fakerHelper")
fun <T : Any> fakerHelper(t: KClass<T>): Faker<T> {
    logger.trace { "Class: ${t.qualifiedName}" }
    val constructor =
        t.primaryConstructor ?: throw RuntimeException("No primary constructor found for type [${t.qualifiedName}]")
    val constructorParams: List<Pair<String, (Random) -> Sequence<Any>>> = constructor.parameters.map { param ->
        logger.trace { "└─ ${param.name}: ${param.type}" }
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

            List::class -> {
                val collectionContentType = param.type.arguments.first().type!!.classifier
                val generator = basicTypes[collectionContentType]!!
                ({ random -> listOf(generator(random).take(random.nextInt(0, 10)).toList()).asSequence() })
            }

            Map::class -> {
                val t1 = param.type.arguments[0].type!!.classifier
                val g1 = basicTypes[t1]!!
                val t2 = param.type.arguments[1].type!!.classifier
                val g2 = basicTypes[t2]!!
                ({ random ->
                    sequenceOf(g1(random).zip(g2(random)).take(random.nextInt(0, 10)).toMap())
                })
            }

            // Others are complex types and must be handled recursively
            else -> {
                { random ->
                    fakerHelper(
                        param.type.classifier as? KClass<*>
                            ?: throw RuntimeException("Parameter is not a KClass, should probably not end up here?")
                    ).generate(random)
                }
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
                            is FakerOverrider<*> -> override.faker.generate(random).first()
                        }
                    } ?: it.second(random).first()
                }.toTypedArray()
            )
        }
    )
}

val basicTypes: Map<KClassifier, (Random) -> Sequence<*>> = mapOf(
    Byte::class to Byte::generator,
    UByte::class to UByte::generator,
    Short::class to Short.generator(),
    UShort::class to UShort.generator(),
    Int::class to Int.generator(),
    UInt::class to UInt.generator(),
    Long::class to Long.generator(),
    ULong::class to ULong.generator(),
    Float::class to Float.generator(),
    Double::class to Double.generator(),

    // String types
    String::class to String.generator(),
    Char::class to Char.generator(),

    // Boolean
    Boolean::class to Boolean::generator
)

sealed class Overrider
data class SequenceOverrider<T>(val sequence: Sequence<T>) : Overrider()
data class FunctionOverrider<T>(val function: (Random) -> Sequence<T>) : Overrider()

data class FakerOverrider<T>(val faker: Faker<T>) : Overrider()
