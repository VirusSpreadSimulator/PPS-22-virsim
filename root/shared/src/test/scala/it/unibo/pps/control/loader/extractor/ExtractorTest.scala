package it.unibo.pps.control.loader.extractor

import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.control.loader.extractor.EntitiesStats.*
import it.unibo.pps.control.loader.extractor.Extractor.*
import it.unibo.pps.control.loader.extractor.EnvironmentStats.*
import it.unibo.pps.control.loader.extractor.HospitalStats.*

class ExtractorTest extends AnyFunSuite with Matchers:

  private val baseEnv: Environment = Samples.sampleEnv

  test("Extractor should extract the correct number of dead and alive entities ") {
    val aliveExtractor: DataExtractor[Int] = Alive()
    val deathsExtractor: DataExtractor[Int] = Deaths()
    aliveExtractor.extractData(baseEnv) + deathsExtractor.extractData(baseEnv) shouldBe baseEnv.allEntities.size
  }

  test("Extractor should extract the correct hour from starting environment ") {
    val hoursExtractor: DataExtractor[Long] = Hours()
    hoursExtractor.extractData(baseEnv) shouldBe 0
  }

  test("Extractor should extract the correct days from starting environment ") {
    val daysExtractor: DataExtractor[Long] = Days()
    daysExtractor.extractData(baseEnv) shouldBe 0
  }

  test("Extractor should not detect entities inside hospital in starting environment") {
    val hospitalizedExtractor: DataExtractor[Int] = Hospitalized()
    hospitalizedExtractor.extractData(baseEnv) shouldBe 0
  }

  test("Hospital free sets should always be the difference between capacity and hospitalized") {
    val freeSeatsExtractor: DataExtractor[Int] = HospitalFreeSeats()
    val hospitalizedExtractor: DataExtractor[Int] = Hospitalized()
    val hospitalsCapacity: DataExtractor[Int] = HospitalsCapacity()
    freeSeatsExtractor
      .extractData(baseEnv) shouldBe (hospitalsCapacity.extractData(baseEnv) - hospitalizedExtractor.extractData(
      baseEnv
    ))
  }
