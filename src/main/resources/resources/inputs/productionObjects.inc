# MIT License
# Copyright (c) 2020 Lukas Kretschmar

Define IconModel {
    Node2D
}

Node2D ImageFile { '<res>/images/Node-256.png'  }

Define ColladaModel {
    Vehicle3D
}

Vehicle3D ColladaFile { '<res>/shapes/Vehicle.dae'}

Define PolylineModel { PathSegmentModel }

Define ObjectType {
    Node
    PathSegment
    Vehicle
}

Node        JavaClass { ch.hsr.plm.jaamsim.Transportation.Node          } Palette { 'Production'    } DefaultDisplayModel { Node2D              } IconFile { '<res>/images/Node-24.png'     } DefaultSize { 0.25 0.25 m }
PathSegment JavaClass { ch.hsr.plm.jaamsim.Transportation.PathSegment   } Palette { 'Production'    } DefaultDisplayModel { PathSegmentModel    } IconFile { '<res>/images/Line-24.png'     }
Vehicle     JavaClass { ch.hsr.plm.jaamsim.Transportation.Vehicle       } Palette { 'Production'    } DefaultDisplayModel { Vehicle3D           } IconFile { '<res>/images/Vehicle-24.png'  } DefaultSize { 1.0 0.6 0.4 m }

PathSegment Description { 'Represents a travel distance an entity can travel with a given speed.'    }
Vehicle     Description { 'Vehicle that moves between Nodes.' }