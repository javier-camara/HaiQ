// ----------------------------------------------------------------------------
// turtlebot-ros.als - ROS turtlebot reconfiguration
//-----------------------------------------------------------------------------

// Components and connectors - abstract definitions
abstract sig component {pub: set topic, sub: set topic}
abstract sig topic {pub: set component, sub: set component}

fact {all c:component,t:topic | (c in t.sub <=> t in c.sub) and (c in t.pub <=> t in c.pub) }

// Components and connectors - ROS turtlebot

// Components
abstract sig sensing extends component {}
abstract sig localization extends component {}

lone sig kinect extends sensing {}
lone sig lidar extends sensing {}
lone sig camera extends sensing {} // Simplified front-and back to just one type (always go together)

lone sig amcl extends localization {}
lone sig mrpt extends localization {}
lone sig markerLocalization extends localization{}

lone sig laserscanNodelet extends component {}
lone sig markerRecognizer extends component {} // Simplified to just one type (like cameras)
lone sig headlamp extends component {}

// Connectors
lone sig laserScanTopic extends topic{}
lone sig sensorMsgsImageTopic extends topic{}
lone sig markerPoseTopic extends topic{}

// Options
abstract sig option {}

abstract sig speedSetting extends option {}

lone sig halfSpeedSetting extends speedSetting {}
lone sig fullSpeedSetting extends speedSetting {}

// Constraints - ROS turtlebot

pred publishesTo[c:component, t:topic] { t in c.pub }
pred onlyPublishesTo[c:component, t:topic] { publishesTo[c,t] and all t':topic-t | not publishesTo[c,t'] }

pred subscribesTo[c:component, t:topic] { t in c.sub }
pred onlySubscribesTo[c:component, t:topic] { subscribesTo[c,t] and all t':topic-t | not subscribesTo[c,t'] }

pred doesNotSubscribe[c:component] { all t:topic | not subscribesTo[c,t] }
pred doesNotPublish[c:component] { all t:topic | not publishesTo[c,t] }

fact { all t:topic | t in component.pub+component.sub }

// Sensing constraints
fact { all c:lidar | onlyPublishesTo[c,laserScanTopic] } 
fact { all c:kinect | onlyPublishesTo[c,sensorMsgsImageTopic] }
fact { all c:camera | onlyPublishesTo[c,sensorMsgsImageTopic] }
fact { all c:sensing | doesNotSubscribe[c] }


// Aux component constraints
fact { all c:laserscanNodelet | onlyPublishesTo[c,laserScanTopic] }
fact { all c:laserscanNodelet | onlySubscribesTo[c,sensorMsgsImageTopic] }

fact { all c:markerRecognizer | onlySubscribesTo[c,sensorMsgsImageTopic] }
fact { all c:markerRecognizer | onlyPublishesTo[c,markerPoseTopic] }

fact { doesNotSubscribe[headlamp] and doesNotPublish[headlamp] }

// Localization constraints
fact { all c:localization-markerLocalization | onlySubscribesTo[c,laserScanTopic] }
fact { all c:markerLocalization | onlySubscribesTo[c,markerPoseTopic] }
fact { all c:localization | doesNotPublish[c] }

pred config{
  (some camera or some kinect) <=> some sensorMsgsImageTopic 
  (some laserscanNodelet or some lidar) <=> some laserScanTopic
  some markerRecognizer <=> some markerPoseTopic
  some kinect <=> some laserscanNodelet
  some camera <=> some markerLocalization
  some camera <=> some markerRecognizer
  some headlamp => some camera
  one sensing
  one localization
  no speedSetting
}

// Synthesis command
run config for 10