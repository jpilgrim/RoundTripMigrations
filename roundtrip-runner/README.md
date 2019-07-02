# Round Trip Runner for the N4JS IDE

This plugin extends the N4JS IDE by a runner that allows to execute N4IDL migrations and visualise their implications in round-trip migration scenarios.

# Project Structure

* **plugins** Eclipse Plugin Projects that are required to use the Round Trip Runner. These bundles depend on the eclipse/n4js project.
* **features** An Eclipse Feature project to include the Round Trip Runner in a launch configuration or product definition
* **n4js** Required N4JS runtime libraries in order to use the round trip runner

# Building 

1. Import all projects in `../migrations` into an N4JS IDE
2. Run a full build of all projects (including N4JS's Projects)
3. Install the dependencies of the `n4js/com.enfore.n4js.n4idl.roundtrip.viewer.parent` project
4. Use the `npx webpack` command in the `n4js/com.enfore.n4js.n4idl.roundtrip.viewer.parent` directory of this repository, to package the viewer source files using webpack. This will create the required files in `plugins/com.enfore.n4js.n4idl.roundtrip/res/js`.

# Update Site Creation

In order to create an update site for publishing of the tools plugin, first perform the steps under *Building*.

To build the update site, use the Eclipse IDE *Build Update Site* feature with the `releng/com.enfore.n4js.n4idl.roundtrip.site` project.
