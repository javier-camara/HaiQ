echo "Running TAS - Tele Assistance System)"
echo "---------------------------------------------------------------"

BASE_C="MAX_TIMEOUTS=1:5:1,TIMEOUT_MULT_FACTOR=2:6:1"

ASC="AS1_FR=0.3,AS1_RT=11,AS1_C=4.1,AS2_FR=0.4,AS2_RT=9,AS2_C=2.5,AS3_FR=0.08,AS3_RT=3,AS3_C=6.8"
MSC="MS1_FR=0.06,MS1_RT=22,MS1_C=9.8,MS2_FR=0.1,MS2_RT=27,MS2_C=8.9,MS3_FR=0.15,MS3_RT=31,MS3_C=9.3,MS4_FR=0.25,MS4_RT=29,MS4_C=7.3,MS5_FR=0.05,MS5_RT=20,MS5_C=11.9"
DSC="DS1_FR=0.12,DS1_RT=1,DS1_C=1"

TAS_CONSTANTS="${BASE_C},${ASC},${MSC},${DSC}"


ASCP="AS1_FR=0.3,AS1_RT=11,AS1_C=4.1,AS2_FR=0.4,AS2_RT=9,AS2_C=2.5,AS3_FR=0.5,AS3_RT=11,AS3_C=6.8"
MSCP="MS1_FR=0.06,MS1_RT=22,MS1_C=9.8,MS2_FR=0.1,MS2_RT=27,MS2_C=8.9,MS3_FR=0.15,MS3_RT=31,MS3_C=9.3,MS4_FR=0.25,MS4_RT=29,MS4_C=7.3,MS5_FR=0.02,MS5_RT=5,MS5_C=11.9"
DSCP="DS1_FR=0.12,DS1_RT=1,DS1_C=1"

TASP_CONSTANTS="${BASE_C},${ASCP},${MSCP},${DSCP}"


echo ${TAS_CONSTANTS}

echo "In this first variant of TAS, we have the regular version of the system."
../../../haiq-analyzer/dist-bin/haiq -model[tas.haiq] -properties[0,1,2] -consts[${TAS_CONSTANTS}] -engine[hybrid] -exportFeatures[tas-data]
echo "In the prime  variant of TAS, we have modified the reliabilities and response times of AS3, which have become worse, and of MS5, which have become better."
../../../haiq-analyzer/dist-bin/haiq -model[tas.haiq] -properties[0,1,2] -consts[${TASP_CONSTANTS}] -engine[hybrid] -exportFeatures[tas-prime-data]
