package packageDAO

import entity.Entity
import entity.MovieShow

// объект доступа к данным для киношоу
class MovieShowDAO : EntityDAO() {

    private val movieShowPath = "$dataDirectory/movieShows.csv"

    override fun getData(): List<MovieShow> {
        val dataFromFile = getDataFromFile<MovieShow>(movieShowPath)

        return dataFromFile
    }

    override fun saveData(entities: List<Entity>) {
        saveDataToFile<MovieShow>(entities, movieShowPath)
    }
}