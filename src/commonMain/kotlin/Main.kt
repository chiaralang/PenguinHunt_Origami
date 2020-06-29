import com.soywiz.korev.Key
import com.soywiz.korge.view.addUpdater
import com.soywiz.korim.color.Colors
import me.emig.engineEmi.*


val engineConfig = EngineConfig(width = 1600, height = 750)

suspend fun main() = Engine(engineConfig) {

    val input = views.input

	val penguin = Player (100, 100, 20, 50, 0.0, Colors.RED)
	addChild(penguin)

    val iceFloes = mutableListOf<IceFloe>(  IceFloe(0.0, 600.0, 750),
                                            IceFloe(850.0, 600.0, 300),
                                            IceFloe(1300.0, 600.0, 450))

    iceFloes.forEach{ addChild(it) }

    var iceFloesVx = 5.0

	val ay = 100.0 //Beschleunigung in y-Richtung -> ≙ Erdanziehung g
	val ax = -10.0 //Beschleunigung in x-Richtung -> Schwierigkeitsgrad erhöhen


    fun move(p: Double, v: Double, a: Double, dt: Double) : Pair<Double, Double> {
        val vNeu = v + (a)*dt
        val pNeu = p + 0.5*(v + vNeu) * dt
        return Pair(pNeu, vNeu)
        //val vAlt = v / val vNeu = v + (a)*dt / val pNeu = p + 0.5*(vAlt + vNeu) * dt
    }

    fun collidesWith(player : Player, iceFloe : IceFloe) : Boolean {
        return (player.y + player.height > iceFloe.y &&
                player.y + player.height < iceFloe.y + iceFloe.height &&
                player.x + player.width > iceFloe.x &&
                player.x < iceFloe.x + iceFloe.width)//player.x + player.width < iceFloe.x + iceFloe.width

    }

    fun createIceFloe (){
        val differentSpaces : DoubleArray =  doubleArrayOf(150.0, 200.0, 300.0)
        val differentYs : DoubleArray =  doubleArrayOf(550.0, 600.0)
        val differentWidths : DoubleArray =  doubleArrayOf(100.0, 150.0, 200.0, 300.0)
        if (iceFloes[iceFloes.size-1].x+ iceFloes[iceFloes.size-1].width + differentSpaces.random()
                <= engineConfig.width.toDouble()) {
            iceFloes += IceFloe(engineConfig.width, differentYs.random(), differentWidths.random())
            addChild(iceFloes[iceFloes.size-1])
        }
    }

    fun removeIceFloes(iceFloe: IceFloe){
        if (iceFloe.x + iceFloe.width == 0.0){
            removeChild(iceFloe)
        }
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

        createIceFloe()

        //iceFloes werden entfernt sobald sie den View verlassen
        iceFloes.forEach{
            removeIceFloes(it)
        }

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


        //penguin springt mit vy 150 ab, wenn man die Leertaste drückt
        iceFloes.forEach {
            if (penguin.y == it.y - penguin.height){
                if (input.keys[Key.SPACE]) {
                    penguin.vy = -150.0
                }
            }
        }

	}

	//val background = Bild(0, 0, "vorübergehenderBackground.jpg")
	//addChild(background)

}