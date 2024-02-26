echo "Running TAS - Tele Assistance System)"
echo "---------------------------------------------------------------"

echo "In this dynamic version of TAS, we have modified the reliabilities and response times of AS3, which become worse, and of MS5, which become better over a time horizon of 10 units."
echo ../../../haiq-analyzer/dist-bin/haiq -model[tas.haiq] -properties[0,1,2] -consts[TIMESTAMP=1:10:1,MAX_TIMEOUTS=1:5:1,TIMEOUT_MULT_FACTOR=2:6:1] -engine[hybrid] -exportFeatures[tas-data]

../../../haiq-analyzer/dist-bin/haiq -model[tas.haiq] -properties[0] -consts[TIMESTAMP=1,MAX_TIMEOUTS=1,TIMEOUT_MULT_FACTOR=2] -engine[hybrid] 