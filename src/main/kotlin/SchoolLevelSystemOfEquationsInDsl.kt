fun main() {
    solver {
        config {
            this["model"] = "true"
        }

        val x = intVar("x")
        val y = intVar("y")
        val z = intVar("z")

        add(3*x + 2*y - z `=` 1)
        add(2*x - 2*y + 4*z `=` -2)
        add(-x + fr(1,2)*y - z `=` 0)

    }.check {
        println(it)
    }.onSuccess{ model, context ->
        println(model)
    }
}