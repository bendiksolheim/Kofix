package kofix

import kotlin.random.Random

fun Byte.Companion.generator(random: Random): Sequence<Byte> = sequence {
    while (true) {
        yield(random.nextBytes(1)[0])
    }
}

fun UByte.Companion.generator(random: Random): Sequence<UByte> = sequence {
    while (true) {
        yield(random.nextBytes(1)[0].toUByte())
    }
}

fun Short.Companion.generator(from: Short = 0, until: Short = MAX_VALUE): (random: Random) -> Sequence<Short> =
    { random ->
        sequence {
            while (true) {
                yield(random.nextInt(from.toInt(), until.toInt()).toShort())
            }
        }
    }

fun UShort.Companion.generator(from: UShort = 0u, until: UShort = MAX_VALUE): (random: Random) -> Sequence<UShort> =
    { random ->
        sequence {
            while (true) {
                yield(random.nextInt(from.toInt(), until.toInt()).toUShort())
            }
        }
    }

fun Int.Companion.generator(from: Int = 0, until: Int = 1000): (random: Random) -> Sequence<Int> = { random ->
    sequence {
        while (true) {
            yield(random.nextInt(from, until))
        }
    }
}

fun UInt.Companion.generator(from: UInt = 0u, until: UInt = 1000u): (random: Random) -> Sequence<UInt> = { random ->
    sequence {
        while (true) {
            yield(random.nextInt(from.toInt(), until.toInt()).toUInt())
        }
    }
}

fun Long.Companion.generator(from: Long = 0, until: Long = 1000): (random: Random) -> Sequence<Long> = { random ->
    sequence {
        while (true) {
            yield(random.nextLong(from, until))
        }
    }
}

fun ULong.Companion.generator(from: ULong = 0u, until: ULong = 1000u): (random: Random) -> Sequence<ULong> = { random ->
    sequence {
        while (true) {
            yield(random.nextLong(from.toLong(), until.toLong()).toULong())
        }
    }
}

fun Float.Companion.generator(): (random: Random) -> Sequence<Float> = { random ->
    sequence {
        while (true) {
            yield(random.nextFloat())
        }
    }
}

fun Double.Companion.generator(): (random: Random) -> Sequence<Double> = { random ->
    sequence {
        while (true) {
            yield(random.nextDouble())
        }
    }
}

fun String.Companion.generator(
    minLength: Int = 0,
    maxLength: Int = 20,
    characterSet: List<Char> = letters
): (random: Random) -> Sequence<String> =
    { random ->
        sequence {
            while (true) {
                val length = random.nextInt(minLength, maxLength + 1)
                yield(CharArray(length) { characterSet.random(random) }.joinToString(""))
            }
        }
    }

fun Char.Companion.generator(characterSet: List<Char> = letters): (random: Random) -> Sequence<Char> =
    { random ->
        sequence {
            while (true) {
                yield(lettersAndNumbers.random(random))
            }
        }
    }

fun Boolean.Companion.generator(random: Random): Sequence<Boolean> = sequence {
    while (true) {
        yield(random.nextBoolean())
    }
}

fun String.Companion.name(random: Random): Sequence<String> = sequence {
    while (true) {
        yield(firstNames.random(random) + " " + lastNames.random(random))
    }
}

private val firstNames = listOf(
    "Jan",
    "Per",
    "Bjørn",
    "Ole",
    "Kjell",
    "Lars",
    "Arne",
    "Knut",
    "Svein",
    "Hans",
    "Odd",
    "Thomas",
    "Geir",
    "Tor",
    "Terje",
    "Morten",
    "Anne",
    "Inger",
    "Kari",
    "Marit",
    "Liv",
    "Ingrid",
    "Eva",
    "Astrid",
    "Solveig",
    "Berit",
    "Bjørg",
    "Randi",
    "Hilde",
    "Marianne",
    "Nina",
    "Elisabeth"
)

private val lastNames = listOf(
    "Hansen",
    "Johansen",
    "Olsen",
    "Larsen",
    "Andersen",
    "Pedersen",
    "Nilsen",
    "Kristiansen",
    "Jensen",
    "Karlsen",
    "Johnsen",
    "Pettersen",
    "Eriksen",
    "Berg",
    "Haugen",
    "Hagen",
    "Johannessen",
    "Andreassen",
    "Jacobsen",
    "Halvorsen",
    "Dahl",
    "Jørgensen",
    "Henriksen"
)

private val letters: List<Char> = ('A'..'Z') + ('a'..'z')
private val lettersAndNumbers: List<Char> = ('A'..'Z') + ('a'..'z') + ('0'..'9')
