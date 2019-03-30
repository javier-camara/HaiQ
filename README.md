# HaiQ

`HaiQ` is a tool that integrates a language for describing structure and (stochastic) behavior of systems, and a probabilistic temporal logic (M-PCTL) that allows checking probability and reward-based properties over sets of feasible design alternatives implicitly described by the relational constraints in a HaiQ model.

## Getting Started

### Prerequisites

- Eclipse development environment.
- JDK 1.8.
- MacOS.

### Installation 

* Import the contents of the `haiq-analyzer` folder as a project into your Eclipse development environment. 
* Once the project is built, export a runnable jar file called `haiq.jar` into the `dist-bin` directory.
* Edit the startup script `haiq` in the `dist-bin` directory, setting the value of the `HAIQ_DIR` variable to point to your `dist-bin` directory.
* It is also advisable to add the `dist-bin` directory to your path environment variable.


### Running the examples

Every example contains a script to run analysis of different properties. To do that, go into the right example directory, and type

```
./run.sh
```

Alternatively, you can run the analysis directly from the command line. For instance, you can run the following command for the analysis of the simple client-server example

```
haiq -model[clientserver.haiq] -properties[0]
```

The command above run from the client-server example folder will analyze the first M-PCTL property defined in the haiq model file (assuming that the haiq binary folder is in your path environment variable).


## Authors

* **Javier Camara** - *University of York* - [homepage](http://www.javicamara.com)

