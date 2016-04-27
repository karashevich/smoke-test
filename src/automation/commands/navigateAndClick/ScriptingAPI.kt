package automation.commands.navigateAndClick

import automation.commands.Command
import automation.commands.Parameters
import com.intellij.util.containers.Queue

/**
 * Created by karashevich on 27/04/16.
 */

fun Queue<Command>. navigateAndClick(params: Parameters){
    val command = NavigateAndClickCommand(params)
    command.process(this)
}

fun withScript(queue: Queue<Command>, block: Queue<Command>.() -> Unit){
    queue.block()
}
