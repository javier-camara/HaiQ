layout: page
title: "About HaiQ"
permalink: /about/
# About HaiQ

Formal methods used to validate software designs, like Alloy, OCL, and B, are powerful tools to analyze complex structures (e.g., software architectures, object-relational mappings) captured as sets of relational constraints. 
However, their applicability is limited when software is subject to uncertainty (derived, e.g., from lack of control over third-party components, interaction with physical elements). 
In contrast, quantitative verification has emerged as a powerful way of providing quantitative guarantees about the performance, cost, and reliability of systems operating under uncertainty. 
However, quantitative verification methods do not retain the flexibility of relational modeling in describing structures, forcing engineers to trade structural exploration for analytic capabilities that concern probabilistic and other quantitative guarantees.
HaiQ is a tool that enhances structural modeling/synthesis with quantitative guarantees in the style provided by quantitative verification. It includes:

* A language for describing structure and (stochastic) behavior of systems, and 
* A temporal logic called *Manifold Probabilistic Computation Tree Logic* (or M-PCTL) that allows checking probability and reward-based properties over sets of feasible design alternatives implicitly described by the relational constraints in a HaiQ model. 
