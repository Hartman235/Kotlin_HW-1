package packageDAO

import entity.Entity
import entity.Movie

// объект доступа к данным для объектов фильма
class MovieDAO : EntityDAO() {

    private val moviesFilePath = "$dataDirectory/movies.csv"

    override fun getData(): List<Movie> {
        val resultList = getDataFromFile<Movie>(moviesFilePath)

        return resultList
    }

    // сохранение данного списка фильмов в файл CSV
    override fun saveData(entities: List<Entity>) {
        saveDataToFile<Movie>(entities, moviesFilePath)
    }
}