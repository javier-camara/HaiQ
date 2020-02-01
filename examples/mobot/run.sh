echo "---------------------------------------------------------------"
../../haiq-analyzer/dist-bin/haiq -model[./mobot60b.haiq] -properties[0,1,2] -exportModels  -setMaxConfigs[100] -exportPolicies[./adv/] -showScoreboard -exportScoreboardJSON[./scores.json]
