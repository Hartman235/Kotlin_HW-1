package entity

data class Movie(val name: String, val year: Int) : Entity() {

    override fun toString(): String {
        return "Фильм : $name, год $year"
    }

    // функция, отвечающая за сериализацию CSV
    override fun toCSV(): String {
        return "$name, $year"
    }
}