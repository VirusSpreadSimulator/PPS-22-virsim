<img src="https://github.com/VirusSpreadSimulator/PPS-22-virsim/blob/gh-pages/assets/logo.png?raw=true#gh-dark-mode-only" style="width:80%"></img> \
<img src="https://github.com/VirusSpreadSimulator/PPS-22-virsim/blob/gh-pages/assets/logo_light.png?raw=true#gh-light-mode-only" style="width:80%"></img> \
![workflow-main-badge](https://github.com/VirusSpreadSimulator/PPS-22-Virsim/actions/workflows/build.yml/badge.svg?branch=main)
![scala-version-badge](https://img.shields.io/badge/scala-3.1.1-red)
![sbt-version-badge](https://img.shields.io/badge/sbt-1.6.2-red)
[![ScalaDoc](https://img.shields.io/badge/Scaladoc-link-red)](https://virusspreadsimulator.github.io/PPS-22-virsim/latest/api/) \
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=VirusSpreadSimulator_PPS-22-virsim&metric=coverage)](https://sonarcloud.io/summary/new_code?id=VirusSpreadSimulator_PPS-22-virsim)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=VirusSpreadSimulator_PPS-22-virsim&metric=bugs)](https://sonarcloud.io/summary/new_code?id=VirusSpreadSimulator_PPS-22-virsim)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=VirusSpreadSimulator_PPS-22-virsim&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=VirusSpreadSimulator_PPS-22-virsim)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=VirusSpreadSimulator_PPS-22-virsim&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=VirusSpreadSimulator_PPS-22-virsim) \
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![semantic-release: angular](https://img.shields.io/badge/semantic--release-angular-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

**Virsim** is a Scala-based simulation tool for the spread of a Virus within a population. \
You can create your own simulation configuring everything such as the virus or the map of the environment and then Virsim will simulate the progress of the infections inside it. \
Take a look at our [user guide](https://github.com/VirusSpreadSimulator/PPS-22-virsim/blob/main/doc/report/07-user-guide.md) to write your first simulation configuration.

For more information visit our [website](https://virusspreadsimulator.github.io/PPS-22-virsim).

## Usage
Virsim comes in two different versions:
- Desktop Application
- Web Application

### Desktop Application
Download latest Jar [here](https://github.com/VirusSpreadSimulator/PPS-22-virsim/releases/latest) and then:
```
java -jar virsim.jar
```
You need to provide a valid *Scala* configuration. [Here](https://github.com/VirusSpreadSimulator/PPS-22-virsim/releases/latest/download/configuration.scala) you can find a *Scala* configuration sample.
### Web Application
We have deployed the Web Application in our website so you can try it without install anything. \
You have only to provide a valid *YAML* configuration. [Here](https://github.com/VirusSpreadSimulator/PPS-22-virsim/releases/latest/download/configuration.yml) you can find a *YAML* configuration sample.

**Launch a simulation at this [link](https://virusspreadsimulator.github.io/PPS-22-virsim/simulator/).**

## Authors
Developed for Academic purpose by:
- Andrea Acampora - andrea.acampora@studio.unibo.it
- Andrea Giulianelli - andrea.giulianelli4@studio.unibo.it
- Giacomo Accursi - giacomo.accursi@studio.unibo.it
