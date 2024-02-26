echo "Running TAS - Tele Assistance System)"
echo "---------------------------------------------------------------"
echo ../../haiq-analyzer/dist-bin/haiq -model[tas.haiq] -properties[0,1,2] -consts[MAX_TIMEOUTS=1:4:1,TIMEOUT_MULT_FACTOR=3] -engine[hybrid] -exportScoreboardJSON[tasdata] -exportConfigurationsJSON[./tasconfigs]
../../haiq-analyzer/dist-bin/haiq -model[tas.haiq] -properties[0,1,2] -consts[MAX_TIMEOUTS=1:4:1,TIMEOUT_MULT_FACTOR=2:4:1] -engine[hybrid] 

