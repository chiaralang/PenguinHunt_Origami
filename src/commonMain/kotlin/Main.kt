import EngineEmi.graphics.bild.Bild
import com.soywiz.korev.Key
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.collidesWith
import com.soywiz.korim.color.Colors
import me.emig.engineEmi.*

val engineConfig = EngineConfig(width = 1600, height = 750)

suspend fun main() = Engine(engineConfig) {

	val input = views.input

	val penguin = Player (100, 100, 20, 50, 0.0, Colors.RED)
	addChild(penguin)

    val spaceArray : DoubleArray =  doubleArrayOf(10.0, 50.0, 200.0)



    val iceFloes = arrayOf<IceFloe> ( IceFloe(100, 600, 1000, 50), IceFloe(1200, 600, 1000, 50) )
    addChild(iceFloes[0])
    addChild(iceFloes[1])

    var iceFloesVx = 5.0

	val ay = 100.0 //Beschleunigung in y-Richtung -> ≙ Erdanziehung g
	val ax = -10.0 //Beschleunigung in x-Richtung -> Schwierigkeitsgrad erhöhen

    fun move(p: Double, v: Double, a: Double, dt: Double) : Pair<Double, Double> {
        val vAlt = v
        val vNeu = v + (a)*dt

        val pNeu = p + 0.5*(vAlt + vNeu) * dt

        return Pair(pNeu, vNeu)
    }

    fun collidesWith(player : Player, iceFloe : IceFloe) : Boolean {
        return (player.y + player.height >= iceFloe.y &&
                player.x < iceFloe.x + iceFloe.width &&
                player.x > iceFloe.x)

    }

	addUpdater { dt ->
		//iceFloes werden in x Richtung mit ax beschleunigt
        var newVx : Double = 0.0
        iceFloes.forEach {
            val (x1, vx1) = move(it.x, iceFloesVx, ax, dt.seconds)
            it.x = x1
            newVx = vx1
        }
        iceFloesVx = newVx

        //penguin wird in y Richtung mit ay beschleunigt
        val (y, vy) = move(penguin.y, penguin.vy, ay, dt.seconds)
        penguin.y = y
        penguin.vy = vy

        //penguin wird auf exakte(kann je nah frame auch etwas darunter sein) Schollenhöhe, wenn er die Scholle trifft, wenn nicht fällt er hinunter
        iceFloes.forEach {
           if (collidesWith(penguin, it)){
               penguin.y = it.y - penguin.height
           }
        }

        //penguin springt mit vy 150 ab, wenn man die Pfeiltaste nach oben drückt
        if (input.keys[Key.UP]) {
            penguin.vy = -150.0
        }

	}

	//val background = Bild(0, 0, "vorübergehenderBackground.jpg")
	//addChild(background)

}