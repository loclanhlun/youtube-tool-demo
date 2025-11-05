plugins {
	id("nebula.release") version "19.0.10"
}

allprojects {
	group = "com.hbloc"
	version = "0.1.0"
}

subprojects {
	repositories {
		mavenCentral()
	}
}