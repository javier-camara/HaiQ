# About HaiQ

Formal methods used to validate software designs, like Alloy, OCL, and B, are powerful tools to analyze complex structures (e.g., software architectures, object-relational mappings) captured as sets of relational constraints. 
However, their applicability is limited when software is subject to uncertainty (derived, e.g., from lack of control over third-party components, interaction with physical elements). 
In contrast, quantitative verification has emerged as a powerful way of providing quantitative guarantees about the performance, cost, and reliability of systems operating under uncertainty. 
However, quantitative verification methods do not retain the flexibility of relational modeling in describing structures, forcing engineers to trade structural exploration for analytic capabilities that concern probabilistic and other quantitative guarantees.

HaiQ is a tool that enhances structural modeling/synthesis with quantitative guarantees in the style provided by quantitative verification. It includes:

* A language for describing structure and (stochastic) behavior of systems. 
* A temporal logic called *Manifold Probabilistic Computation Tree Logic* (or M-PCTL) that allows checking probability and reward-based properties over sets of feasible design alternatives implicitly described by the relational constraints in a HaiQ model. 

You can read more about these languages and see some examples in this [paper](http://www.javicamara.com).

# Getting Started

## Prerequisites

- Eclipse development environment.
- JDK 1.8.
- MacOS.

## Installation 

### Building in Eclipse
* Import the contents of the `haiq-analyzer` folder as a project into your Eclipse development environment. 
* Once the project is built, export a runnable jar file called `haiq.jar` into the `dist-bin/lib` directory.
* Edit the startup script `haiq` in the `dist-bin` directory, setting the value of the `HAIQ_DIR` variable to point to your `dist-bin` directory.
* It is also advisable to add the `dist-bin` directory to your path environment variable.

### Pre-built MacOS binaries

Alternatively, you can download and install a pre-built binary version, which can be downloaded from [here](http://www.javicamara.com).
* Unzip the contents of the file in a folder of your choice.
* Edit the startup script `haiq` in the `dist-bin` directory, setting the value of the `HAIQ_DIR` variable to point to your `dist-bin` directory.
* It is also advisable to add the `dist-bin` directory to your path environment variable.

## Running the examples

Every example contains a script to run analysis of different properties. To do that, go into the right example directory, and type

```
./run.sh
```

Alternatively, you can run the analysis directly from the command line. For instance, you can run the following command for the analysis of the simple client-server example

```
haiq -model[clientserver.haiq] -properties[0]
```

The command above run from the client-server example folder will analyze the first M-PCTL property defined in the haiq model file (assuming that the haiq binary folder is in your path environment variable).


# Authors

* **Javier Camara** - *University of York* - [homepage](http://www.javicamara.com)

