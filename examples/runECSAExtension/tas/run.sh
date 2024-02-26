echo "Running TAS - Tele Assistance System)"
echo "---------------------------------------------------------------"

echo "In this first variant of TAS, we have the regular version of the system."
../../../haiq-analyzer/dist-bin/haiq -model[tas.haiq] -properties[0,1,2] -consts[MAX_TIMEOUTS=1:5:1,TIMEOUT_MULT_FACTOR=2:6:1] -engine[hybrid] -exportFeatures[tas-data]
echo "In the prime  variant of TAS, we have modified the reliabilities and response times of AS3, which have become worse, and of MS5, which have become better."
../../../haiq-analyzer/dist-bin/haiq -model[tas-prime.haiq] -properties[0,1,2] -consts[MAX_TIMEOUTS=1:5:1,TIMEOUT_MULT_FACTOR=2:6:1] -engine[hybrid] -exportFeatures[tas-prime-data]
