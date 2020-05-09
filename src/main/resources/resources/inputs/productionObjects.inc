# MIT License
# Copyright (c) 2020 Lukas Kretschmar

Define IconModel {
    LogicController2D
    Node2D
    NodeSequenceLogicController2D
}

LogicController2D               ImageFile { '<res>/images/LogicController-256.png'              }
Node2D                          ImageFile { '<res>/images/Node-256.png'                         }
NodeSequenceLogicController2D   ImageFile { '<res>/images/NodeSequenceLogicController-256.png'  }

Define ColladaModel {
    Vehicle3D
}

Vehicle3D ColladaFile { '<res>/shapes/Vehicle.dae'}

Define PolylineModel { PathSegmentModel }

Define ObjectType {
    LogicController
    Node
    NodeSequenceLogicController
    PathSegment
    Vehicle
}

LogicController             JavaClass { ch.hsr.plm.jaamsim.Transportation.LogicController               } Palette { 'Production'    } DefaultDisplayModel { LogicController2D               } IconFile { '<res>/images/LogicController-24.png'              }
Node                        JavaClass { ch.hsr.plm.jaamsim.Transportation.Node                          } Palette { 'Production'    } DefaultDisplayModel { Node2D                          } IconFile { '<res>/images/Node-24.png'                         } DefaultSize { 0.25 0.25 m }
NodeSequenceLogicController JavaClass { ch.hsr.plm.jaamsim.Transportation.NodeSequenceLogicController   } Palette { 'Production'    } DefaultDisplayModel { NodeSequenceLogicController2D   } IconFile { '<res>/images/NodeSequenceLogicController-24.png'  }
PathSegment                 JavaClass { ch.hsr.plm.jaamsim.Transportation.PathSegment                   } Palette { 'Production'    } DefaultDisplayModel { PathSegmentModel                } IconFile { '<res>/images/Line-24.png'                         }
Vehicle                     JavaClass { ch.hsr.plm.jaamsim.Transportation.Vehicle                       } Palette { 'Production'    } DefaultDisplayModel { Vehicle3D                       } IconFile { '<res>/images/Vehicle-24.png'                      } DefaultSize { 1.0 0.6 0.4 m }

LogicController             Description { 'Simple controller to handle requests and dispatch vehicles.' }
Node                        Description { 'A node a vehicle can move to.'   }
NodeSequenceLogicController Description { 'Controller that sends vehicles on a fixed tour.' }
PathSegment                 Description { 'Represents a travel distance an entity can travel with a given speed.'    }
Vehicle                     Description { 'Vehicle that moves between Nodes.' }