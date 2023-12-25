package startApp

import entity.MovieShow
import packageDAO.MovieDAO
import packageDAO.MovieShowDAO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MovieShowService {
    companion object {
        private val movieDAO = MovieDAO()
        private val movieShowDAO = MovieShowDAO()

        fun addMovieShow() {
            println("Выберите фильм из представленных ниже:")

            val movieList = movieDAO.getData()

            if (movieList.isEmpty()) {
                println("В базу данных не добавлено ни одного фильма.")
                println("Добавление киносеанса отменено.")

                readEnter()
                return
            }

            for (i in movieList.indices) {
                println("${i + 1}. ${movieList[i]}.")
            }

            print("Введите номер фильма для добавления киносеанса: ")

            val movieNumber: Int
            try {
                movieNumber = readln().toInt()


            } catch (numEx: NumberFormatException) {
                println("Неверный номер фильма.")

                readEnter()
                return
            }

            if (movieNumber <= 0 || movieNumber > movieList.size) {
                println("Номер фильма слишком большой или слишком маленький: $movieNumber.")

                readEnter()
                return
            }

            val movie = movieList[movieNumber - 1]

            print("Введите дату показа фильма. Формат дд: мм: гггг. ")
            val localDate: LocalDate

            try {
                val dateString = readln()

                val dateArgs = dateString.split(" ", ":", ",", ".", ";").filter { it.isNotEmpty() }

                if (dateArgs.size > 3) {
                    throw RuntimeException("Слишком много аргументов для даты.")
                }

                val dayOfMonth: Int = dateArgs[0].toInt()
                val month: Int = dateArgs[1].toInt()
                val year: Int = dateArgs[2].toInt()

                localDate = LocalDate.of(year, month, dayOfMonth)
            } catch (runEx: RuntimeException) {
                println(runEx.message)
                println("Неверный формат данных.")

                readEnter()
                return
            }

            print("Введите время начала. Формат: чч: мм. ")

            val localTime: LocalTime

            try {
                val timeString: String = readln()
                val timeArgs = timeString.split(" ", ":", ",", ";", ".").filter { it.isNotEmpty() }

                if (timeArgs.size > 2) {
                    throw RuntimeException("Слишком много аргументов для времени.")
                }

                val hours: Int = timeArgs[0].toInt()
                val minutes: Int = timeArgs[1].toInt()

                localTime = LocalTime.of(hours, minutes)
            } catch (runEx: RuntimeException) {
                println(runEx.message)
                println("Невозможно создать местное время.")

                readEnter()
                return
            }

            val movieShow = MovieShow(movie, localDate, localTime)

            try {
                movieShowDAO.add(movieShow)
                println("$movieShow был успешно добавлено.")

                readEnter()
            } catch (ex: Exception) {
                println(ex.message)
                print("Нажмите Enter, чтобы вернуться в главное меню. ")

                readln()
                return
            }
        }

        fun removeMovieShow() {
            val movieShowList: MutableList<MovieShow>

            try {
                movieShowList = movieShowDAO.getData().toMutableList()
            } catch (runEx: RuntimeException) {
                println(runEx.message)
                println("Удаление отменено.")

                readEnter()
                return
            }

            val optionsList: List<String> = mutableListOf("Отменить удаление") + movieShowList.map { it.toString() }

            println("Выберите снизу номер киносеанса, который хотите удалить:")

            for (i in optionsList.indices) {
                println("${i}. ${optionsList[i]}.")
            }

            print("Введите номер киносеанса для удаления : ")

            val actionNumber: Int

            try {
                actionNumber = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println("Введено неверное действие.")

                readEnter()
                return
            }

            if (actionNumber < 0 || actionNumber >= optionsList.size) {
                println("Введено неверное действие.")

                readEnter()
                return
            }

            if (actionNumber == 0) {
                println("Удаление отменено.")
                println("Возврат в главное меню.")
                return
            }

            val movieShowForRemove = movieShowList[actionNumber - 1]

            movieShowDAO.remove(movieShowForRemove)

            println("Киношоу $movieShowForRemove было успешно удалено.")

            readEnter()
        }

        private fun readEnter() {
            val pressEnterString: String = "Нажмите Enter, чтобы вернуться в главное меню. "

            print(pressEnterString)

            readln()
        }

        fun sellTicket() {
            val movieShowList: MutableList<MovieShow>

            try {
                movieShowList = movieShowDAO.getData().toMutableList()
            } catch (runEx: RuntimeException) {
                println(runEx.message)
                println("Процесс продажи билета отменен.")

                readEnter()
                return
            }

            val optionsList: List<String> = mutableListOf("Отменить продажу") + movieShowList.map { it.toString() }

            println("Выберите снизу номер киносеанса, чтобы продать билет : ")

            for (i in optionsList.indices) {
                println("${i}. ${optionsList[i]}.")
            }

            print("Введите номер сеанса для продажи билета : ")

            val actionNumber: Int

            try {
                actionNumber = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println("Введено неверное действие.")

                readEnter()
                return
            }

            if (actionNumber < 0 || actionNumber >= optionsList.size) {
                println("Введено неверное действие.")

                readEnter()
                return
            }

            if (actionNumber == 0) {
                println("Процесс продажи билета отменен.")

                readEnter()
                return
            }

            val selectedMovieShow = movieShowList[actionNumber - 1]

            val curDateTime = LocalDateTime.now()
            val selectedDateTime = selectedMovieShow.startDateTime

            if (curDateTime > selectedDateTime) {
                println("Невозможно продать или вернуть билеты на $selectedDateTime.")
                println("Кинопоказ уже начался")

                readEnter()
                return
            }

            val seatsForShow = selectedMovieShow.seats

            println("Выберите места, на которые нужно купить билет (из указанных ниже).")

            for (i in seatsForShow.indices) {
                val curSeatStatus = if (seatsForShow[i]) "КУПЛЕНО" else "СВОБОДНО"

                println("${i + 1}. $curSeatStatus.")
            }

            print("Выберите места, на которое вы хотите купить билет: ")

            val seatNumber: Int
            try {
                seatNumber = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println(numEx.message)

                println("Процесс покупки билета отменяется.")

                readEnter()
                return
            }

            if (seatNumber <= 0 || seatNumber > seatsForShow.size) {
                println("Нет места с таким номером: $seatNumber.")
                println("Процесс покупки билета отменяется.")

                readEnter()
                return
            }

            val selectedSeatIsBought = seatsForShow[seatNumber - 1]

            if (selectedSeatIsBought) {
                println("Место номер $seatNumber уже куплено.")

                println("Процесс покупки билета отменяется.")

                readEnter()
                return
            }

            selectedMovieShow.buyTicketAt(seatNumber - 1)

            movieShowDAO.saveData(movieShowList)

            println("Билет на место $seatNumber был успешно куплен.")

            readEnter()
        }

        fun refundTicket() {
            val movieShowList: MutableList<MovieShow>

            try {
                movieShowList = movieShowDAO.getData().toMutableList()
            } catch (runEx: RuntimeException) {
                println(runEx.message)
                println("Процесс возврата билета отменен.")

                readEnter()
                return
            }

            val optionsList: List<String> =
                mutableListOf("Отменить возврат средств") + movieShowList.map { it.toString() }

            println("Выберите ниже номер киносеанса для возврата билета :")

            for (i in optionsList.indices) {
                println("${i}. ${optionsList[i]}.")
            }

            print("Введите номер киносеанса для возврата билета : ")

            val actionNumber: Int

            try {
                actionNumber = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println("Введено неверное действие.")

                readEnter()
                return
            }

            if (actionNumber < 0 || actionNumber >= optionsList.size) {
                println("Введено неверное действие.")

                readEnter()
                return
            }

            if (actionNumber == 0) {
                println("Процесс возврата билета отменен.")

                readEnter()
                return
            }

            val selectedMovieShow = movieShowList[actionNumber - 1]

            val curDateTime = LocalDateTime.now()
            val selectedDateTime = selectedMovieShow.startDateTime

            if (curDateTime > selectedDateTime) {
                println("Невозможно продать или вернуть билеты на $selectedDateTime.")
                println("Кинопоказ уже начался.")

                readEnter()
                return
            }

            val seatsForShow = selectedMovieShow.seats

            println(
                "Выберите номера мест для возврата билета " + "(из показанных ниже)."
            )

            for (i in seatsForShow.indices) {
                val curSeatStatus = if (seatsForShow[i]) "КУПЛЕНО" else "СВОБОДНО"

                println("${i + 1}. $curSeatStatus.")
            }

            print("Выберите место, за которое хотите вернуть деньги за билет: ")

            val seatNumber: Int
            try {
                seatNumber = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println(numEx.message)

                println("Процесс возврата билета отменяется.")

                readEnter()
                return
            }

            if (seatNumber <= 0 || seatNumber > seatsForShow.size) {
                println("Нет места с таким номером: $seatNumber.")
                println("Процесс возврата билета отменяется.")

                readEnter()
                return
            }

            val selectedSeatIsBought = seatsForShow[seatNumber - 1]

            if (!selectedSeatIsBought) {
                println("Место номер $seatNumber ещё не куплено.")

                println("Процесс возврата билета отменяется.")

                readEnter()
                return
            }

            selectedMovieShow.refundTicketAt(seatNumber - 1)

            movieShowDAO.saveData(movieShowList)

            println("Билет на место $seatNumber был успешно возвращен.")

            readEnter()
        }
    }
}