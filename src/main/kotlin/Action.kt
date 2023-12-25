package startApp

// класс данных, который представляет имя действия и само действие
data class Action(val actionName: String, val action: () -> Unit) {
    override fun toString(): String {
        return actionName
    }
}