echo "Running virus network infection example"
echo "---------------------------------------------------------------"
echo "Range min-max probability that high nodes will be eventually infected within 50 time units:"
../../haiq-analyzer/dist-bin/haiq -model[./virus-ec.haiq] -properties[0] -setMaxConfigs[2]
echo "Identify configuration that minimizes probability of having all high nodes infected within 50 time units:"
echo ../../haiq-analyzer/dist-bin/haiq -model[./virus-ec.haiq] -properties[1] -skipModelChecking
