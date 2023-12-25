package entity

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

// неизменяемый класс данных, который представляет киносеанс
data class MovieShow(
    val movie: Movie, val date: LocalDate,
    val startTime: LocalTime,
    private val _seats: MutableList<Boolean>
) : Entity() {
    // ЛОЖЬ = свободно (никто не купил билет на это место)
    // TRUE = кто-то купил билет в это место
    val seats: List<Boolean>
        get() = _seats

    constructor(movie: Movie, date: LocalDate, startTime: LocalTime) :
            this(
                movie, date, startTime,
                MutableList<Boolean>(SEATS_COUNT, { false })
            )

    companion object {
        // форматировщик даты, необходимый для преобразования киношоу с анализом строки CSV
        val dateFormatter: DateTimeFormatter
            get() = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.US)

        private const val SEATS_COUNT = 20

        // форматировщик времени, необходимый для преобразования фильма в строку CSV,
        // её анализа и получения из неё фильма
        val timeFormatter: DateTimeFormatter
            get() = DateTimeFormatter.ofPattern("hh-mm-a")
    }

    val startDateTime: LocalDateTime
        get() = LocalDateTime.of(date, startTime)

    override fun toString(): String {
        val consoleDateFormatter = DateTimeFormatter
            .ofPattern("dd MMMM yyyy")
        val dateForConsole = date.format(consoleDateFormatter)

        return "$movie, Дата = $dateForConsole, " +
                "Время начала = $startTime"
    }

    override fun toCSV(): String {
        val formattedDate = date.format(dateFormatter)
        val formattedTime = startTime.format(timeFormatter)

        val boughtSeatsString = _seats.joinToString("-")
        return "${movie.toCSV()}, $formattedDate, $formattedTime, $boughtSeatsString"
    }

    fun buyTicketAt(index: Int) {
        if (_seats[index]) {
            throw RuntimeException("Билет на место номер ${index + 1} уже куплен.")
        }

        _seats[index] = true
    }

    fun refundTicketAt(index: Int) {
        if (!_seats[index]) {
            throw RuntimeException("Билет на место номер ${index + 1} ещё не куплен.")
        }

        _seats[index] = false
    }

    // мы сравниваем только по фильму и дате начала,
    // чтобы каждый киносеанс подходил только для одного места размещения
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        return (other is MovieShow && this.movie == other.movie &&
                this.startDateTime == other.startDateTime)
    }
}