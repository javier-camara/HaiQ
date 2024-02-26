echo "Running client-server example"
echo "---------------------------------------------------------------"
echo "Range minimum-maximum probability of satisfying: all requests are correctly issued by clients, and at least one of them is correctly received by a server:"
echo "Identify configuration that maximizes probability that all requests are correctly issued by clients, and at least one of them is correctly received by a server:"
echo ../../haiq-analyzer/dist-bin/haiq -model[./clientserver.haiq] -properties[1] -exportModels -mode[combined] -engine[explicit]
