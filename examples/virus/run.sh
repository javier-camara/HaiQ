echo "Running virus network infection example"
echo "---------------------------------------------------------------"
echo "Range min-max probability that high nodes will be eventually infected within 50 time units:"
../../haiq-analyzer/dist-bin/haiq -model[./virus.haiq] -properties[0]
echo "Identify configuration that minimizes probability of having all high nodes infected within 50 time units:"
../../haiq-analyzer/dist-bin/haiq -model[./virus.haiq] -properties[1]
