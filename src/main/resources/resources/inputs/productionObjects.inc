# MIT License
# Copyright (c) 2020 Lukas Kretschmar

Define ObjectType {
    PathSegment
}

Define PolylineModel { PathSegmentModel }

PathSegment JavaClass { ch.hsr.plm.jaamsim.Transportation.PathSegment  } Palette { 'Production' }  DefaultDisplayModel { PathSegmentModel  } IconFile { '<res>/images/Line-24.png' }

PathSegment Description { 'Represents a travel distance an entity can travel with a given speed.'    }