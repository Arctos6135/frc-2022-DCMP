# FRC 2022 Robot Code DCMP 
This is the update from https://github.com/Arctos6135/frc-2022, using 2022 GradleRIO plugins, Gradle 7.3.1, and JDK 17. 

Known Issues: 
1. Robot communications is inconsistent due to the following possibilities: packet loss on Driver station, wiring Limelight through second port of radio, or faulty cables. 
2. I2C port on roboRIO connecting the colour sensor has known issues, it may be better to remove it and its corresponding code. 
3. Deploy code through radio instead of roboRIO temporarily when in pits. 
