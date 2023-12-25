package entity

import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.typeOf

abstract class Entity {
    companion object {

        // создание объекта определенного типа из строки CSV
        inline fun <reified TEntity> fromCsv(csvString: String): TEntity where TEntity : Entity {
            val args: List<String> = csvString.split(", ").filter { it.isNotEmpty() }

            val exception = RuntimeException("Невозможно преобразовать строку $csvString в ${typeOf<TEntity>()}.")

            if (typeOf<TEntity>() == typeOf<Movie>()) {
                if (args.size != 2) {
                    throw exception
                }

                try {
                    val movieName = args[0]
                    val movieYear = args[1].toInt()

                    return Movie(movieName, movieYear) as TEntity
                } catch (numEx: NumberFormatException) {
                    throw exception
                }
            }

            if (typeOf<TEntity>() == typeOf<MovieShow>()) {
                if (args.size != 5) {
                    throw exception
                }

                try {
                    val movieName = args[0]
                    val movieYear = args[1].toInt()

                    val movie = Movie(movieName, movieYear)

                    val dateFormatter = MovieShow.dateFormatter
                    val timeFormatter = MovieShow.timeFormatter

                    val movieShowDate = LocalDate.parse(
                        args[2],
                        dateFormatter
                    )

                    val movieShowStartTime = LocalTime.parse(
                        args[3],
                        timeFormatter
                    )


                    val boughtSeats = args[4].split("-")
                        .map { it.toBoolean() }

                    return MovieShow(
                        movie, movieShowDate, movieShowStartTime,
                        boughtSeats.toMutableList()
                    )
                            as TEntity
                } catch (ex: NumberFormatException) {
                    throw exception
                }
            }

            throw exception
        }
    }

    abstract fun toCSV(): String
}