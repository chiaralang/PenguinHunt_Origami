import com.soywiz.korev.Key
import com.soywiz.korge.view.addUpdater
import com.soywiz.korim.color.Colors
import me.emig.engineEmi.*


val engineConfig = EngineConfig(width = 1600, height = 750)

suspend fun main() = Engine(engineConfig) {

    val input = views.input

	val penguin = Player (100, 100, 20, 50, 0.0, Colors.RED)
	addChild(penguin)

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
        return (player.y + player.height > iceFloe.y &&
                player.y + player.height < iceFloe.y + iceFloe.height &&
                player.x + player.width > iceFloe.x &&
                player.x < iceFloe.x + iceFloe.width)//player.x + player.width < iceFloe.x + iceFloe.width

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


        val yAltPenguin = penguin.y

        //penguin wird in y Richtung mit ay beschleunigt
        val (y, vy) = move(penguin.y, penguin.vy, ay, dt.seconds)
        penguin.y = y
        penguin.vy = vy

        //penguin wird auf Schollenhöhe gesetzt, wenn er auf der Scholle landet,
        //wenn nicht fällt er hinunter + wird vor ihr hergeschoben

        iceFloes.forEach {
           if (collidesWith(penguin, it)){
               if (yAltPenguin + penguin.height <= it.y){
                   penguin.y = it.y - penguin.height
                   penguin.vy = 0.0
               } else {
                   penguin.x = it.x - penguin.width
               }
           }
        }


        //penguin springt mit vy 150 ab, wenn man die Pfeiltaste nach oben drückt
        iceFloes.forEach {
            if (penguin.y == it.y - penguin.height){
                    if (input.keys[Key.UP]) {
                        penguin.vy = -150.0
                    }
            }
        }

	}

	//val background = Bild(0, 0, "vorübergehenderBackground.jpg")
	//addChild(background)
}