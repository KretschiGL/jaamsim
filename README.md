# What's in here?

This is an extension to JaamSim (see below) allowing to create and simulate production lines.
The idea is, that we use simulation as part of the product development process.
This project is part of my master thesis, and is work in progress.


# What is this JaamSim?

JaamSim is a Java based discrete-event simulation environment developed since 2002 and includes: 
- drag-and-drop user interface 
- interactive 3D graphics 
- input and output processing 
- model development tools and editors. 

Examples of our simulation models can be seen at: 
www.youtube.com/c/jaamsim. 

The key feature that makes JaamSim different from commercial off-the-shelf 
simulation software is that it allows a user to develop new palettes of 
high-level objects for a given application. These objects will automatically 
have 3D graphics, be available in the drag-and-drop interface, and have their 
inputs editable through the Input Editor. Users can focus on the logic for their 
objects without having to program a user interface and input/output processing. 

All the coding for new objects is done in standard Java using standard 
development tools such as Eclipse. There is no need for the specialised 
simulation languages, process flow diagrams, or scripting languages used by 
commercial off-the-shelf simulation software. Model logic can be coded directly 
in either a event- or process-oriented style using a few simple classes and 
methods provided by JaamSim.

The present release includes the following palettes:
- Graphic objects: static 3D objects, overlay text, clock, arrow, graph, etc.
- Probability distributions: uniform, triangular, normal, erlang, gamma, weibull, etc.
- Basic objects: generator, sink, server, queue, delay, resource, branch, time series, etc.
- Calculation objects: weighted sum, polynomial, integrator, differentiator, etc.
- Fluid objects: tank, pipe, pump, etc.

The JaamSim executable, user manuals, examples, and technical articles can be downloaded
from:
http://jaamsim.com

# Dependencies and Installation

A copy of all dependencies is shipped in the jar/ folder and are as follows:
- JOGL2 (from jogamp.org)

Run the ant build tool in the topmost directory to produce build products
in build/jars/

# License

We use a dual license approach:

- The source of JaamSim is Licensed under the Apache License, Version 2.0
- Extensions (everything in src/main/java/ch/ or if stated in the file explicitly) is Licensed under the MIT License

Since these two license are compatible with each other, we don't see any problems with that.
We just prefer the MIT License over the Apache License.