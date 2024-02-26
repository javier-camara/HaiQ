Three variants of the network virus case study. Each one of them comprises 200 configurations. Each one with the following variability for parameters:

- Probability of detection of attack from an infected node by a barrier node (barrier_detect=0.5 to 1 in steps of 0.1)
- Probability of detection of attack from an infected node by any other node (node_detect=0.3 to 0.6 in steps of 0.1)
- Probability of infection after unsuccessful attack detection (infect=0.3 to 0.7 in steps of 0.1)

* virus3-data.csv: In this first variant of the virus scenario, we have a 3x3 network without any modifications in the structural constraints of the system.

* virus3-nobarrier-data.csv: In this second variant of the virus scenario, we have a 3x3 network without enforcement of barrier nodes between low and high nodes.

* virus3-sbarrier-data.csv: In this third variant of the virus scenario, we have a 3x3 network that enforces communication between all nodes through a barrier node.