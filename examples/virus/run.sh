echo "Running virus network infection example"
echo "---------------------------------------------------------------"
echo "Range min-max probability that high nodes will be eventually infected within 50 time units:"
echo ../../haiq-analyzer/dist-bin/haiq -model[./virus.haiq] -properties[0]
echo "Identify configuration that minimizes probability of having all high nodes infected within 50 time units:"
echo ../../haiq-analyzer/dist-bin/haiq -model[./virus.haiq] -properties[1,2] -setMaxConfigs[500]
../../haiq-analyzer/dist-bin/haiq -model[./virus.haiq] -properties[2,4] -consts[barrier_detect=0.5:1:0.1,node_detect=0.3:0.6:0.3,infect=0.5] -setMaxConfigs[50] -mode[iterative]

