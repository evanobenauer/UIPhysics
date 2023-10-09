# UIPhysics
UIPhysics is an addon library for GlowUI that contains 2 Dimensional physics utilities, formulas, and objects that can be updated and modified based off of torques and forces.

# PhysicsObjectUI
PhysicsObjectUI is an on-screen element that extends ElementUI of GlowUI. This object is a container for any shape (implementing IShape) that will also be able to have an associated mass, charge, velocity, acceleration, netForce, omega, alpha, and netTorque
The object is in the tick thread of GlowUI which controls its position and spin that it contains

# GravityUtil
GravityUtil has the ability to calculate the gravitational force, field, and surface gravity on and from any PhysicsObjectUI. The class also contains constants G and g

# ElectricUtil
GravityUtil has the ability to calculate the electric force, field, and voltage on and from any PhysicsObjectUI. The class also contains constants epsilon0 and k



