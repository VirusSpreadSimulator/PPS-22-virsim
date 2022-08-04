package it.unibo.pps.entity.structure.entrance

object Entrance:
  type Entity = String // todo: to be removed when integrated with @accursi's entity code

  /** Trait that model the entrance strategy interface */
  trait EntranceStrategy:
    /** It's the core of the strategy, and it decide if the entity can enter or not.
      * @param entity
      *   the entity that wants to enter in the structure
      * @return
      *   true if the entity can enter, false otherwise.
      */
    def canEnter(entity: Entity): Boolean

  /** Base entrance strategy. It corresponds to the free strategy. So everyone could possibly enter. */
  class BaseEntranceStrategy() extends EntranceStrategy:
    override def canEnter(entity: Entity): Boolean = true

  /** Filter based entrance strategy. It decides if allow the entity to enter based on the filter function passed when
    * mixed-in
    */
  trait FilterBasedStrategy(val filter: Entity => Boolean) extends EntranceStrategy:
    abstract override def canEnter(entity: Entity): Boolean = filter(entity) && super.canEnter(entity)

  /** Probability based entrance strategy. It decides if allow the entity to enter based on the probability passed when
    * mixed-in
    */
  trait ProbabilityBasedStrategy(val probability: Double) extends EntranceStrategy:
    import it.unibo.pps.entity.common.ProblableEvents.ProbabilityResult.given
    import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.isHappening
    import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
    abstract override def canEnter(entity: Entity): Boolean = probability.isHappening && super.canEnter(entity)
