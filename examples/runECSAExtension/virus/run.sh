echo "Running virus network infection example"
echo "---------------------------------------------------------------"

mc=500
echo "In this first variant of the virus scenario, we have a 3x3 network without any modifications in the structural constraints of the system"
 ../../../haiq-analyzer/dist-bin/haiq -model[virus3.haiq] -properties[0,1,2,3,4] -consts[barrier_detect=0.5:1:0.1,node_detect=0.3:0.6:0.1,infect=0.3:0.7:0.1] -setMaxConfigs[$mc] -mode[iterative] -exportFeatures[virus3-data] -engine[hybrid]
echo "In this second variant of the virus scenario, we have a 3x3 network without enforcement of barrier nodes between low and high nodes"
 ../../../haiq-analyzer/dist-bin/haiq -model[virus3-nobarrier.haiq] -properties[0,1,2,3,4] -consts[barrier_detect=0.5:1:0.1,node_detect=0.3:0.6:0.1,infect=0.3:0.7:0.1] -setMaxConfigs[$mc] -mode[iterative] -exportFeatures[virus3-nobarrier-data] -engine[hybrid]
echo "In this third variant of the virus scenario, we have a 3x3 network that enforces communication between all nodes through a barrier node"
 ../../../haiq-analyzer/dist-bin/haiq -model[virus3-sbarrier.haiq] -properties[0,1,2,3,4] -consts[barrier_detect=0.5:1:0.1,node_detect=0.3:0.6:0.1,infect=0.3:0.7:0.1] -setMaxConfigs[$mc] -mode[iterative] -exportFeatures[virus3-sbarrier-data] -engine[hybrid]


echo "In this first variant of the virus scenario, we have a 4x4 network without any modifications in the structural constraints of the system"
echo ../../../haiq-analyzer/dist-bin/haiq -model[virus4.haiq] -properties[0,1,2,3,4] -consts[barrier_detect=0.5:1:0.1,node_detect=0.3:0.6:0.1,infect=0.3:0.7:0.1] -setMaxConfigs[$mc] -mode[iterative] -exportFeatures[virus4-data] -engine[hybrid]
echo "In this second variant of the virus scenario, we have a 4x4 network without enforcement of barrier nodes between low and high nodes"
echo  ../../../haiq-analyzer/dist-bin/haiq -model[virus4-nobarrier.haiq] -properties[0,1,2,3,4] -consts[barrier_detect=0.5:1:0.1,node_detect=0.3:0.6:0.1,infect=0.3:0.7:0.1] -setMaxConfigs[$mc] -mode[iterative] -exportFeatures[virus4-nobarrier-data] -engine[hybrid]
echo "In this third variant of the virus scenario, we have a 4x4 network that enforces communication between all nodes through a barrier node"
echo  ../../../haiq-analyzer/dist-bin/haiq -model[virus4-sbarrier.haiq] -properties[0,1,2,3,4] -consts[barrier_detect=0.5:1:0.1,node_detect=0.3:0.6:0.1,infect=0.3:0.7:0.1] -setMaxConfigs[$mc] -mode[iterative] -exportFeatures[virus4-sbarrier-data] -engine[hybrid]

