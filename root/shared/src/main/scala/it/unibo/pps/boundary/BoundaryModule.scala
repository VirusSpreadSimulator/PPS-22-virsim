package it.unibo.pps.boundary
import monix.eval.Task
import monix.reactive.Observable
import component.Events.Event
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.entity.environment.EnvironmentModule.Environment

object BoundaryModule:
  /** Interface that define the base boundary. All the boundary need to extend this trait in order to be compatible. All
    * the data will be send to the boundaries in the same way (ECB pattern)
    */
  trait Boundary:
    /** Initialise the boundary.
      * @return
      *   the task
      */
    def init(): Task[Unit]
    /** Start the simulation
      * @return
      *   the task
      */
    def start(): Task[Unit]
    /** Consume the current state of the simulation.
      * @param env
      *   the current environment
      * @return
      *   the task
      */
    def consume(env: Environment): Task[Unit]
    /** Every boundary can produce events and the control part of the architecture may be interested in.
      * @return
      *   the observable that emit events for the boundary
      */
    def events(): Observable[Event]
    /** Simulation terminated
      * @return
      *   the task
      */
    def stop(): Task[Unit]

  /** ConfigBoundary is a special type of boundary dedicated to the start of the visual part of the simulator. This
    * allow to upload the configuration and start the simulation. So it's a special boundary, that is different respect
    * to the other ones.
    */
  trait ConfigBoundary extends Boundary:
    /** This task allow the caller to obtain the path of the configuration file for the simulation.
      * @return
      *   the task
      */
    def config(): Task[FilePath]
    /** This task allow the caller to report an error in the configuration to the boundary
      * @param error
      *   the error in the configuration
      * @return
      *   the task
      */
    def error(error: Seq[ConfigurationError]): Task[Unit]
  /** Provider of the component */
  trait Provider:
    /** All the boundaries that are inside the system. */
    val boundaries: Seq[Boundary]
    /** The configuration boundary */
    val configBoundary: ConfigBoundary
  trait Interface extends Provider
