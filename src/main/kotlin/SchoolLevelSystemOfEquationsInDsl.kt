fun main() {
    solver {
        config {
            this["model"] = "true"
        }

        val x = s("x")
        val y = s("y")
        val z = s("z")

        add(3*x + 2*y - z `=` 1)
        add(2*x - 2*y + 4*z `=` -2)
        add(-x + fr(1,2)*y - z `=` 0)

    }.check {
        println(it)
    }.onSuccess{ model, context ->
        println(model)
    }
}