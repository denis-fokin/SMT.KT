fun main() {
    solver {
        config {
            this["model"] = "true"
        }

        val (circle, square, triangle) = s("circle", "square", "triangle")

        add(circle + circle `=` 10)
        add(circle * square + square `=` 12)
        add(circle * square- triangle * circle `=` circle)

    }.check {
        println(it)
    }.onSuccess{ model, context ->
        println(model)
    }
}


