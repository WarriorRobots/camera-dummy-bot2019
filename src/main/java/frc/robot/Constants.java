/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * Contains all constants that are used in the program, grouped into subclasses.
 */
public final class Constants {

	/** Contains PID constants used for autonomous closed-loop control. */
	public static final class AutoDrive {
		// TODO constants from ApproachWall.java into Constants.java
	}


	/**
	 * Contains values and methods related to the camera.
	 * Conversions of degrees and radians unincluded.
	 */
	public static final class Camera {

		/** Values applicable only to Ll2 */
		public static final class Limelight2 {

			/** Horizontal FOV in pixels */
			public static final double PIXELS_H = 320;
			/** Vertical FOV in pixels */
			public static final double PIXELS_V = 240;
			
			// 59.6 degrees
			/** Horizontal FOV in radians */
			public static final double RAD_H = 1.04;
			// 49.7 degrees
			/** Vertical FOV in radians */
			public static final double RAD_V = 0.867;

			/** Pixels per radian horizontally; {@value} pixels/rad */
			public static final double PPR_H = PIXELS_H/RAD_H;
			/** Pixels per radian vertically; {@value} pixels/rad */
			public static final double PPR_V = PIXELS_V/RAD_V;

		}

		/** Values applcable only to Ll1 */
		public static final class Limelight1 {

			/** Horizontal FOV in pixels */
			public static final double PIXELS_H = 320;
			/** Vertical FOV in pixels */
			public static final double PIXELS_V = 240;
			
			// 54 degrees
			/** Horizontal FOV in radians */
			public static final double RAD_H = 0.942;
			// 41 degrees
			/** Vertical FOV in radians */
			public static final double RAD_V = 0.716;

			/** Pixels per radian horizontally; {@value} pixels/rad */
			public static final double PPR_H = PIXELS_H/RAD_H;
			/** Pixels per radian vertically; {@value} pixels/rad */
			public static final double PPR_V = PIXELS_V/RAD_V;

		}



		/*
		Height	= 0.5in + arbitrary side

		(5.5in)^2			= arbitrary side ^2 + (1.35in)^2	// 1.35 is from field sketch
		arbitrary side ^2	= 30.25 in^2 - 1.8225 in^2
		arbitrary side		= sqrt (28.4275)in

		Height	= 0.5in + sqrt(28.4275)in
		Height	~ 5.83in
		 */
		/** Vision target height in inches */
		public static final double TARGET_HEIGHT = 5.83;
		
	}
}