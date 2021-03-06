package executors;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import play.libs.concurrent.CustomExecutionContext;

/**
 * Created by agonlohaj on 30 Oct, 2019
 */
public class SingleThreadedExecutionContext extends CustomExecutionContext {

	@Inject
	public SingleThreadedExecutionContext(ActorSystem actorSystem) {
		// uses a custom thread pool defined in application.conf
		super(actorSystem, "single-threaded");
	}
}