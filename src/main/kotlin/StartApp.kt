package startApp

import entity.Movie
import entity.MovieShow
import packageDAO.MovieDAO
import packageDAO.MovieShowDAO
import java.time.LocalDateTime
import kotlin.system.exitProcess

// класс который обрабатывает запросы пользователя
class StartApp {

    companion object {
        private val movieDAO = MovieDAO()
        private val movieShowDAO = MovieShowDAO()

        // список всех действий, которые можно обработать
        val actionList = listOf(
            Action("Продажа билета", action = { MovieShowService.sellTicket() }),
            Action("Возврат билета", action = { MovieShowService.refundTicket() }),
            Action("Показать свободные и забронированные места", action = { showPlaces() }),
            Action("Добавить новый фильм", action = { addNewMovie() }),
            Action("Удалить фильм", action = { removeMovie() }),
            Action("Добавить новый киносеанс", action = { MovieShowService.addMovieShow() }),
            Action("Удалить киносеанс", action = { MovieShowService.removeMovieShow() }),
            Action("Выход", action = { exit() })
        )

        private fun readEnter() {
            val pressEnterString: String = "Нажмите Enter чтобы вернуться в главное меню. "

            print(pressEnterString)

            readln()
        }

        fun run() {
            var actionNumber: Int
            while (true) {
                println("Выберите следующее действие из показанных ниже: ")

                for (i in actionList.indices) {
                    println("${i + 1}. ${actionList[i]}.")
                }

                print("Введите номер вашего действия: ")


                try {
                    actionNumber = readln().toInt()
                    processAction(actionNumber)
                } catch (numEx: NumberFormatException) {
                    println(numEx.message)
                }
            }
        }

        private fun processAction(actionNumber: Int) {

            if (actionNumber <= 0 || actionNumber > actionList.size) {
                throw NumberFormatException("Неверный номер действия : $actionNumber.")
            }

            actionList[actionNumber - 1].action.invoke()
        }

        // эта функция показывает места в кинозале
        private fun showPlaces() {
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

            println("Выберите номер киносеанса для возврата билета ниже:")

            for (i in optionsList.indices) {
                println("${i}. ${optionsList[i]}.")
            }

            print("Введите номер киносеанса для возврата билета:")

            val actionNumber: Int

            try {
                actionNumber = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println("Неверный номер действия.")

                readEnter()
                return
            }

            if (actionNumber < 0 || actionNumber >= optionsList.size) {
                println("Неверный номер действия.")

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

            println("Печать мест из $selectedMovieShow.")

            for (i in seatsForShow.indices) {
                val curSeatStatus = if (seatsForShow[i]) "КУПЛЕНО" else "СВОБОДНО"

                println("${i + 1}. $curSeatStatus.")
            }

            readEnter()
        }

        private fun addNewMovie() {
            print("Введите название фильма: ")

            val movieName = readln()

            if (movieName.isEmpty()) {
                println("Название фильма пустое.")

                readEnter()
                return
            }

            print("Введите год фильма: ")

            val movieYear: Int
            try {
                movieYear = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println("Неправильный формат года фильма.")

                readEnter()
                return
            }

            val movie = Movie(movieName, movieYear)

            try {
                movieDAO.add(movie)
                println("$movie был успешно добавлен.")

                readEnter()
                return
            } catch (ex: Exception) {
                println(ex.message)

                readEnter()
                return
            }
        }

        private fun removeMovie() {
            val movieList: MutableList<Movie>
            try {
                movieList = movieDAO.getData().toMutableList()
            } catch (runtimeEx: RuntimeException) {
                println(runtimeEx.message)
                println("Удаление отменено.")
                print("Нажмите Enter, чтобы вернуться в главное меню.")

                readln()
                return
            }

            val optionsList: List<String> = mutableListOf("Отменить удаление") + movieList.map { it.toString() }

            println("Выберите номер фильма для удаления ниже: ")

            for (i in optionsList.indices) {
                println("${i}. ${optionsList[i]}.")
            }

            print("Введите номер фильма для удаления: ")

            val actionNumber: Int

            try {
                actionNumber = readln().toInt()
            } catch (numEx: NumberFormatException) {
                println("Неверный номер действия.")

                readEnter()
                return
            }

            if (actionNumber < 0 || actionNumber >= optionsList.size) {
                println("Неверный номер действия.")

                readEnter()
                return
            }

            if (actionNumber == 0) {
                println("Удаление отменено.")

                readEnter()
                return
            }

            val movieForRemove = movieList[actionNumber - 1]

            movieDAO.remove(movieForRemove)

            println("$movieForRemove был успешно удален.")

            readEnter()
        }

        private fun exit() {
            exitProcess(0)
        }
    }
}