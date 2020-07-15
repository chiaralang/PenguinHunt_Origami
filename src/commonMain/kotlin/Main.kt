import EngineEmi.graphics.bild.Bild
import com.soywiz.korev.Key
import com.soywiz.korge.internal.KorgeInternal
import com.soywiz.korge.view.addUpdater
import com.soywiz.korim.color.Colors
import me.emig.engineEmi.*

val engineConfig = EngineConfig(width = 1600, height = 750)

@KorgeInternal
suspend fun main() = Engine(engineConfig) {

    val input = views.input

    
    val penguin = Player (100, 550, 20, 50, 0.0, Colors.RED)
    addChild(penguin)
    val iceFloes = mutableListOf(  IceFloe(0.0, 600.0, 750),
                                            IceFloe(850.0, 600.0, 300),
                                            IceFloe(1300.0, 600.0, 450))
    iceFloes.forEach{ addChild(it) }

    var iceFloesVx = 0.0
    val ax = -7.0


    fun move(p: Double, v: Double, a: Double, dt: Double) : Pair<Double, Double> {
        val vNeu = v + (a)*dt
        val pNeu = p + 0.5*(v + vNeu) * dt
        return Pair(pNeu, vNeu)
        //val vAlt = v / val vNeu = v + (a)*dt / val pNeu = p + 0.5*(vAlt + vNeu) * dt
    }
    fun collidesWith(player : Player, iceFloe : IceFloe) : Boolean {
        return (player.y + player.height > iceFloe.y &&
                player.y < iceFloe.y + iceFloe.height &&
                player.x + player.width > iceFloe.x &&
                player.x < iceFloe.x + iceFloe.width)
    }
    fun createIceFloe () {
        val differentSpaces : DoubleArray =  doubleArrayOf(150.0, 200.0, 300.0)
        val differentYs : DoubleArray =  doubleArrayOf(550.0, 600.0)
        val differentWidths : DoubleArray =  doubleArrayOf(200.0, 250.0, 300.0)
        if (iceFloes[iceFloes.size-1].x+ iceFloes[iceFloes.size-1].width + differentSpaces.random()
                <= engineConfig.width.toDouble()) {
            iceFloes += IceFloe(engineConfig.width, differentYs.random(), differentWidths.random())
            addChild(iceFloes[iceFloes.size-1])
        }
    }
    fun removeIceFloes(iceFloe: IceFloe) {
        if (iceFloe.x + iceFloe.width == 0.0){
            removeChild(iceFloe)
        }
    }
    fun gameOverAnimation () {}

    addUpdater { dt ->

        fun logo () {}
        fun menu () {
            if (input.keys[Key.ENTER]) {
                iceFloesVx = -3.0
            }

        }
        fun game (){

            //iceFloes movement (x-direction with speed ax)
            var newVx = 0.0
            iceFloes.forEach {
                val (x1, vx1) = move(it.x, iceFloesVx, ax, dt.seconds)
                it.x = x1
                newVx = vx1
            }
            iceFloesVx = newVx

            createIceFloe()

            //iceFloes removal
            iceFloes.forEach{
                removeIceFloes(it)
            }

            val yAltPenguin = penguin.y

            var ay = 155.0 //Speedup in y-direction -> ≙ Gravitation g of the player
            ay += 0.5

            //penguin movement (in y-direction with speed ay)
            val (y, vy) = move(penguin.y, penguin.vy, ay, dt.seconds)
            penguin.y = y
            penguin.vy = vy

            //penguin y-position corrected if landing on iceFloe, if not fall

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

            //penguin jump with speed vy (on space)
            iceFloes.forEach {
                if (penguin.y == it.y - penguin.height){
                    if (input.keys[Key.SPACE]) {
                        penguin.vy = -150.0
                    }
                }
            }

        }

        val penguinY = penguin.y

        logo()

        if (iceFloesVx == 0.0){
            menu()
        }

        if (penguinY < engineConfig.height.toDouble() && iceFloesVx != 0.0){
            game ()
        } else {
            iceFloesVx = 0.0
            gameOverAnimation()
        }

    }
}