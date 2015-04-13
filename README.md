Requirements
============

Android 1.6 and later.

Installation
============

### Maven users

Add this dependency to your project's POM:

    <dependency>
      <groupId>com.cooladata</groupId>
      <artifactId>cooladata-android</artifactId>
      <version>2.2.0</version>
    </dependency>

### Gradle users

Add this dependency to your project's build file:

    compile "com.cooladata:cooladata-android:2.2.0"

### Manually

You'll need to manually install the following JARs:

Setup
============

### [ProGuard](http://proguard.sourceforge.net/)

If you're planning on using ProGuard, make sure that you exclude the CoolaData bindings. You can do this by adding the following to your `proguard.cfg` file:

    -keep class com.cooladata.android.** { *; }

Add the following to your `AndroidManifest.xml`

    <uses-permission android:name="android.permission.INTERNET" />



Usage
=====

TBD